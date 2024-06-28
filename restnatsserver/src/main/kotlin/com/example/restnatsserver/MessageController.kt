package com.example.restnatsserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// curl -X POST localhost:8082/message -H "Content-type: application/json" -d '{"message":"333"}'

@RestController
@RequestMapping("/message")
class MessageController(
    private val messageService: MessageService,
) {
    @PostMapping
    fun postMessage(
        @RequestBody message: Map<String, String>,
    ): Message {
        val msg = message["message"] ?: throw IllegalArgumentException("Message is required")
        return messageService.saveMessage(msg)
    }

    @GetMapping("/{id}")
    fun getMessage(
        @PathVariable id: Long,
    ): Message? = messageService.getMessage(id)
}
