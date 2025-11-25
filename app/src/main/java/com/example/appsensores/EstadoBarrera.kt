package com.example.appsensores

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject

class EstadoBarrera : AppCompatActivity() {

    private lateinit var txtEstadoBarrera: TextView
    private lateinit var btnActualizar: Button
    private lateinit var btnAbrir: Button
    private lateinit var btnCerrar: Button
    private val handler = Handler(Looper.getMainLooper())
    private var idDepartamento: String = ""
    private var idUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estado_barrera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idDepartamento = intent.getStringExtra("id_departamento") ?: ""
        idUsuario = intent.getStringExtra("id_usuario") ?: ""

        txtEstadoBarrera = findViewById(R.id.txt_estado_barrera)
        btnActualizar = findViewById(R.id.btn_actualizar_estado)
        btnAbrir = findViewById(R.id.btn_abrir_barrera_estado)
        btnCerrar = findViewById(R.id.btn_cerrar_barrera_estado)

        btnActualizar.setOnClickListener { consultarEstado() }
        btnAbrir.setOnClickListener { controlarBarrera("ABRIR") }
        btnCerrar.setOnClickListener { controlarBarrera("CERRAR") }

        consultarEstado()
        iniciarActualizacionAutomatica()
    }

    private fun consultarEstado() {
        val url = "http://35.168.148.150/estadoBarrera.php?id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val estado = json.getString("estado")
                    actualizarUI(estado)
                } catch (e: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Error al procesar respuesta: ${e.message}")
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al consultar estado: $error")
                    .show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun controlarBarrera(accion: String) {
        val url = "http://35.168.148.150/barrera.php?accion=$accion&id_usuario=$idUsuario&id_departamento=$idDepartamento"
        val request = StringRequest(Request.Method.GET, url,
            { _ ->
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Acción ejecutada")
                    .setContentText("Barrera: $accion")
                    .show()

                consultarEstado()
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al controlar barrera: $error")
                    .show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarUI(estado: String) {
        txtEstadoBarrera.text = "Estado: $estado"
        when (estado.uppercase()) {
            "ABIERTA", "ABIERTO" -> {
                txtEstadoBarrera.setTextColor(getColor(android.R.color.holo_green_dark))
                btnAbrir.isEnabled = false
                btnCerrar.isEnabled = true
            }
            "CERRADA", "CERRADO" -> {
                txtEstadoBarrera.setTextColor(getColor(android.R.color.holo_red_dark))
                btnAbrir.isEnabled = true
                btnCerrar.isEnabled = false
            }
            else -> {
                txtEstadoBarrera.setTextColor(getColor(android.R.color.darker_gray))
                btnAbrir.isEnabled = true
                btnCerrar.isEnabled = true
            }
        }
    }

    private fun iniciarActualizacionAutomatica() {
        val runnable = object : Runnable {
            override fun run() {
                consultarEstado()
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
