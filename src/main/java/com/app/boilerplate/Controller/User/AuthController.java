package com.app.boilerplate.Controller.User;

import com.app.boilerplate.Service.Auth.AuthService;
import com.app.boilerplate.Shared.Authentication.AccessJwt;
import com.app.boilerplate.Shared.Authentication.Dto.LoginDto;
import com.app.boilerplate.Shared.Authentication.Model.LoginResultModel;
import com.app.boilerplate.Shared.Authentication.Model.RefreshAccessTokenModel;
import com.app.boilerplate.Util.AppConsts;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
	private final AuthService authService;

	@PostMapping("/authenticate")
	public LoginResultModel authenticate(@RequestBody @Valid LoginDto request, HttpServletResponse response) {
		return authService.authenticate(request, response);
	}

	@PostMapping("/refresh-token")
	public RefreshAccessTokenModel refreshToken(@CookieValue(AppConsts.REFRESH_TOKEN) String refreshToken,
												HttpServletResponse response) {
		return authService.refreshAccessToken(refreshToken, response);
	}

	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal AccessJwt jwt, HttpServletResponse response,
					   @CookieValue(AppConsts.REFRESH_TOKEN) String refreshToken) {
		authService.logout(jwt, response, refreshToken);
	}
}
