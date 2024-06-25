package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.PullSubscribeOptions
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
class NatsConnectionManager(private val natsConnection: Connection) {
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private val jetStream = natsConnection.jetStream()

    private val pullSubscribeOptions =
        PullSubscribeOptions.builder()
            .durable("message-group-1") // Durable name is necessary for pull subscriptions
            .build()

    fun <T> subscribe(
        subject: String,
        value: T,
    ) {
        try {
            jetStream.subscribe(subject, pullSubscribeOptions)?.let { subscription ->
                scheduler.scheduleAtFixedRate({
                    if (natsConnection.status == Connection.Status.CONNECTED) {
                        val messages = subscription?.fetch(10, 1000)
                        messages?.forEach { message ->
                            message?.takeIf { it.isJetStream && it.subject == subject }?.let {
                                println("$subject Value " + value!!::class.simpleName)
                                println(String(message.data))
                                it.ack()
                            }
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS)
            }
        } catch (e: Exception) {
            println("Unable to subscribe to $subject. ${e.message}")
        }
    }
}
