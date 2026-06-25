package com.example.tea1

import ObjectClasses.ItemToDo
import ObjectClasses.ListeToDo
import ObjectClasses.ProfilListeToDo
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query

@Dao
public interface ProfilListeToDoDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<ProfilListeToDo>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: Array<Int>): List<ProfilListeToDo>

    @Query("SELECT * FROM user WHERE pseudo LIKE :name AND " +
            "password LIKE :mdp LIMIT 1")
    fun findByName(name: String, mdp: String): ProfilListeToDo

    @Insert
    fun insertAll(users: Array<ProfilListeToDo>)

    @Delete
    fun delete(user: ProfilListeToDo)
}

@Dao
public interface ListeToDoDao {
    @Query("SELECT * FROM liste")
    fun getAll(): List<ListeToDo>

    @Query("SELECT * FROM liste WHERE id IN (:listIds)")
    fun loadAllByIds(listIds: Array<Int>): List<ListeToDo>

    @Query("SELECT * FROM liste WHERE titreListeToDo LIKE :titre LIMIT 1")
    fun findByName(titre: String): ListeToDo

    @Insert
    fun insertAll(lists: Array<ListeToDo>)

    @Delete
    fun delete(list: ListeToDo)
}

@Dao
public interface ItemToDoDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<ItemToDo>

    @Query("SELECT * FROM item WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: Array<Int>): List<ItemToDo>

    @Query("SELECT * FROM item WHERE description LIKE :titre LIMIT 1")
    fun findByName(titre: String): ItemToDo

    @Insert
    fun insertAll(items: Array<ItemToDo>)

    @Delete
    fun delete(item: ItemToDo)
}