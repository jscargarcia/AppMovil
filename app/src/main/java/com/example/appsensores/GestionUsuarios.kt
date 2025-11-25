package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONArray
import org.json.JSONObject

class GestionUsuarios : AppCompatActivity() {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var adapter: UsuariosAdapter
    private var usuariosList = mutableListOf<Usuario>()

    private var idDepartamento = ""
    private var rol = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        rol = intent.getStringExtra("rol") ?: ""

        // Validar que solo ADMIN pueda acceder
        if (rol != "ADMIN") {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Acceso denegado")
                .setContentText("Solo administradores")
                .show()
            finish()
            return
        }

        rvUsuarios = findViewById(R.id.rvUsuarios)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        adapter = UsuariosAdapter(
            usuariosList,
            onEditar = { usuario -> abrirEditarUsuario(usuario) }
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
        listarUsuarios()
    }

    // -------------------- LISTAR USUARIOS --------------------
    private fun listarUsuarios() {
        val url = "http://35.168.148.150/listarUsuarios.php?id_departamento=$idDepartamento"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET, url,
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
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al listar usuarios")
                    .show()
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
        val url = "http://35.168.148.150/cambiarEstadoUsuario.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                val json = JSONObject(response)
                if (json.getInt("estado") == 1) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Listo")
                        .setContentText("Estado cambiado correctamente")
                        .show()
                    listarUsuarios()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No se pudo cambiar el estado")
                        .show()
                }
            },
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de red")
                    .setContentText("No se pudo conectar al servidor")
                    .show()
            }
        ) {
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




