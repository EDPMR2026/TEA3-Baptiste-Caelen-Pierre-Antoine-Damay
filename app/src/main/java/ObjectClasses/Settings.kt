package ObjectClasses

object Settings {
    public var pseudo : String = "Pseudo"
    public var password : String = "Password"
    public var lastId : Int = 0
    public var profilActuel = ProfilListeToDo(lastId, pseudo, password, mutableListOf())
    public var listOfUsers : MutableList<ProfilListeToDo> = mutableListOf()
    public var url : String = "http://tomnab.fr/todo-api/"
}