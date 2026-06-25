package ObjectClasses

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "items")
class ItemToDo(
    @PrimaryKey val id : String? = null,
    val description : String? = null,
    var fait : Boolean = false,
    val listId: String,         // Clé étrangère vers la liste parente
    var toSync: Boolean = false  // True si modifié hors ligne
) {

    override fun toString(): String {
        return ("Item is ${description} and it is currently set to ${fait}.")
    }

}