package com.example.walletai

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIBankStatementAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val expenseDao: ExpenseDao
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val datePatterns = listOf(
        "MM/dd/yyyy", "yyyy-MM-dd", "dd/MM/yyyy", "MMM dd, yyyy"
    )

    suspend fun analyzeBankStatement(uri: Uri): Int {
        val image = InputImage.fromFilePath(context, uri)
        var expensesAdded = 0

        withContext(Dispatchers.Default) {
            try {
                val visionText = recognizer.process(image).await()
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        val expense = parseExpenseFromLine(line.text)
                        if (expense != null) {
                            expenseDao.insertExpense(expense)
                            expensesAdded++
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AIBankAnalyzer", "Failed to process image", e)
            }
        }

        return expensesAdded
    }

    private fun parseExpenseFromLine(line: String): Expense? {
        val parts = line.split(Regex("\\s+"))
        if (parts.size >= 3) {
            val date = parseDateFromString(parts[0])
            val amount = parseAmount(parts)
            val description = parts.subList(2, parts.size).joinToString(" ")

            if (date != null && amount != null) {
                return Expense(
                    id = 0,
                    description = description,
                    amount = amount,
                    date = date,
                    category = categorizeExpense(description),
                    tags = "",
                    receiptPath = ""
                )
            }
        }
        return null
    }

    private fun parseDateFromString(dateString: String): Long? {
        for (pattern in datePatterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                return sdf.parse(dateString)?.time
            } catch (e: Exception) {
                // continue trying next pattern
            }
        }
        return null
    }

    private fun parseAmount(parts: List<String>): Double? {
        val amountPattern = Pattern.compile("""[-+]?\$?\d+(\.\d+)?""")
        for (part in parts) {
            val matcher = amountPattern.matcher(part)
            if (matcher.find()) {
                return matcher.group().replace("$", "").toDoubleOrNull()
            }
        }
        return null
    }

    private fun categorizeExpense(description: String): String {
        val lowerDesc = description.lowercase(Locale.US)
        return when {
            lowerDesc.contains("groceries") || lowerDesc.contains("restaurant") -> "Food"
            lowerDesc.contains("gas") || lowerDesc.contains("uber") -> "Transportation"
            lowerDesc.contains("movie") || lowerDesc.contains("entertainment") -> "Entertainment"
            lowerDesc.contains("electric") || lowerDesc.contains("water") -> "Utilities"
            lowerDesc.contains("amazon") || lowerDesc.contains("walmart") -> "Shopping"
            else -> "Other"
        }
    }
}
