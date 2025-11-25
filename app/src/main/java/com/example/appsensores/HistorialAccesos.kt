package com.example.appsensores

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class HistorialAccesos : AppCompatActivity() {

    private lateinit var listViewHistorial: ListView
    private lateinit var btnCargarHistorial: Button
    private var idDepartamento: String = ""
    private var idUsuario: String = ""
    private var rol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_accesos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recibir datos del Intent
        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        idUsuario = intent.getStringExtra("id_usuario") ?: ""
        rol = intent.getStringExtra("rol") ?: "OPERADOR"

        // Vincular vistas
        listViewHistorial = findViewById(R.id.listViewHistorialAccesos)
        btnCargarHistorial = findViewById(R.id.btn_cargar_historial_accesos)

        // Configurar botón
        btnCargarHistorial.setOnClickListener {
            cargarHistorial()
        }

        // Cargar historial automáticamente al abrir
        cargarHistorial()
    }

    private fun cargarHistorial() {
        val url = "http://35.168.148.150/listarEventos.php?rol=$rol&id_usuario=$idUsuario&id_departamento=$idDepartamento"
        Log.d("HistorialAccesos", "URL: $url")
        Log.d("HistorialAccesos", "Rol: $rol, ID Usuario: $idUsuario, ID Depto: $idDepartamento")

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaEventos = ArrayList<String>()

                    for (i in 0 until jsonArray.length()) {
                        val evento = jsonArray.getJSONObject(i)
                        val tipo = evento.getString("tipo_evento")
                        val resultado = evento.getString("resultado")
                        val sensor = evento.optString("id_sensor", "N/A")
                        val usuario = evento.optString("id_usuario", "N/A")
                        val fecha = evento.getString("fecha_hora")
                        
                        listaEventos.add("[$fecha] Usuario: $usuario Sensor: $sensor\n$tipo - $resultado")
                    }

                    if (listaEventos.isEmpty()) {
                        listaEventos.add("No hay eventos registrados")
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEventos)
                    listViewHistorial.adapter = adapter
                    Toast.makeText(this, "Historial cargado (${listaEventos.size} eventos)", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this, "Error procesando JSON: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("HistorialAccesos", "Error JSON", e)
                }
            },
            { error ->
                val errorMsg = when {
                    error.networkResponse != null -> {
                        val statusCode = error.networkResponse.statusCode
                        val data = String(error.networkResponse.data)
                        "Error $statusCode: $data"
                    }
                    else -> "Error de red: ${error.message}"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                Log.e("HistorialAccesos", "Error servidor: $error")
            })

        Volley.newRequestQueue(this).add(request)
    }
}