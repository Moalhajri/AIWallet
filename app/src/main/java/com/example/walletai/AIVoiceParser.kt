package com.example.walletai

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class AIVoiceParser(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val currencyPattern = Pattern.compile("""\$?\d+(\.\d{2})?""")
    private val datePattern = Pattern.compile("""(\d{4}-\d{2}-\d{2})|(\d{2}/\d{2}/\d{4})|((jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\s+\d{1,2})""", Pattern.CASE_INSENSITIVE)

    suspend fun parseVoiceInput(input: String): Expense? {
        val lowercaseInput = input.toLowerCase(Locale.US)
        val amount = extractAmount(lowercaseInput)
        val date = extractDate(lowercaseInput)
        val category = extractCategory(lowercaseInput)
        val description = extractDescription(lowercaseInput)

        return if (amount != null && description.isNotBlank()) {
            Expense(
                description = description,
                amount = amount,
                date = date ?: System.currentTimeMillis(),
                category = category
            )
        } else null
    }

    private fun extractAmount(input: String): Double? {
        val matcher = currencyPattern.matcher(input)
        return if (matcher.find()) {
            matcher.group().replace("$", "").toDoubleOrNull()
        } else null
    }

    private fun extractDate(input: String): Long? {
        val matcher = datePattern.matcher(input)
        return if (matcher.find()) {
            try {
                dateFormat.parse(matcher.group())?.time
            } catch (e: Exception) {
                null
            }
        } else null
    }

    private fun extractCategory(input: String): String {
        val categories = listOf("food", "transportation", "entertainment", "utilities", "shopping")
        return categories.find { input.contains(it) } ?: "other"
    }

    private fun extractDescription(input: String): String {
        return input.replace(currencyPattern.toRegex(), "")
            .replace(datePattern.toRegex(), "")
            .trim()
    }
}