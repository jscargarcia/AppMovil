package com.example.appsensores

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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
    private var idUsuario = ""  // Si edita, viene lleno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_editar_usuario)

        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        idUsuario = intent.getStringExtra("id_usuario") ?: ""

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        spRol = findViewById(R.id.spRol)
        btnGuardar = findViewById(R.id.btnGuardar)

        if (idUsuario.isNotEmpty()) {
            // MODO EDICIÓN: cargar datos del usuario
            cargarDatosUsuario()
        }

        btnGuardar.setOnClickListener {
            guardarUsuario()
        }
    }

    private fun cargarDatosUsuario() {
        val url = "http://35.168.148.150/obtenerUsuario.php?id_usuario=$idUsuario"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    etNombre.setText(json.getString("nombre"))
                    etEmail.setText(json.getString("email"))
                    
                    val rol = json.getString("rol")
                    spRol.setSelection(if (rol == "ADMIN") 0 else 1)
                    
                    // No mostrar contraseña por seguridad
                    etPassword.hint = "Dejar vacío para mantener contraseña actual"
                } catch (e: Exception) {
                    Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Complete nombre y email", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar contraseña solo si es nuevo usuario
        if (idUsuario.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val rolSeleccionado = spRol.selectedItem.toString()

        if (idUsuario.isEmpty()) {
            // AGREGAR NUEVO USUARIO
            registrarUsuario(nombre, email, password, rolSeleccionado)
        } else {
            // EDITAR USUARIO EXISTENTE
            actualizarUsuario(nombre, email, password, rolSeleccionado)
        }
    }

    private fun registrarUsuario(nombre: String, email: String, password: String, rol: String) {
        val url = "http://35.168.148.150/registrarUsuario.php"
        
        val request = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getInt("estado") == 1) {
                        Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
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
        
        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarUsuario(nombre: String, email: String, password: String, rol: String) {
        val url = "http://35.168.148.150/actualizarUsuario.php"
        
        val request = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getInt("estado") == 1) {
                        Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = mutableMapOf(
                    "id_usuario" to idUsuario,
                    "nombre" to nombre,
                    "email" to email,
                    "rol" to rol
                )
                
                // Solo incluir password si se proporcionó uno nuevo
                if (password.isNotEmpty()) {
                    params["password"] = password
                }
                
                return params
            }
        }
        
        Volley.newRequestQueue(this).add(request)
    }
}
