package com.tees.mybudgets.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//representing a table in a Room database
@Entity(tableName = "budget_items")
data class BudgetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val item: String,
    val price: Double,
    val date: String,
    val isChecked: Boolean = false // Ensure this property is included
)