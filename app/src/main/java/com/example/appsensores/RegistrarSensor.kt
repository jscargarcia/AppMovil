package com.example.appsensores

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class RegistrarSensor : AppCompatActivity() {

    private lateinit var txtCodigo: EditText
    private lateinit var txtTipo: EditText
    private lateinit var spinnerUsuarios: Spinner
    private lateinit var btnGuardar: Button
    private var idDepartamento: String = ""
    private var rol: String = ""
    private val listaUsuariosIds = ArrayList<String>()
    private val listaUsuariosNombres = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_sensor)

        txtCodigo = findViewById(R.id.etCodigoSensor)
        txtTipo = findViewById(R.id.etTipoSensor)
        spinnerUsuarios = findViewById(R.id.spinnerUsuarios)
        btnGuardar = findViewById(R.id.btnGuardarSensor)

        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        rol = intent.getStringExtra("rol") ?: "ADMIN"

        // Validar que solo ADMIN pueda registrar sensores
        if (rol != "ADMIN") {
            Toast.makeText(this, "Acceso denegado: Solo administradores", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar lista de usuarios
        cargarUsuarios()

        btnGuardar.setOnClickListener {
            registrarSensor()
        }
    }

    private fun cargarUsuarios() {
        val url = "http://35.168.148.150/listarUsuarios.php?id_departamento=$idDepartamento"
        
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    listaUsuariosIds.clear()
                    listaUsuariosNombres.clear()
                    
                    for (i in 0 until jsonArray.length()) {
                        val usuario = jsonArray.getJSONObject(i)
                        val id = usuario.getString("id_usuario")
                        val nombre = usuario.getString("nombre")
                        val estado = usuario.getString("estado")
                        
                        // Solo mostrar usuarios activos
                        if (estado == "ACTIVO") {
                            listaUsuariosIds.add(id)
                            listaUsuariosNombres.add("$id - $nombre")
                        }
                    }
                    
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaUsuariosNombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerUsuarios.adapter = adapter
                    
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        
        Volley.newRequestQueue(this).add(request)
    }

    private fun registrarSensor() {
        val codigo = txtCodigo.text.toString()
        val tipo = txtTipo.text.toString()

        if (codigo.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (idDepartamento.isEmpty()) {
            Toast.makeText(this, "Error: Departamento no especificado", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaUsuariosIds.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del usuario seleccionado
        val posicionSeleccionada = spinnerUsuarios.selectedItemPosition
        val idUsuario = listaUsuariosIds[posicionSeleccionada]

        val url = "http://35.168.148.150/registrarSensor.php?codigo=$codigo&tipo=$tipo&id_departamento=$idDepartamento&id_usuario=$idUsuario&estado=ACTIVO"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = org.json.JSONObject(response)
                    if (json.getInt("estado") == 1) {
                        Toast.makeText(this, "Sensor registrado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Sensor registrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}

