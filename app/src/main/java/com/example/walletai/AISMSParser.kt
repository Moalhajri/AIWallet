package com.example.walletai

import android.content.Context
import java.util.regex.Pattern

class AISMSParser(private val context: Context) {
    fun parseExpenseFromSMS(messageBody: String): Expense? {
        val amountPattern = Pattern.compile("""\$?(\d+(\.\d{2})?)""")
        val amountMatch = amountPattern.matcher(messageBody)

        return if (amountMatch.find()) {
            val amount = amountMatch.group(1)?.toDoubleOrNull() ?: return null

            Expense(
                description = "SMS Transaction: ${messageBody.take(50)}...",
                amount = amount,
                date = System.currentTimeMillis(),
                category = "Uncategorized"
            )
        } else null
    }
}