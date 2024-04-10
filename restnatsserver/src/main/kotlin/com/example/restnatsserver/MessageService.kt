package com.example.restnatsserver
import io.nats.client.Connection
import io.nats.client.JetStream
import io.nats.client.JetStreamSubscription
import io.nats.client.api.PublishAck
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class MessageService(private val messageRepository: MessageRepository, private val natsConnection: Connection) {

    private lateinit var jetStream: JetStream
    private lateinit var subscription: JetStreamSubscription

    @PostConstruct
    fun init() {
        jetStream = natsConnection.jetStream()

        subscription = jetStream.pullSubscribe("message.in", "message-group")
        Thread {
            while (true) {
                val messages = subscription.fetch(10) // Adjust based on your throughput needs
                for (message in messages) {
                    val msg = String(message.data)
                    val savedMessage = saveMessage(msg)
                    publishMessage(savedMessage)
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
        val messageJson = // Convert message to JSON
            jetStream.publish("message.out", messageJson.toByteArray())
    }

    fun getMessage(id: Long): Message? = messageRepository.findById(id).orElse(null)
}