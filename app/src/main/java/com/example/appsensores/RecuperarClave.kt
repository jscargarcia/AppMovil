package com.example.appsensores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RecuperarClaveActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var btnEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_clave)

        txtEmail = findViewById(R.id.txt_email_recuperar)
        btnEnviar = findViewById(R.id.btn_enviar_recuperacion)

        btnEnviar.setOnClickListener {
            recuperarClave()
        }
    }

    private fun recuperarClave() {
        val url = "http://54.144.226.230/recuperarClave.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["email"] = txtEmail.text.toString()
                return map
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
