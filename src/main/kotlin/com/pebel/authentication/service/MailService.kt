package com.pebel.authentication.service

import com.pebel.authentication.model.User

interface MailService {
    fun sendVerificationEmail(user: User)
    fun sendPasswordResetEmail(user: User, token: String)
}