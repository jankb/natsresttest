package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.PullSubscribeOptions
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
class NatsConnectionManager(
    private val natsConnection: Connection,
) {
    private val logger = LoggerFactory.getLogger(NatsConfig::class.java)
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private val jetStream = natsConnection.jetStream()

    fun subscribe(
        subject: String,
        // parser: (s: String) -> Unit,
    ) {
        val pullSubscribeOptions =
            PullSubscribeOptions
                .builder()
                .stream("bjeff")
                .durable("consumer-1") // Durable name is necessary for pull subscriptions
                .build()

        try {
            jetStream.subscribe(null, pullSubscribeOptions)?.let { subscription ->
                scheduler.scheduleAtFixedRate({
                    if (natsConnection.status == Connection.Status.CONNECTED) {
                        val messages = subscription.fetch(10, 1000)
                        println("Messages ${messages.size}")
                        messages?.forEach { message ->
                            message?.takeIf { it.isJetStream && it.subject == subject }?.let {
                                //  println("$subject Value " + parser!!::class.simpleName)
                                //  parser(String(message.data))
                                println("Subject ${it.subject} " + String(message.data))
                                it.ack()
                            }
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS)
            }
        } catch (e: Exception) {
            logger.error("Unable to subscribe to $subject. ${e.message}")
        }
    }

    @PreDestroy
    fun cleanUp() {
        scheduler.shutdownNow()
    }

    fun doStuff(function: (s: String) -> Unit) {
        val name = "Name in function"
        function(name)
    }
}
