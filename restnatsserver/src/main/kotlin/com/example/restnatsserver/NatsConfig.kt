package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.Nats
import io.nats.client.Options
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConfig {
    private val logger = LoggerFactory.getLogger(NatsConfig::class.java)

    private val url = "nats://localhost:4222"
    // fun natsConnection(): Connection = Nats.connect("nats://localhost:4222")
    @Bean
    fun natsConnection(): Connection? =
        try {
            Nats.connect(
                Options
                    .builder()
                    .server(url)
                    .userInfo("sys", "pass")
                    .build(),
            )
        } catch (e: Exception) {
            logger.warn("Unable to create NATS connection to $url.")
            null
        }
}
