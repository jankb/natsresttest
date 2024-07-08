package com.example.restnatsserver

import io.nats.client.Connection
import io.nats.client.Message
import io.nats.client.MessageHandler
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

    fun timeReply(
        time: String,
        replySubject: String,
    )
}

interface NatsSubscriberIf {
    fun orderRequest(order: String): Boolean

    fun timeRequest(
        myvalue: String,
        replySubject: String,
    )
}

@Component
class NATSSubscriber(
    private val natsConnection: Connection?,
    natsSubscriberIf: NatsSubscriberIf,
) {
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val jetStream = natsConnection?.jetStream()
    private val logger = LoggerFactory.getLogger(NATSSubscriber::class.java)

    init {
        val pullSubscribeOptions =
            PullSubscribeOptions
                .builder()
                .stream("jk-test")
                .durable("consumer-1") // Durable name is necessary for pull subscriptions
                .build()

        try {
            jetStream?.subscribe(null, pullSubscribeOptions)?.let { subscription ->
                scheduler.scheduleAtFixedRate({
                    if (natsConnection?.status == Connection.Status.CONNECTED) {
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
    private val natsConnection: Connection?,
) : NATSPublishIf {
    private val logger = LoggerFactory.getLogger(NATSPublisher::class.java)

    override fun publishOrderReceipt(orderReceipt: String) {
        val subject = "provetaking.respons"
        natsConnection?.let {
            it.publish(subject, orderReceipt.toByteArray())
            logger.info("Publishing message to subject $subject : $orderReceipt")
        }
    }

    override fun publishRequisition(requisition: String) {
        println("natsConnection.publish(\"provetaking.order\", requisition.toByteArray())")
    }

    override fun timeReply(
        time: String,
        replySubject: String,
    ) {
        natsConnection?.let {
            logger.info("Sending reply on subject $replySubject, $time")
            it.publish(replySubject, time.toByteArray())
        }
    }
}

@Component
class NATSRequestReply(
    natsConnection: Connection?,
    private val natsSubscriberIf: NatsSubscriberIf,
) : MessageHandler {
    private val logger = LoggerFactory.getLogger(NATSRequestReply::class.java)

    init {
         natsConnection?.let { connection ->
            connection.createDispatcher(this).let { dispatcher ->
                dispatcher.subscribe("timeRequest")
                logger.info("Subscribed to timeRequest.")
            }
        }
    }

    override fun onMessage(msg: Message?) {
        msg?.let {
            logger.info("Got Request : ${String(msg.data)}")
            natsSubscriberIf.timeRequest(String(msg.data), msg.replyTo)
        }
    }
}
