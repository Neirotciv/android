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
import java.lang.reflect.Field

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demandMessage = findViewById<EditText>(R.id.demandMessage)
        val validateButton = findViewById<Button>(R.id.validateButton)

        validateButton.setOnClickListener {
            val message = demandMessage.text.toString()
            isEmptyEditText(demandMessage)
        }
    }

    private fun isEmptyEditText(demandMessage: EditText) {
        val errorText = findViewById<TextView>(R.id.errorTextView)

        if (demandMessage.text.toString().isEmpty()) {
            errorText.visibility = TextView.VISIBLE
        } else {
            errorText.visibility = TextView.GONE
        }
    }
}