package com.app.boilerplate.Service.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Exception.EmailAlreadyUsedException;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Repository.UserRepository;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import com.app.boilerplate.Util.RandomUtil;
import com.app.boilerplate.Util.Translator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService implements Translator {
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;
	private final RandomUtil randomUtil;
	private final IUserMapper userMapper;

	@Transactional(readOnly = true)
	public Page<User> getAllUsers(Optional<UserCriteriaDto> userCriteriaDto, Pageable pageable) {
		final var specification = UserSpecification.specification(userCriteriaDto);
		return userRepository.findAll(specification, pageable);
	}

	@Transactional(readOnly = true)
	public User getUserById(UUID id) {
		return Optional.of(id)
			.flatMap(userRepository::findById)
			.orElseThrow(() -> new NotFoundException(
				translateEnglish("error.user.id.notfound", id),
				"error.user.id.notfound", id));
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		return Optional.of(username)
			.flatMap(userRepository::findOneByUsername)
			.orElseThrow(() -> new UsernameNotFoundException(
				translateEnglish("error.user.login.notfound", username)));
	}

	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return Optional.of(email)
			.flatMap(userRepository::findOneByEmailIgnoreCase)
			.orElseThrow(
				() -> new NotFoundException(
					translateEnglish("error.user.email.notfound", email),
					"error.user.email.notfound", email));
	}

	public User createUser(User request) {
		Optional.of(request.getEmail())
			.filter(userRepository::existsByEmailIgnoreCase)
			.ifPresent(email -> {
				throw new EmailAlreadyUsedException(email);
			});
		return Optional.of(request)
			.map(req -> {
				req.setPassword(Optional.ofNullable(req.getPassword())
					.orElseGet(randomUtil::randomPassword));
				req.setSecurityStamp(UUID.randomUUID()
					.toString());
				return req;
			})
			.map(this::save)
			.orElseThrow();
	}

	public User putUser(PutUserDto request) {
		return Optional.of(request.getId())
			.map(this::getUserById)
			.map(user -> {
				userMapper.update(user, request);
				user.setSecurityStamp(UUID.randomUUID()
					.toString());
				save(user);
				log.debug("Updated Information for User: {}", user);
				return user;
			})
			.orElseThrow(() -> new NotFoundException(
				translateEnglish("error.user.email.notfound", request.getId()),
				"error.user.id.notfound", request.getId()));
	}

	public void deleteUserById(UUID id) {
		userRepository.deleteById(id);
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public boolean validateSecurityStampInDatabase(UUID id, String securityStamp) {
		return userRepository.existsByIdAndSecurityStamp(id, securityStamp);
	}

}
