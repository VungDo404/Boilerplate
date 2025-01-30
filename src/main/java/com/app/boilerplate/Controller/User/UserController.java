package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Mapper.IUserMapper;
import com.app.boilerplate.Service.User.UserService;
import com.app.boilerplate.Shared.User.Dto.PostUserDto;
import com.app.boilerplate.Shared.User.Dto.PutUserDto;
import com.app.boilerplate.Shared.User.Dto.UserCriteriaDto;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "User")
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;
	private final IUserMapper userMapper;

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.USER + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/")
	public Page<User> getUsers(Optional<UserCriteriaDto> userCriteriaDto, @ParameterObject Pageable pageable) {
		return userService.getAllUsers(userCriteriaDto, pageable);
	}

	@PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/{id}")
	public User getUserById(@PathVariable @NotNull UUID id) {
		return userService.getUserById(id);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.USER + "', '" + PermissionUtil.CREATE + "')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public User createUser(@RequestBody @Validated(Default.class) PostUserDto request) {
		return userService.createUser(request, request.getShouldSendConfirmationEmail());
	}

	@PreAuthorize("hasPermission(#request.id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.WRITE + "')")
	@ResponseStatus(HttpStatus.CREATED)
	@PutMapping
	public User putUser(@RequestBody @Valid PutUserDto request) {
		return userService.putUser(request);
	}

	@PreAuthorize("hasPermission(#id.toString(), '" + PermissionUtil.USER + "', '" + PermissionUtil.DELETE + "')")
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable @NotNull UUID id) {
		userService.deleteUserById(id);
	}
}
