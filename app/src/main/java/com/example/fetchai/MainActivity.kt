package com.example.fetchai


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demandMessage = findViewById<EditText>(R.id.demandMessage)
        val validateButton = findViewById<Button>(R.id.validateButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val appIcon = findViewById<ImageView>(R.id.appIcon)
        val message = demandMessage.text.toString()

        validateButton.setOnClickListener {
            if(!isEmptyEditText(demandMessage)) {
                progressBar.visibility = View.VISIBLE
                appIcon.visibility = View.GONE

                GlobalScope.launch {
                    val content = makeHttpRequest(message)
                    if (content != null) {
                        println("Content : $content")
                        val intent = Intent(this@MainActivity, LibertAiResponseActivity::class.java)
                        intent.putExtra("content", content)
                        startActivity(intent)
                    } else {
                        println("Error while fetching API.")
                    }
                }
            }
        }
    }

    private fun isEmptyEditText(demandMessage: EditText): Boolean {
        val errorText = findViewById<TextView>(R.id.errorTextView)

        if (demandMessage.text.toString().isEmpty()) {
            errorText.visibility = TextView.VISIBLE
            Toast.makeText(this, "Veuillez compléter le champ avant de valider", Toast.LENGTH_SHORT).show()
            return true
        }
        errorText.visibility = TextView.GONE
        return false
    }

    suspend fun makeHttpRequest(text: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "https://curated.aleph.cloud/vm/a8b6d895cfe757d4bc5db9ba30675b5031fe3189a99a14f13d5210c473220caf/completion"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val postData = "{\"prompt\": \"$text\", \"n_predict\": 128}"
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line).append('\n')
                    }
                    reader.close()

                    val jsonResponse = JSONObject(response.toString())
                    return@withContext jsonResponse.optString("content")
                } else {
                    println("Request error : $responseCode")
                }
            } catch (e: Exception) {
                println("Exception : ${e.message}")
                e.printStackTrace()
            }
            return@withContext null
        }
    }
}