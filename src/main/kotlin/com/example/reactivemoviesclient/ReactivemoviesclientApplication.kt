package com.example.reactivemoviesclient

import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class ReactivemoviesclientApplication

fun main(args: Array<String>) {

    //function bean definitions with DSL
    SpringApplicationBuilder()
            .sources(ReactivemoviesclientApplication::class.java)
            .initializers(
                    beans {

                        bean {
                            WebClient.builder()
                                    .baseUrl("http://localhost:8081/movies")
                                    .build()
                        }

                        bean {
                            router {

                                val client = ref<WebClient>()

                                GET("/titles") {
                                    val names: Publisher<String> = client
                                            .get()
                                            .retrieve()
//                                            .bodyToFlux(Movie::class.java)
                                            .bodyToFlux<Movie>()
//                                            .map { movie -> movie.title}
                                            .map { it.title }

                                    ServerResponse.ok().body(names)
//                                    ServerResponse.ok().body(names, String::class.java)   // Kotlin knows it
                                }
                            }
                        }

                        //Spring Gateway
                        bean {
                            val builder = ref<RouteLocatorBuilder>()
                            builder.routes {

                                route {
                                    path("/proxy")
                                    //works with Eureka
                                    uri("http://localhost:8081/movies")
                                }

                                // 1:01:00
//                                route {
//                                    val rl = ref<RequestRateLimiterGatewayFilterFactory>()
//                                    val redisRl = RedisRateLimiter(5,10)
//                                    val filter = rl.apply(redisRl.config)
//
//                                    path("/rl")
//                                    filters {
//                                        filter(rl)
//                                    }
//                                    uri("http://localhost:8081/movies")
//                                }

                            }
                        }
                    }
            )
            .run(*args);
}

class Movie(val id: String? = null, val title: String? = null)