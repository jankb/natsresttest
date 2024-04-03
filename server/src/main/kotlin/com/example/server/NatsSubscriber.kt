package com.example.server

import io.nats.client.Dispatcher
import io.nats.client.JetStream
import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.PullSubscribeOptions
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import java.time.Duration

@Service
class NatsSubscriber(private val jetStream: JetStream, private val natsConnection: Connection) {
    @PostConstruct
    fun subscribe() {
        val subject = "hello.world"

     /*   val dispatcher: Dispatcher = natsConnection.createDispatcher { message ->
            val messageData = String(message.data)
            println("Received message: $messageData")
            message.ack()
        }*/

        // Example from
        // https://github.com/nats-io/nats.java/blob/main/src/examples/java/io/nats/examples/jetstream/NatsJsPullSubBatchSize.java

        val options: PullSubscribeOptions = PullSubscribeOptions.builder()
            .build()

        val subscription: JetStreamSubscription = jetStream.subscribe(subject, options)
        natsConnection.flush(Duration.ofSeconds(1))

        subscription.consumerInfo?.let {
            println("Server consumer is named: ${it.name}")
        }

        subscription.pull(10)
        subscription.nextMessage(Duration.ofSeconds(1))?.let {
            println("Received message: ${String(it.data)}")
        }

    //    dispatcher.subscribe(subject)
    }
}
