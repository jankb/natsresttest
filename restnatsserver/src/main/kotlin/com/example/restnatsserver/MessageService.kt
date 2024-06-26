package com.example.restnatsserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.nats.client.JetStream
import io.nats.client.JetStreamSubscription
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    // private val natsConnection: Connection,
    private val conn: NatsConnectionManager,
) {
    private lateinit var jetStream: JetStream
    private lateinit var subscription: JetStreamSubscription

    @PostConstruct
    fun init() {
        val sub1 = conn.subscribe("provetaking.order")
        val sub2 = conn.subscribe("provetaking.respons")
    }

    fun saveMessage(messageContent: String): Message {
        val message = Message(message = messageContent)
        return messageRepository.save(message)
    }

    fun publishMessage(message: Message) {
        val messageJson = jacksonObjectMapper().writeValueAsString(message)
        val subject: String = "provetaking.order." + message.id
        jetStream.publish(subject, messageJson.toByteArray())
    }

    fun getMessage(id: Long): Message? = messageRepository.findById(id).orElse(null)
}
