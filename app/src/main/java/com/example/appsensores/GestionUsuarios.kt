package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class GestionUsuarios : AppCompatActivity() {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var adapter: UsuariosAdapter
    private var usuariosList = mutableListOf<Usuario>()

    private var idDepartamento = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        idDepartamento = intent.getStringExtra("id_departamento") ?: ""

        rvUsuarios = findViewById(R.id.rvUsuarios)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        adapter = UsuariosAdapter(
            usuariosList,
            onEditar = { usuario -> abrirEditarUsuario(usuario) },
            onEliminar = { usuario -> cambiarEstadoUsuario(usuario.id, "INACTIVO") },
            onActivar = { usuario -> cambiarEstadoUsuario(usuario.id, "ACTIVO") }
        )
        rvUsuarios.adapter = adapter

        findViewById<Button>(R.id.btnAgregarUsuario).setOnClickListener {
            val intent = Intent(this, AgregarEditarUsuarioActivity::class.java)
            intent.putExtra("id_departamento", idDepartamento)
            startActivity(intent)
        }

        listarUsuarios()
    }

    override fun onResume() {
        super.onResume()
        listarUsuarios() // refresca la lista automáticamente al volver de agregar/editar
    }

    // -------------------- LISTAR USUARIOS --------------------
    private fun listarUsuarios() {
        val url = "https://54.144.226.230/listarUsuarios.php?id_departamento=$idDepartamento"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                usuariosList.clear()
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val u = Usuario(
                        obj.getString("id_usuario"),
                        obj.getString("nombre"),
                        obj.getString("email"),
                        obj.getString("rol"),
                        obj.getString("estado")
                    )
                    usuariosList.add(u)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error al listar usuarios", Toast.LENGTH_SHORT).show()
            })
        queue.add(request)
    }

    // -------------------- ABRIR EDITAR USUARIO --------------------
    private fun abrirEditarUsuario(usuario: Usuario) {
        val intent = Intent(this, AgregarEditarUsuarioActivity::class.java)
        intent.putExtra("id_departamento", idDepartamento)
        intent.putExtra("id_usuario", usuario.id)
        startActivity(intent)
    }

    // -------------------- ACTIVAR / DESACTIVAR --------------------
    private fun cambiarEstadoUsuario(id: String, estado: String) {
        val url = "https://54.144.226.230/cambiarEstadoUsuario.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                val json = JSONObject(response)
                if (json.getInt("estado") == 1) {
                    Toast.makeText(this, "Estado cambiado", Toast.LENGTH_SHORT).show()
                    listarUsuarios()
                } else {
                    Toast.makeText(this, "Error al cambiar estado", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error red", Toast.LENGTH_SHORT).show() }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "id_usuario" to id,
                    "estado" to estado
                )
            }
        }
        queue.add(stringRequest)
    }

    // -------------------- DATA CLASS --------------------
    data class Usuario(val id: String, val nombre: String, val email: String, val rol: String, val estado: String)
}




