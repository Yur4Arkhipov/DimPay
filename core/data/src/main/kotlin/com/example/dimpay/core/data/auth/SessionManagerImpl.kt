package com.example.dimpay.core.data.auth

import com.example.dimpay.core.domain.auth.SessionManager
import javax.inject.Inject

class SessionManagerImpl @Inject constructor() : SessionManager {
    private var authenticated = false

    override fun markAuthenticated() { authenticated = true }
    override fun isAuthenticated(): Boolean = authenticated
    override fun invalidateSession() { authenticated = false }
}