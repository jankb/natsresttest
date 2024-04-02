package com.example.server

import io.nats.client.Dispatcher
import io.nats.client.JetStream
import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.PullSubscribeOptions
import io.nats.client.SubscribeOptions
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class NatsSubscriber(private val jetStream: JetStream, private val natsConnection: Connection) {

    @PostConstruct
    fun subscribe() {
        val subject = "hello.world" 

        val dispatcher: Dispatcher = natsConnection.createDispatcher { message ->
            val messageData = String(message.data)
            println("Received message: $messageData")
            message.ack()
        }

        dispatcher.subscribe(subject)
    }
}
