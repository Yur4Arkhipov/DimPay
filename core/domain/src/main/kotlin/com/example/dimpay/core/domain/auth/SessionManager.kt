package com.example.dimpay.core.domain.auth

interface SessionManager {
    fun markAuthenticated()
    fun isAuthenticated(): Boolean
    fun invalidateSession()
}