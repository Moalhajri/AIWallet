package com.example.walletai

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toBudgetPeriod(value: String) = enumValueOf<BudgetPeriod>(value)

    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod) = value.name
}