package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter{

	@Autowired
	private ReactiveAuthenticationManager authenticationManager;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		/***
		 * Convierte en un flujo reactivo
		 * Operador .map -> devuelve al flujo un objeto comun y corriente ejemplo string
		 * .flatMap -> devuelve al flujo otro flujo
		 */
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				.filter(authHeader -> authHeader.startsWith("Bearer ")) // preguntamos si esta cabezera comienza con Bearer
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty())) // Si es vacio continuamos con un flujo vacio
				.map(token -> token.replace("Bearer ", "")) // transfmamos con map quitamos el Bearer ya que no lo necesitamos para validar el token
				.flatMap(token -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(null, token))) // Devuelve fujo tipo mono de programacion reactiva
				.flatMap(authentication -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))); // Devuelve flujo tipo mono de programacion reactiva
	}

}
