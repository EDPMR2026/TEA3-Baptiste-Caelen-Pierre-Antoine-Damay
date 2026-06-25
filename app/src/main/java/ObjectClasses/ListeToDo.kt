package ObjectClasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "lists")
class ListeToDo(
    @PrimaryKey var id: String? = null,
    var titreListeToDo: String,
) {
    @Ignore
    var lesItems: MutableList<ItemToDo> = mutableListOf()
}


//    private fun RechercherItem(description : String) : ItemToDo{
//        for (i in 0..(lesItems.size)) {
//            if (lesItems[i].description == description) {
//                return lesItems[i]
//            }
//        }
//        return  ItemToDo(null, null, false)
//    }
//
//    override fun toString(): String {
//        return ("la liste ${titreListeToDo} contient : ${lesItems}")
//    }