package com.example.appsensores

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var listViewEventos: ListView
    private lateinit var btnCargarHistorial: Button
    private lateinit var spinnerSensores: Spinner
    private lateinit var btnActivarSensor: Button
    private lateinit var btnDesactivarSensor: Button
    private lateinit var spinnerUsuarios: Spinner
    private lateinit var btnActivarUsuario: Button
    private lateinit var btnDesactivarUsuario: Button
    private lateinit var btnAbrirBarrera: Button
    private lateinit var btnCerrarBarrera: Button

    private var idDepartamento: String = ""
    private var idUsuario: String = ""
    private var rol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        // Vincular vistas
        listViewEventos = findViewById(R.id.listViewEventos)
        btnCargarHistorial = findViewById(R.id.btn_cargar_historial)
        spinnerSensores = findViewById(R.id.spinner_sensores)
        btnActivarSensor = findViewById(R.id.btn_activar_sensor)
        btnDesactivarSensor = findViewById(R.id.btn_desactivar_sensor)
        spinnerUsuarios = findViewById(R.id.spinner_usuarios)
        btnActivarUsuario = findViewById(R.id.btn_activar_usuario)
        btnDesactivarUsuario = findViewById(R.id.btn_desactivar_usuario)
        btnAbrirBarrera = findViewById(R.id.btn_abrir_barrera)
        btnCerrarBarrera = findViewById(R.id.btn_cerrar_barrera)

        // Recibir datos de MainActivity
        intent.extras?.let {
            rol = it.getString("rol", "")
            idUsuario = it.getString("id_usuario", "")
            idDepartamento = it.getString("id_departamento", "")
        }

        // Configurar listeners
        btnCargarHistorial.setOnClickListener { cargarHistorial() }
        btnActivarSensor.setOnClickListener { cambiarEstadoSensor("ACTIVO") }
        btnDesactivarSensor.setOnClickListener { cambiarEstadoSensor("INACTIVO") }
        btnActivarUsuario.setOnClickListener { cambiarEstadoUsuario("ACTIVO") }
        btnDesactivarUsuario.setOnClickListener { cambiarEstadoUsuario("INACTIVO") }
        btnAbrirBarrera.setOnClickListener { controlarBarrera("ABRIR") }
        btnCerrarBarrera.setOnClickListener { controlarBarrera("CERRAR") }

        // Cargar datos iniciales
        cargarSensores()
        cargarUsuarios()
    }

    private fun cargarHistorial() {
        val url = "http://54.144.226.230/listarEventos.php?rol=$rol&id_usuario=$idUsuario&id_departamento=$idDepartamento"
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
                        listaEventos.add("[$fecha] Usuario: $usuario Sensor: $sensor - $tipo - $resultado")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEventos)
                    listViewEventos.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this, "Error procesando JSON: $e", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cargarSensores() {
        val url = "http://54.144.226.230/listarSensores.php?id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaSensores = ArrayList<String>()
                    for (i in 0 until jsonArray.length()) {
                        val sensor = jsonArray.getJSONObject(i)
                        val id = sensor.getString("id_sensor")
                        val codigo = sensor.getString("codigo_sensor")
                        val estado = sensor.getString("estado")
                        listaSensores.add("$id - $codigo ($estado)")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSensores)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSensores.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this, "Error cargando sensores: $e", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cargarUsuarios() {
        val url = "http://54.144.226.230/listarUsuarios.php?id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaUsuarios = ArrayList<String>()
                    for (i in 0 until jsonArray.length()) {
                        val usuario = jsonArray.getJSONObject(i)
                        val id = usuario.getString("id_usuario")
                        val nombre = usuario.getString("nombre")
                        val estado = usuario.getString("estado")
                        listaUsuarios.add("$id - $nombre ($estado)")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaUsuarios)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerUsuarios.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this, "Error cargando usuarios: $e", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cambiarEstadoSensor(nuevoEstado: String) {
        val seleccionado = spinnerSensores.selectedItem?.toString()?.split(" - ")?.get(0) ?: return
        val url = "http://54.144.226.230/actualizarEstadoSensor.php?id_sensor=$seleccionado&estado=$nuevoEstado"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                Toast.makeText(this, "Sensor actualizado", Toast.LENGTH_SHORT).show()
                cargarSensores()
            },
            { error -> Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show() })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cambiarEstadoUsuario(nuevoEstado: String) {
        val seleccionado = spinnerUsuarios.selectedItem?.toString()?.split(" - ")?.get(0) ?: return
        val url = "http://54.144.226.230/cambiarEstadoUsuario.php?id_usuario=$seleccionado&estado=$nuevoEstado"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            },
            { error -> Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show() })
        Volley.newRequestQueue(this).add(request)
    }

    private fun controlarBarrera(accion: String) {
        val url = "http://54.144.226.230/barrera.php?accion=$accion"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                Toast.makeText(this, "Acción barrera: $accion", Toast.LENGTH_SHORT).show()
                cargarHistorial()
            },
            { error -> Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show() })
        Volley.newRequestQueue(this).add(request)
    }
}

