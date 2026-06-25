package com.example.tea1

import ObjectClasses.ItemToDo
import ObjectClasses.ListeToDo
import ObjectClasses.ProfilListeToDo
import ObjectClasses.Settings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlin.collections.plus


class ShowListActivity : AppCompatActivity() {

    private var btnPrefs: Button? = null
    private var pseudo: String = ""
    private var editTextItem: EditText? = null

    private lateinit var maListe: ListeToDo

    var adapter: CustomAdapterItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        var isInList = false
//        Settings.listOfUsers.forEach { element ->
//            if (pseudo == element.login) {
//                adapter = CustomAdapterItem(element)
//                Settings.profilActuel = element
//                isInList = true
//            }
//        }
//        if (!isInList) {
//            var nouvProfil = ProfilListeToDo(pseudo,mutableListOf(), mutableListOf())
//            Settings.listOfUsers.add(nouvProfil)
//            Settings.profilActuel = nouvProfil
//            adapter = CustomAdapterItem(nouvProfil)
//        }
//        recyclerView.adapter = adapter

        // Récupération de l'ID envoyé par l'écran précédent
        val idListeRecu = intent.getStringExtra("ID_LISTE") ?: ""

        Log.d("PMR", "ID de la liste sélectionnée : $idListeRecu")

        //val nomListe = intent.getStringExtra("NOM_LISTE")
        val listeTrouvee = Settings.profilActuel.mesListesToDo.find {it.id == idListeRecu}
        if (listeTrouvee != null) {
            maListe = listeTrouvee
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val hash = prefs.getString("hash", "") ?: ""

            adapter = CustomAdapterItem(hash, idListeRecu, maListe)
            recyclerView.adapter = adapter

            refreshItems(idListeRecu,hash)

        } else {
                finish()
        }
    }


    fun clickBtnItem(view: View?) {
        val editField = findViewById<EditText>(R.id.editTextTextItem)
        val text = editField.text.toString()
        val hash = PreferenceManager.getDefaultSharedPreferences(this).getString("hash", "") ?: ""
        val idListeRecu = intent.getStringExtra("ID_LISTE") ?: ""

        if (text.isNotBlank() && idListeRecu.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    // Envoi au serveur
                    TeaApi.retrofitService.addItem(idListeRecu, text, hash)
                    Log.i("PMR", "Ajout de l'item : $text")

                    // Recharger la liste pour voir le nouvel item avec son ID officiel
                    //val newItem = ItemToDo(null, text, false)
                    //maListe.lesItems.add(newItem)
                    //adapter?.notifyItemInserted(maListe.lesItems.size - 1)
                    refreshItems(idListeRecu, hash)

                    editField.text.clear()
                } catch (e: Exception) {
                    Log.e("PMR", "Erreur ajout : ${e.message}")
                }
            }
        }
    }



fun refreshItems(id : String, token : String, liste: ListeToDo = maListe) {
    lifecycleScope.launch {
        try {
            // Appel API pour récupérer les items en JSON
            val jsonResponse = TeaApi.retrofitService.getItems(id, token)
            val rootObject = org.json.JSONObject(jsonResponse)
            val jsonArray =
                rootObject.getJSONArray("items") // Le serveur renvoie une clé "items"

            val nouveauxItems = mutableListOf<ItemToDo>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val idItem = obj.getString("id")
                Log.d("PMR_JSON", "Contenu de l'item : $obj")
                val desc = obj.getString("label")
                //val isDone = obj.getInt("checked") == 1
                val checkedValue = obj.optString("checked")
                val isDone = (checkedValue == "1")
                nouveauxItems.add(ItemToDo(idItem, desc, isDone))
            }

            // Mise à jour de l'affichage
            liste.lesItems.clear()
            liste.lesItems.addAll(nouveauxItems)
            adapter?.notifyDataSetChanged()

        } catch (e: Exception) {
            Log.e("PMR", "Erreur items : ${e.message}")
        }
    }
}
    fun clickBtnPoints(view: View?) {
        btnPrefs = findViewById(R.id.btnPrefs3)
        if (btnPrefs?.visibility == View.INVISIBLE) {
            btnPrefs?.visibility = View.VISIBLE
        } else {
            btnPrefs?.visibility = View.INVISIBLE
        }
    }

    fun clickBtnPrefs(view: View?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}