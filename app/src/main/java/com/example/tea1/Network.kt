package com.example.tea1

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

private var BASE_URL = "http://tomnab.fr/todo-api/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

@Throws(IOException::class)
private fun convertStreamToString(`in`: InputStream): String {
    try {
        val reader = BufferedReader(InputStreamReader(`in`))
        val sb = StringBuilder()
        var line: String? = null
        while ((reader.readLine().also { line = it }) != null) {
            sb.append(line + "\n")
        }
        return sb.toString()
    } finally {
        try {
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun requete(qs: String?, context: Context): String {
    if (qs != null) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val urlData: String = prefs.getString("urlData", "http://10.0.2.2/android_chat/data.php")!!

        try {
            val url = URL(urlData + "?" + qs)
            Log.i("PMR", "url utilisée : " + url.toString())
            var urlConnection: HttpURLConnection? = null
            urlConnection = url.openConnection() as HttpURLConnection?
            var `in`: InputStream? = null
            `in` = BufferedInputStream(urlConnection!!.getInputStream())
            val txtReponse = convertStreamToString(`in`)
            urlConnection.disconnect()
            return txtReponse
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return ""
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun verifReseau(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("PMR", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("PMR", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("PMR", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }

    return false
}

fun changerUrl(url: String) {
    BASE_URL = url
}

interface TeaApiService {
    @POST("authenticate")
    suspend fun authenticate(
        @Query("user") user: String,
        @Query("password") password: String
    ): String

    @GET("lists")
    suspend fun getLists(
        @Header("hash") hash: String
    ): String
    // Note : Le serveur renvoie du JSON, mais on le reçoit en String pour l'instant

    @POST("lists")
    suspend fun addList(
        @Query("label") label: String,
        @Header("hash") hash: String
    ): String
    
    @GET("lists/{id}/items")
    suspend fun getItems(
        @Path("id") id: String, // L'ID de la liste que l'on vient de recevoir
        @Header("hash") hash: String
    ): String

    // Ajouter un item (POST)
    @POST("lists/{id}/items")
    suspend fun addItem(
        @Path("id") listId: String,
        @Query("label") label: String,
        @Header("hash") hash: String
    ): String

    // Modifier l'état d'un item (PUT)
    @PUT("lists/{idList}/items/{idItem}")
    suspend fun updateItem(
        @Path("idList") idList: String,
        @Path("idItem") idItem: String,
        @Query("checked") checked: String, // 1 pour fait, 0 pour non fait
        @Header("hash") hash: String
    ): String

}



object TeaApi {
    val retrofitService : TeaApiService by lazy {
        retrofit.create(TeaApiService::class.java)
    }
}