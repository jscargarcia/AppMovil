package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var btnIrGestionSensores: Button
    private lateinit var btnIrGestionUsuarios: Button
    private lateinit var btnIrEstadoBarrera: Button
    private lateinit var btnIrHistorialCompleto: Button
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
        btnIrGestionSensores = findViewById(R.id.btn_ir_gestion_sensores)
        btnIrGestionUsuarios = findViewById(R.id.btn_ir_gestion_usuarios)
        btnIrEstadoBarrera = findViewById(R.id.btn_ir_estado_barrera)
        btnIrHistorialCompleto = findViewById(R.id.btn_ir_historial_completo)
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

        // Configurar listeners de navegación
        btnIrGestionSensores.setOnClickListener {
            val intent = Intent(this, GestionSensores::class.java)
            intent.putExtra("rol", rol)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("id_departamento", idDepartamento)
            startActivity(intent)
        }

        btnIrGestionUsuarios.setOnClickListener {
            val intent = Intent(this, GestionUsuarios::class.java)
            intent.putExtra("rol", rol)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("id_departamento", idDepartamento)
            startActivity(intent)
        }

        btnIrEstadoBarrera.setOnClickListener {
            val intent = Intent(this, EstadoBarrera::class.java)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("id_departamento", idDepartamento)
            startActivity(intent)
        }

        btnIrHistorialCompleto.setOnClickListener {
            val intent = Intent(this, HistorialAccesos::class.java)
            intent.putExtra("rol", rol)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("id_departamento", idDepartamento)
            startActivity(intent)
        }

        // Configurar listeners de acciones
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
        val url = "http://35.168.148.150/listarEventos.php?rol=$rol&id_usuario=$idUsuario&id_departamento=$idDepartamento"
        Log.d("HomeAdmin", "URL: $url")
        Log.d("HomeAdmin", "Rol: $rol, ID Usuario: $idUsuario, ID Depto: $idDepartamento")
        
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
                val errorMsg = when {
                    error.networkResponse != null -> {
                        val statusCode = error.networkResponse.statusCode
                        val data = String(error.networkResponse.data)
                        "Error $statusCode: $data"
                    }
                    else -> "Error de red: ${error.message}"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cargarSensores() {
        val url = "http://35.168.148.150/listarSensores.php?id_departamento=$idDepartamento"
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
                        val nombreUsuario = sensor.optString("nombre_usuario", "Sin asignar")
                        listaSensores.add("$id - $codigo ($estado) - Usuario: $nombreUsuario")
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
        val url = "http://35.168.148.150/listarUsuarios.php?id_departamento=$idDepartamento"
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
        val url = "http://35.168.148.150/actualizarEstadoSensor.php?id_sensor=$seleccionado&estado=$nuevoEstado"
        Log.d("HomeAdmin", "URL Sensor: $url")
        Log.d("HomeAdmin", "ID Sensor: $seleccionado, Nuevo Estado: $nuevoEstado")
        
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                Log.d("HomeAdmin", "Respuesta completa: $response")
                try {
                    val json = JSONObject(response)
                    val estado = json.getInt("estado")
                    val mensaje = json.getString("mensaje")
                    
                    if (estado == 1) {
                        Toast.makeText(this, "Sensor actualizado: $mensaje", Toast.LENGTH_SHORT).show()
                        // Esperar un momento antes de recargar
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            cargarSensores()
                        }, 500)
                    } else {
                        Toast.makeText(this, "Error: $mensaje", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Sensor actualizado", Toast.LENGTH_SHORT).show()
                    cargarSensores()
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
                Log.e("HomeAdmin", "Error cambiar estado sensor: $errorMsg")
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun cambiarEstadoUsuario(nuevoEstado: String) {
        val seleccionado = spinnerUsuarios.selectedItem?.toString()?.split(" - ")?.get(0) ?: return
        val url = "http://35.168.148.150/cambiarEstadoUsuario.php?id_usuario=$seleccionado&estado=$nuevoEstado"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            },
            { error -> Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show() })
        Volley.newRequestQueue(this).add(request)
    }

    private fun controlarBarrera(accion: String) {
        val url = "http://35.168.148.150/barrera.php?accion=$accion&id_usuario=$idUsuario&id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                Toast.makeText(this, "Acción barrera: $accion", Toast.LENGTH_SHORT).show()
                cargarHistorial()
            },
            { error -> Toast.makeText(this, "Error servidor: $error", Toast.LENGTH_LONG).show() })
        Volley.newRequestQueue(this).add(request)
    }
}

