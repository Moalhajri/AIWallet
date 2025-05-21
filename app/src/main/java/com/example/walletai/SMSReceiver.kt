package com.example.walletai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.originatingAddress
                val messageBody = message.messageBody

                // Check if the sender is your bank's number
                if (sender == "YOUR_BANK_NUMBER") {
                    // Parse the message and create an expense
                    val aiSMSParser = AISMSParser(context)
                    val expense = aiSMSParser.parseExpenseFromSMS(messageBody)
                    if (expense != null) {
                        // Add the expense to your database
                        val expenseDao = ExpenseDatabase.getDatabase(context).expenseDao()
                        GlobalScope.launch {
                            expenseDao.insertExpense(expense)
                        }
                    }
                }
            }
        }
    }
}