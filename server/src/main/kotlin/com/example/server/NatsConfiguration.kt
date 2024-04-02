package com.example.server

import io.nats.client.Connection
import io.nats.client.JetStream
import io.nats.client.Nats
import io.nats.client.Options
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConfiguration {

    @Value("\${nats.server.url}")
    private lateinit var natsServerUrl: String

    @Bean
    fun natsConnection(): Connection {
        val options = Options.Builder().server(natsServerUrl).build()
        return Nats.connect(options)
    }

    @Bean
    fun jetStream(natsConnection: Connection): JetStream {
        return natsConnection.jetStream()
    }
}
