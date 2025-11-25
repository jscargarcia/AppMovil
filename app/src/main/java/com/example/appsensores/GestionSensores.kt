package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class GestionSensores : AppCompatActivity(), OnEstadoCambiadoListener {

    private lateinit var lista: ListView
    private lateinit var btnAgregar: Button
    private var rol: String = ""
    private var idDepartamento: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        // Recibir y validar rol
        rol = intent.getStringExtra("rol") ?: ""
        idDepartamento = intent.getStringExtra("id_departamento") ?: ""

        // Validar que solo ADMIN pueda acceder
        if (rol != "ADMIN") {
            Toast.makeText(this, "Acceso denegado: Solo administradores", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        lista = findViewById(R.id.listaSensores)
        btnAgregar = findViewById(R.id.btnRegistrarSensor)

        btnAgregar.setOnClickListener {
            val intent = Intent(this, RegistrarSensor::class.java)
            intent.putExtra("id_departamento", idDepartamento)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        cargarSensores()
    }

    override fun onResume() {
        super.onResume()
        cargarSensores() // Recargar lista al volver de registrar sensor
    }

    fun cargarSensores() {

        val url = if (rol == "ADMIN") {
            "http://35.168.148.150/listarSensores.php?id_departamento=$idDepartamento"
        } else {
            "http://35.168.148.150/listar_sensores_por_departamento.php?id_departamento=$idDepartamento"
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

                lista.adapter = SensorAdapter(this, listaSensores, this)

            },
            { Toast.makeText(this, "Error servidor", Toast.LENGTH_SHORT).show() }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onEstadoCambiado() {
        cargarSensores()
    }
}
