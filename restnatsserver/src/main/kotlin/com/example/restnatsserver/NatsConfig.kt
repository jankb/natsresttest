package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConfig {

    @Bean
    fun natsConnection(): Connection = Nats.connect("nats://localhost:4222")
}
