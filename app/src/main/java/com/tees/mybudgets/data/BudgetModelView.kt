package com.tees.mybudgets.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//Inherits from AndroidViewModel, which provides application context to the ViewModel.
//Manages and provides budget-related data for the UI while handling configuration changes (e.g., screen rotations) efficiently
class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BudgetRepository
    val allItems: LiveData<List<BudgetItem>>

    init {
        val dao = BudgetDatabase.getDatabase(application).budgetDao()
        repository = BudgetRepository(dao)
        allItems = repository.allItems.asLiveData()  // Converts Flow to LiveData
    }


    fun getItems(): Flow<List<BudgetItem>> {
        return repository.allItems  // Returns the Flow from the repository
    }

    fun updateItemStatus(budgetItem: BudgetItem) {
        viewModelScope.launch {
            repository.updateItemStatus(budgetItem.id, budgetItem.isChecked)
        }
    }

    fun deleteItem(budgetItem: BudgetItem) {
        viewModelScope.launch {
            repository.deleteItem(budgetItem)
        }
    }

    fun addItem(budgetItem: BudgetItem) {
        viewModelScope.launch {
            repository.addItem(budgetItem)
        }
    }



}


