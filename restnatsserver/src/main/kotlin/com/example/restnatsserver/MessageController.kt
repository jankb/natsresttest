package com.example.restnatsserver

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message")
class MessageController(private val messageService: MessageService) {

    @PostMapping
    fun postMessage(@RequestBody message: Map<String, String>): Message {
        val msg = message["message"] ?: throw IllegalArgumentException("Message is required")
        return messageService.saveMessage(msg)
    }

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: Long): Message? = messageService.getMessage(id)
}
