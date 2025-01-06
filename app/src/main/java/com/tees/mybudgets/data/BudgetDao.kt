package com.tees.mybudgets.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/*Defining Data Access Object (DAO) interface in Android's Room persistence
 library for interacting with the budget_items table in a SQLite database. */

@Dao
interface BudgetDao {

    //Inserts a new BudgetItem into the database.
    @Insert
    suspend fun insertItem(item: BudgetItem): Long

    //Retrieves all items from the budget_items table, sorted by date in ascending order
    @Query("SELECT * FROM budget_items ORDER BY date ASC")
    fun getAllItems(): Flow<List<BudgetItem>>

    //Updates an existing BudgetItem in the database
    @Update
    suspend fun updateItem(budgetItem: BudgetItem)

    //Deletes the specified BudgetItem from the database.
    @Delete
    suspend fun deleteItem(budgetItem: BudgetItem)

    //Updates the isChecked status of a specific item based on its id
    @Query("UPDATE budget_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateItemStatus(id: Long, isChecked: Boolean)

    // Fetches a single BudgetItem by its ID.
    @Query("SELECT * FROM budget_items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Long): BudgetItem?


}
