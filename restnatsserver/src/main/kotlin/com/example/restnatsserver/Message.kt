package com.example.restnatsserver

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Message(
    @Id @GeneratedValue var id: Long? = null,
    var message: String
)

