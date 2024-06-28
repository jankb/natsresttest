package com.example.restnatsserver

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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

    fun getMessage(id: Long): Message? = messageRepository.findById(id).orElse(null)
}
