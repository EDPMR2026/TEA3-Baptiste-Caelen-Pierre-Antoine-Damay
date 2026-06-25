package com.example.tea1

import ObjectClasses.ProfilListeToDo
import androidx.room.RoomDatabase
import androidx.room3.Database

@Database(entities = [ProfilListeToDo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): ProfilListeToDoDao
    abstract fun listeToDoDao(): ListeToDoDao
    abstract fun itemToDoDao(): ItemToDoDao
}