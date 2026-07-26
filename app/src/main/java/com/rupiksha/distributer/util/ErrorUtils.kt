package com.rupiksha.distributer.util

import android.util.Log

object ErrorUtils {
    private const val TAG = "ErrorUtils"

    fun sanitizeError(message: String?): String {
        if (message == null) return "An unknown error occurred"

        // Log the full technical error for developers
        Log.e(TAG, "Technical Error: $message")

        // 1. Handle specific known business errors
        if (message.contains("retailers_mobile_number_key", ignoreCase = true)) {
            return "This mobile number is already registered. Please use a different one or log in."
        }
        if (message.contains("retailers_email_key", ignoreCase = true)) {
            return "This email address is already registered."
        }
        if (message.contains("retailers_pan_number_key", ignoreCase = true)) {
            return "This PAN number is already registered."
        }
        if (message.contains("retailers_adhar_number_key", ignoreCase = true)) {
            return "This Aadhaar number is already registered."
        }

        // 2. Redact URLs
        val urlRegex = "(https?://[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]*)".toRegex()
        var sanitized = message.replace(urlRegex, "[URL REDACTED]")

        // 3. Redact Headers and API Keys/Tokens
        // Matches patterns like "Authorization=[Bearer ...]" or "apikey=..." or long strings that look like tokens
        val authRegex = "(?i)(Authorization|apikey|Bearer)\\s*[:=]\\s*[\\[\\s]*[\\w\\.\\-]{20,}[\\]\\s]*".toRegex()
        sanitized = sanitized.replace(authRegex, "$1=[REDACTED]")

        // 4. Remove stack traces or overly technical details if they start with common library prefixes
        if (sanitized.contains("io.ktor", ignoreCase = true) || sanitized.contains("supabase", ignoreCase = true)) {
             // If it's a raw library exception, try to extract the message part or give a generic one
             val lines = sanitized.lines()
             if (lines.isNotEmpty()) {
                 val firstLine = lines[0]
                 if (firstLine.contains(":")) {
                     return firstLine.substringAfter(":").trim().takeIf { it.isNotEmpty() } ?: "Network request failed"
                 }
             }
             return "Connection error. Please try again later."
        }

        return sanitized
    }
}
