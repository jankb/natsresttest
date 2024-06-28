package com.example.restnatsserver

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val publisher: NATSPublishIf,
) : NatsSubscriberIf {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    fun saveMessage(messageContent: String): Message {
        publisher.publishRequisition(messageContent)
        val message = Message(message = messageContent)
        return messageRepository.save(message)
    }

    override fun orderRequest(order: String): Boolean {
        logger.info("Got some order to handel, $order")
        publisher.publishOrderReceipt("Got IT.")
        return true
    }

    override fun timeRequest(
        myvalue: String,
        replySubject: String,
    ) {
        val sdf = LocalDateTime.now()
        val reply = "$myvalue $sdf"
        publisher.timeReply(reply, replySubject)
    }

    fun getMessage(id: Long): Message? = messageRepository.findById(id).orElse(null)
}
