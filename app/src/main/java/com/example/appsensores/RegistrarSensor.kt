package com.example.appsensores

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RegistrarSensor : AppCompatActivity() {

    private lateinit var txtCodigo: EditText
    private lateinit var txtTipo: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_sensor)

        txtCodigo = findViewById(R.id.etCodigoSensor)
        txtTipo = findViewById(R.id.etTipoSensor)
        btnGuardar = findViewById(R.id.btnGuardarSensor)

        btnGuardar.setOnClickListener {
            registrarSensor()
        }
    }

    private fun registrarSensor() {
        val codigo = txtCodigo.text.toString()
        val tipo = txtTipo.text.toString()

        if (codigo.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://TU_IP/registrar_sensor.php?codigo=$codigo&tipo=$tipo"

        val request = StringRequest(
            Request.Method.GET, url,
            {
                Toast.makeText(this, "Sensor registrado", Toast.LENGTH_SHORT).show()
                finish()   // ← vuelve a GestiónSensores
            },
            {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}

