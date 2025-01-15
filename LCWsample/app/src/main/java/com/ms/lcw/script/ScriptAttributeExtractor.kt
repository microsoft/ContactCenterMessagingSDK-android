package com.ms.lcw.script

import java.util.regex.Pattern

object ScriptAttributeExtractor {
    private val scriptPattern = Pattern.compile(
        """<script\s+(?=.*\bid="([^"]*)")(?=.*\bsrc="([^"]*)")(?=.*\bdata-app-id="([^"]*)")(?=.*\bdata-lcw-version="([^"]*)")(?=.*\bdata-org-id="([^"]*)")(?=.*\bdata-org-url="([^"]*)").*?>"""
    )

    fun extractAttributes(script: String): ScriptAttributes? {
        try {
            val matcher = scriptPattern.matcher(script)
            return if (matcher.find()) {
                ScriptAttributes(
                    id = getAttributeValue(script, "id"),
                    src = getAttributeValue(script, "src"),
                    dataAppId = getAttributeValue(script, "data-app-id"),
                    dataLcwVersion = getAttributeValue(script, "data-lcw-version"),
                    dataOrgId = getAttributeValue(script, "data-org-id"),
                    dataOrgUrl = getAttributeValue(script, "data-org-url")
                )
            } else {
                null
            }
        } catch (ex: Exception) {
            return null
        }
    }

    private fun getAttributeValue(script: String, attribute: String): String? {
        val regex = """\b$attribute="([^"]*)"""".toRegex()
        return regex.find(script)?.groups?.get(1)?.value
    }
}