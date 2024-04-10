package com.example.server

import io.nats.client.Dispatcher
import io.nats.client.JetStream
import io.nats.client.Connection
import io.nats.client.JetStreamSubscription
import io.nats.client.PullSubscribeOptions
import io.nats.client.api.ConsumerConfiguration
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

        val consumerConfig: ConsumerConfiguration = ConsumerConfiguration.builder()
            .ackWait(Duration.ofMillis(150))
            .durable("voff2-durable")
            .build()

        val options: PullSubscribeOptions = PullSubscribeOptions.builder()
            .configuration(consumerConfig)
            .build()

        val subscription: JetStreamSubscription = jetStream.subscribe(subject, options)
        natsConnection.flush(Duration.ofSeconds(1))

        subscription.consumerInfo?.let {
            println("Server consumer is named: ${it.name}")
            println("Consumerinfo: ${it}")
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutting down gracefully...")
            natsConnection.close()
        })

        try {
            while(!Thread.currentThread().isInterrupted) {
                subscription.pull(10)
                //var message = subscription.nextMessage(Duration.ofSeconds(60))
                var message = subscription.nextMessage(100)
                while (message != null) {
                    if (message.isJetStream) {
                        println("Received message: ${String(message.data)}")
                        message.ack()
                    }
                    message = subscription.nextMessage(100)
                }
            }
        } catch (e: InterruptedException) {
            println("Interrupted, shutting down...")
            Thread.currentThread().interrupt() // Preserve interrupt status
        } catch (e: Exception) {
            println("Error fetching messages: ${e.message}")
            // Handle other exceptions
        } finally {

            natsConnection.close()
        }

    //    dispatcher.subscribe(subject)
    }
}
