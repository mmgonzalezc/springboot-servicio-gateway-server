package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/***
 * @EnableWebFluxSecurity -> Anotacion para habilitar la seguridad en web flux
 */
@EnableWebFluxSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter authenticationFilter;

	/***
	 * Implementamos metodo de configuracion de endpoints
	 * @param http
	 * @return
	 */
	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
		return http.authorizeExchange()
				.pathMatchers("/api/security/oauth/**").permitAll() // acceso publico
				.pathMatchers(HttpMethod.GET, "/api/productos/listar",
						"/api/items/listar",
						"/api/usuarios/usuarios",
						"/api/items/ver/{id}/cantidad/{cantidad}",
						"/api/productos/ver/{id}").permitAll() //acceso publico
				.pathMatchers(HttpMethod.GET, "/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
				.pathMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/usuarios/**").hasRole("ADMIN")
				.anyExchange().authenticated() // Autorizacion de cualquier ruta
				.and().addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) // registramos el flitro y pasamos el orden
				.csrf().disable() // Se desabilita seguridad  csrf ya que es para vista formulario utilizando html, tymelif, jsp
				.build();
	}
	
}
