package com.tees.mybudgets.data

import kotlinx.coroutines.flow.Flow

/* This class serves as a middle layer between the BudgetDao (Data Access Object) and the ViewModel.*/
class BudgetRepository(private val dao: BudgetDao) {
    val allItems: Flow<List<BudgetItem>> = dao.getAllItems()

    suspend fun addItem(item: BudgetItem) {
        dao.insertItem(item)
    }

    suspend fun updateItemStatus(itemId: Long, isChecked: Boolean) {
        val item = dao.getItemById(itemId) // Add a DAO function to fetch an item by ID
        if (item != null) {
            dao.updateItem(item.copy(isChecked = isChecked))
        }
    }

    suspend fun deleteItem(budgetItem: BudgetItem) {
        dao.deleteItem(budgetItem)
    }

    suspend fun updateItem(budgetItem: BudgetItem) {
        dao.updateItem(budgetItem)
    }


}