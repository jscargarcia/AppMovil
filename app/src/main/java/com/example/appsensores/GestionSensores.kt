package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class GestionSensores : AppCompatActivity() {

    private lateinit var lista: ListView
    private lateinit var btnAgregar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        lista = findViewById(R.id.listaSensores)
        btnAgregar = findViewById(R.id.btnRegistrarSensor)

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, RegistrarSensor::class.java))
        }

        cargarSensores()
    }

    fun cargarSensores() {

        val rol = intent.getStringExtra("rol")
        val idDepartamento = intent.getStringExtra("id_departamento")

        val url = if (rol == "ADMIN") {
            "http://54.144.226.230/listar_sensores.php"
        } else {
            "http://54.144.226.230/listar_sensores_por_departamento.php?id_departamento=$idDepartamento"
        }

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val arr = JSONArray(response)
                val listaSensores = mutableListOf<Sensor>()

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    listaSensores.add(
                        Sensor(
                            obj.getString("id_sensor"),
                            obj.getString("codigo_sensor"),
                            obj.getString("estado"),
                            obj.getString("tipo")
                        )
                    )
                }

                lista.adapter = SensorAdapter(this, listaSensores)

            },
            { Toast.makeText(this, "Error servidor", Toast.LENGTH_SHORT).show() }
        )

        Volley.newRequestQueue(this).add(request)
    }}
