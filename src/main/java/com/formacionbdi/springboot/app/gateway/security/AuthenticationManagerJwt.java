package com.formacionbdi.springboot.app.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManagerJwt implements ReactiveAuthenticationManager{

	@Value("${config.security.oauth.jwt.key}")
	private String llaveJwt;
	
	@Override
	@SuppressWarnings("unchecked")
	public Mono<Authentication> authenticate(Authentication authentication) {
		// metodo just convierte a un objeto reactivo
		return Mono.just(authentication.getCredentials().toString())
				/***
				 * Mediante el token que se emite en el map como un string, se convierte se devuelve a un tipo claims
				 */
				.map(token -> {
					// Codificacmos llave en base 64 para que sea mas robusta
					SecretKey llave = Keys.hmacShaKeyFor(Base64.getEncoder().encode(llaveJwt.getBytes()));
					// Retornamos el token
					return Jwts.parserBuilder().setSigningKey(llave).build().parseClaimsJws(token).getBody();
				})
				.map(claims -> {
					// Campos que bienen en el token
					String username = claims.get("user_name", String.class);
					List<String> roles = claims.get("authorities", List.class);

					Collection<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());
					return new UsernamePasswordAuthenticationToken(username, null, authorities);
					
				});
	}

}
