package com.example.appsensores

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class HomeOperadorActivity : AppCompatActivity() {

    private lateinit var listViewHistorial: ListView
    private lateinit var btnVerHistorial: Button

    private var idDepartamento: String = ""
    private var idUsuario: String = ""
    private var rol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_operador)

        // Vincular vistas correctas
        listViewHistorial = findViewById(R.id.listViewHistorial)
        btnVerHistorial = findViewById(R.id.btn_ver_historial)

        // Recibir datos de MainActivity
        intent.extras?.let {
            rol = it.getString("rol", "")
            idUsuario = it.getString("id_usuario", "")
            idDepartamento = it.getString("id_departamento", "")
        }

        // Botón para cargar historial
        btnVerHistorial.setOnClickListener {
            cargarHistorial()
        }
    }

    private fun cargarHistorial() {
        val url = "http://54.144.226.230/listarEventos.php?rol=OPERADOR&id_usuario=$idUsuario&id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaEventos = ArrayList<String>()
                    for (i in 0 until jsonArray.length()) {
                        val evento: JSONObject = jsonArray.getJSONObject(i)
                        val tipo = evento.getString("tipo_evento")
                        val resultado = evento.getString("resultado")
                        val sensor = evento.optString("id_sensor", "N/A")
                        val usuario = evento.optString("id_usuario", "N/A")
                        val fecha = evento.getString("fecha_hora")
                        listaEventos.add("[$fecha] Usuario: $usuario Sensor: $sensor - $tipo - $resultado")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEventos)
                    listViewHistorial.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this, "Error procesando JSON: $e", Toast.LENGTH_LONG).show()
                }
            }, { error ->
                Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }
}





