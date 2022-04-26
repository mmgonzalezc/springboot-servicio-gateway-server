package com.formacionbdi.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
// import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/***
 * Para implemtentar clases con spring cloud gateway, por detras de esena se trabaja cob web flux programacion reactiva
 */

@Component
public class EjemploGlobalFilter implements GlobalFilter, Ordered{

	private final Logger logger = LoggerFactory.getLogger(EjemploGlobalFilter.class);

	/***
	 *
	 * @param exchange para ingresar a request y response
	 * @param chain cadena de filtro
	 * @return
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("ejecutando filtro pre");
		exchange.getRequest().mutate().headers(h -> h.add("token", "123456"));

		// Operador then para ejecutar despues cuando haya finalizado la ejecucion de todos los filtros
		// Mono.fromRunnable nos permite crear un objeto reactivo un mono void
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			logger.info("ejecutando filtro post");
			
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor -> {
				exchange.getResponse().getHeaders().add("token", valor);
			});
			
			exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "rojo").build());
			// exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		}));
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

}
