package com.rinca.erisserver.config;

import com.rinca.erisserver.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;

		if (authHeader != null && authHeader.startsWith("Bearer")) {
			token = authHeader.substring(7);
		}

        if (token != null && jwtService.isTokenValid(token)) {
            Long username = jwtService.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				Collection<GrantedAuthority> authorities = jwtService.extractRoles(token);

				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				context.setAuthentication(authenticationToken);
				SecurityContextHolder.setContext(context);
			}
		}
        filterChain.doFilter(request,response);
    }
}
