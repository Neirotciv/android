package com.example.fetchai

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fetchai.ui.theme.FetchAITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.reflect.Field
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demandMessage = findViewById<EditText>(R.id.demandMessage)
        val validateButton = findViewById<Button>(R.id.validateButton)

        validateButton.setOnClickListener {
            val message = demandMessage.text.toString()
            if(!isEmptyEditText(demandMessage)) {
                GlobalScope.launch {
                    val content = makeHttpRequest(message)
                    if (content != null) {
                        println("Content : $content")
                    } else {
                        println("Error when fetching API.")
                    }
                }
            }
        }
    }

    private fun isEmptyEditText(demandMessage: EditText): Boolean {
        val errorText = findViewById<TextView>(R.id.errorTextView)

        if (demandMessage.text.toString().isEmpty()) {
            errorText.visibility = TextView.VISIBLE
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