package com.example.appsensores

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AgregarEditarUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spRol: Spinner
    private lateinit var btnGuardar: Button

    private var idDepartamento = ""
    private var idUsuario: String? = null // null = agregar, no null = editar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_editar_usuario)

        // --- VINCULAR VIEWS ---
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        spRol = findViewById(R.id.spRol)
        btnGuardar = findViewById(R.id.btnGuardar)

        // --- OBTENER DATOS DEL INTENT ---
        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        idUsuario = intent.getStringExtra("id_usuario") // si viene, es edición

        // --- CARGAR DATOS SI ES EDICIÓN ---
        if (idUsuario != null) cargarDatosUsuario(idUsuario!!)

        // --- BOTÓN GUARDAR ---
        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                if (idUsuario == null) agregarUsuario() else actualizarUsuario()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (etNombre.text.isBlank() || etEmail.text.isBlank() || (idUsuario == null && etPassword.text.isBlank())) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // -------------------- CARGAR DATOS PARA EDICIÓN --------------------
    private fun cargarDatosUsuario(id: String) {
        val url = "https://54.144.226.230/obtenerUsuario.php?id_usuario=$id"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val json = JSONObject(response)
                etNombre.setText(json.getString("nombre"))
                etEmail.setText(json.getString("email"))
                val rol = json.getString("rol")
                val rolesArray = resources.getStringArray(R.array.roles)
                spRol.setSelection(rolesArray.indexOf(rol))
            },
            { error -> Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show() })
        queue.add(request)
    }

    // -------------------- AGREGAR USUARIO --------------------
    private fun agregarUsuario() {
        val nombre = etNombre.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val rol = spRol.selectedItem.toString()

        val url = "https://54.144.226.230/registrarUsuario.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                val json = JSONObject(response)
                if (json.getInt("estado") == 1) {
                    Toast.makeText(this, "Usuario agregado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al agregar usuario", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show() }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password,
                    "rol" to rol,
                    "id_departamento" to idDepartamento
                )
            }
        }
        queue.add(request)
    }

    // -------------------- ACTUALIZAR USUARIO --------------------
    private fun actualizarUsuario() {
        val nombre = etNombre.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val rol = spRol.selectedItem.toString()

        val url = "https://54.144.226.230/actualizarUsuario.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                val json = JSONObject(response)
                if (json.getInt("estado") == 1) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show() }) {
            override fun getParams(): MutableMap<String, String> {
                val params = mutableMapOf(
                    "id_usuario" to idUsuario!!,
                    "nombre" to nombre,
                    "email" to email,
                    "rol" to rol
                )
                if (password.isNotBlank()) params["password"] = password
                return params
            }
        }
        queue.add(request)
    }
}

