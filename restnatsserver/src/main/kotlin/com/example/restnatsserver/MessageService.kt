package com.example.restnatsserver
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.nats.client.Connection
import io.nats.client.JetStream
import io.nats.client.JetStreamSubscription
import io.nats.client.PullSubscribeOptions
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class MessageService(private val messageRepository: MessageRepository, private val natsConnection: Connection) {

    private lateinit var jetStream: JetStream
    private lateinit var subscription: JetStreamSubscription

    @PostConstruct
    fun init() {
        jetStream = natsConnection.jetStream()

        val pullSubscribeOptions = PullSubscribeOptions.builder()
            .durable("message-group") // Durable name is necessary for pull subscriptions
            .build()

        subscription = jetStream.subscribe("hello.world", pullSubscribeOptions)


        Thread {
            while (true) {
                val messages = subscription.fetch(10, 1000) // Adjust based on your throughput needs
                for (message in messages) {
                    val msg = String(message.data)
                    val savedMessage = saveMessage(msg)
                 //   publishMessage(savedMessage)
                    message.ack()
                }
            }
        }.start()
    }

    fun saveMessage(messageContent: String): Message {
        val message = Message(message = messageContent)
        return messageRepository.save(message)
    }

    fun publishMessage(message: Message) {
        val messageJson = jacksonObjectMapper().writeValueAsString(message)
        jetStream.publish("hello.world", messageJson.toByteArray())
    }

    fun getMessage(id: Long): Message? = messageRepository.findById(id).orElse(null)
}