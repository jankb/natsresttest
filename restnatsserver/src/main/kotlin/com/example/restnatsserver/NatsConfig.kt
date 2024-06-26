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

    // fun natsConnection(): Connection = Nats.connect("nats://localhost:4222")
    @Bean
    fun natsConnection(): Connection? =
        try {
            Nats.connect(
                Options
                    .builder()
                    .server("nats://localhost:4222")
                    .userInfo("sys", "pass")
                    .build(),
            )
        } catch (e: Exception) {
            logger.error("\"Unable to create NATS connection.", e)
            null
        }
}
