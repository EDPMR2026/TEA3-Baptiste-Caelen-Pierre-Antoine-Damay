package com.example.tea1

import ObjectClasses.ListeToDo
import ObjectClasses.ProfilListeToDo
import ObjectClasses.Settings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class ChoixListActivity : AppCompatActivity() {

    private var btnPrefs: Button? = null
    private var pseudo: String = ""
    private var password: String = ""
    private var lastId: Int = 0
    private var editTextListe: EditText? = null

    var adapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choix_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pseudo = Settings.pseudo
        password = Settings.password

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var isInList = false
        Settings.listOfUsers.forEach { element ->
            if (pseudo == element.password) {
                adapter = CustomAdapter(element)
                Settings.profilActuel = element
                isInList = true
            }
        }
        if (!isInList) {
            Settings.lastId += 1
            lastId = Settings.lastId
            var nouvProfil = ProfilListeToDo(lastId, pseudo, password, mutableListOf())
            Settings.listOfUsers.add(nouvProfil)
            Settings.profilActuel = nouvProfil
            adapter = CustomAdapter(nouvProfil)
        }
        recyclerView.adapter = adapter

        // 1. Récupération du hash sauvegardé
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val hash = prefs.getString("hash", "") ?: ""

        // 2. Appel API dans une coroutine
        lifecycleScope.launch {
            try {
                val jsonResponse = TeaApi.retrofitService.getLists(hash)
                val rootObject = org.json.JSONObject(jsonResponse)
                val jsonArray = rootObject.getJSONArray("lists")

                // On vide les anciennes données locales
                Settings.profilActuel.mesListesToDo.clear()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    // On récupère le 'label' de l'API pour votre 'titreListeToDo'
                    val nomListe = obj.getString("label")
                    val idListe = obj.getString("id")

                    // Création de l'objet avec l'ID reçu
                    val nouvelleListe = ListeToDo(idListe, nomListe, mutableListOf())
                    Settings.profilActuel.mesListesToDo.add(nouvelleListe)
                }
                adapter?.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.e("PMR", "Erreur : ${e.message}")
            }
        }



        fun clickBtnListe(view: View?) {
            editTextListe = findViewById(R.id.editTextTextListe)
            var newList = ListeToDo(null, editTextListe?.text.toString(), mutableListOf())
            Settings.profilActuel.mesListesToDo.add(newList)
            adapter?.notifyItemInserted(Settings.profilActuel.mesListesToDo.size - 1)
            editTextListe?.text?.clear()
        }

        fun clickBtnListei(view: View?) {
            val intent = Intent(this, ShowListActivity::class.java)
            startActivity(intent)
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
}