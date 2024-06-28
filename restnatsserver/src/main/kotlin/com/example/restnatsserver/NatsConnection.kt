package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.PullSubscribeOptions
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

interface NATSPublishIf {
    fun publishRequisition(requisition: String)

    fun publishOrderReceipt(orderReceipt: String)
}

interface NatsSubscriberIf {
    fun orderRequest(order: String): Boolean
}

@Component
class NATSSubscriber(
    private val natsConnection: Connection,
    natsSubscriberIf: NatsSubscriberIf,
) {
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val jetStream = natsConnection.jetStream()
    private val logger = LoggerFactory.getLogger(NATSSubscriber::class.java)

    init {
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
                        messages?.forEach { message ->
                            message
                                ?.takeIf {
                                    it.isJetStream
                                }?.let {
                                    if (it.subject == "provetaking.order") {
                                        if (natsSubscriberIf.orderRequest(String(message.data))) {
                                            it.ack()
                                        }
                                    }
                                }
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS)
            }
        } catch (e: Exception) {
            logger.error("Unable to subscribe to stream. ${e.message}")
        }
    }

    @PreDestroy
    fun cleanUp() {
        scheduler.shutdownNow()
    }
}

@Component
class NATSPublisher(
    private val natsConnection: Connection,
) : NATSPublishIf {
    private val logger = LoggerFactory.getLogger(NATSPublisher::class.java)

    override fun publishOrderReceipt(orderReceipt: String) {
        val subject = "provetaking.respons"
        logger.info("Publishing message to subject $subject : $orderReceipt")
        natsConnection.publish(subject, orderReceipt.toByteArray())
    }

    override fun publishRequisition(requisition: String) {
        println("natsConnection.publish(\"provetaking.order\", requisition.toByteArray())")
    }
}
