package com.tees.mybudgets.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//entry point for accessing the app's local database
//version: Specifies the version of the database schema (here, version 2). This is important for handling migrations.
@Database(entities = [BudgetItem::class], version = 2) //Specifies that this class represents a Room database.

//Registers custom type converters for the database.
@TypeConverters(Converters::class)

//Provides access to the DAO (BudgetDao) associated with the database
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao

    /*Implements a singleton pattern for the database instance, ensuring that only one instance of
    BudgetDatabase is created during the app's lifecycle.*/
    companion object {
        @Volatile
        private var INSTANCE: BudgetDatabase? = null

        //Returns the singleton BudgetDatabase instance.
        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "budget_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 1 to 2 which ensures data integrity and prevents crashes during updates
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE budget_items ADD COLUMN isChecked INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

