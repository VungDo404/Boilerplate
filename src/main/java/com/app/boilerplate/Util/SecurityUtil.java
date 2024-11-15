package com.app.boilerplate.Util;

import com.app.boilerplate.Shared.Authentication.AccessJwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtil {
	public AccessJwt getAccessJwt() {
		final var context = SecurityContextHolder.getContext();
		final var authentication = (Authentication) context.getAuthentication();
		final var principal = authentication.getPrincipal();
		return (AccessJwt) principal;
	}
	public UUID getUserId() {
		return getAccessJwt().getSub();
	}
}