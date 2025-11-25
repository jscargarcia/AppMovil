package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtClave: EditText
    private lateinit var btnIngresar: Button
    private lateinit var btnRegistrar: Button
    private lateinit var btnRecuperar: Button

    private val URL_LOGIN = "http://35.168.148.150/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular XML → Kotlin
        txtEmail = findViewById(R.id.email)
        txtClave = findViewById(R.id.clave)
        btnIngresar = findViewById(R.id.btn_ingresar)
        btnRegistrar = findViewById(R.id.btn_registrar)
        btnRecuperar = findViewById(R.id.btn_recuperar)

        btnIngresar.setOnClickListener { realizarLogin() }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistrarUsuario::class.java))
        }
    }

    private fun realizarLogin() {
        val email = txtEmail.text.toString().trim()
        val clave = txtClave.text.toString().trim()

        if (email.isEmpty() || clave.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Completa todos los campos", SweetAlertDialog.WARNING_TYPE)
            return
        }

        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.titleText = "Iniciando sesión..."
        dialog.setCancelable(false)
        dialog.show()

        val request = object : StringRequest(
            Method.POST, URL_LOGIN,
            { response ->
                dialog.dismissWithAnimation()
                Log.d("RESPUESTA_LOGIN", response)
                try {
                    val json = JSONObject(response)
                    if (json.getString("estado") == "1") {
                        val rol = json.getString("rol")
                        val idUsuario = json.getString("id_usuario")
                        val idDepartamento = json.getString("id_departamento")

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Bienvenido!")
                            .setContentText("Ingreso correcto ($rol)")
                            .setConfirmText("Continuar")
                            .setConfirmClickListener {
                                val intent = if (rol == "ADMIN") {
                                    Intent(this, HomeAdminActivity::class.java)
                                } else {
                                    Intent(this, HomeOperadorActivity::class.java)
                                }
                                intent.putExtra("rol", rol)
                                intent.putExtra("id_usuario", idUsuario)
                                intent.putExtra("id_departamento", idDepartamento)
                                startActivity(intent)
                                finish()
                            }
                            .show()
                    } else {
                        mostrarAlerta("Error", "Credenciales incorrectas", SweetAlertDialog.ERROR_TYPE)
                    }
                } catch (e: Exception) {
                    mostrarAlerta("Error JSON", e.message ?: "Error al procesar la respuesta", SweetAlertDialog.ERROR_TYPE)
                }
            },
            { error ->
                dialog.dismissWithAnimation()
                mostrarAlerta("Error Servidor", "No se pudo conectar al servidor.\n$error", SweetAlertDialog.ERROR_TYPE)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "email" to email,
                    "password" to clave
                )
            }
        }

        // Aumentar timeout a 30 segundos
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarAlerta(titulo: String, msg: String, tipo: Int) {
        SweetAlertDialog(this, tipo)
            .setTitleText(titulo)
            .setContentText(msg)
            .setConfirmText("OK")
            .show()
    }
}



