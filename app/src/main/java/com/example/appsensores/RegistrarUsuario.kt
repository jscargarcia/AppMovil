package com.example.appsensores

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RegistrarUsuario : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtConfirmar: EditText
    private lateinit var btnRegistrar: Button

    private val URL_REGISTRO = "http://35.168.148.150/registrarUsuario.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        txtNombre = findViewById(R.id.txt_nombre)
        txtEmail = findViewById(R.id.txt_email)
        txtPassword = findViewById(R.id.txt_password)
        txtConfirmar = findViewById(R.id.txt_confirmar)
        btnRegistrar = findViewById(R.id.btn_guardar_usuario)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nombre = txtNombre.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()
        val confirmar = txtConfirmar.text.toString().trim()

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Completa todos los campos", SweetAlertDialog.WARNING_TYPE)
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarAlerta("Email inválido", "Ingresa un correo válido", SweetAlertDialog.ERROR_TYPE)
            return
        }

        if (password != confirmar) {
            mostrarAlerta("Contraseñas no coinciden", "Revisa la contraseña", SweetAlertDialog.WARNING_TYPE)
            return
        }

        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.titleText = "Registrando..."
        dialog.setCancelable(false)
        dialog.show()

        val request = object : StringRequest(Method.POST, URL_REGISTRO,
            { response ->
                dialog.dismissWithAnimation()
                Log.d("RESPUESTA_PHP", response)
                if (response.contains("\"estado\":1")) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("¡Registrado!")
                        .setContentText("Usuario creado correctamente")
                        .setConfirmText("Ir al Login")
                        .setConfirmClickListener {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .show()
                } else {
                    // Muestra la respuesta exacta del servidor para depuración
                    mostrarAlerta("Error", response, SweetAlertDialog.ERROR_TYPE)
                }
            },
            { error ->
                dialog.dismissWithAnimation()
                mostrarAlerta("Error", "No se pudo conectar al servidor", SweetAlertDialog.ERROR_TYPE)
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password,
                    "rol" to "OPERADOR",
                    "id_departamento" to "1"
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }

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








