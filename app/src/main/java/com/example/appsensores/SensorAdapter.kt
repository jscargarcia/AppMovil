package com.example.appsensores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

interface OnEstadoCambiadoListener {
    fun onEstadoCambiado()
}

class SensorAdapter(
    private val ctx: Context,
    private val sensores: MutableList<Sensor>,
    private val listener: OnEstadoCambiadoListener
) : ArrayAdapter<Sensor>(ctx, 0, sensores) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(ctx)
            .inflate(R.layout.item_sensor, parent, false)

        val sensor = sensores[position]

        val txtCodigo = view.findViewById<TextView>(R.id.txtCodigo)
        val txtTipo = view.findViewById<TextView>(R.id.txtTipo)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        val txtUsuario = view.findViewById<TextView>(R.id.txtUsuario)
        val btnActivarDesactivar = view.findViewById<Button>(R.id.btnActivarDesactivar)
        val btnMarcarPerdido = view.findViewById<Button>(R.id.btnMarcarPerdido)
        val btnMarcarBloqueado = view.findViewById<Button>(R.id.btnMarcarBloqueado)

        txtCodigo.text = "Código: ${sensor.codigo_sensor}"
        txtTipo.text = "Tipo: ${sensor.tipo}"
        txtEstado.text = "Estado: ${sensor.estado}"
        txtUsuario.text = "Usuario: ${sensor.nombre_usuario ?: "Sin asignar"}"

        // Colorear el estado según el valor
        when (sensor.estado) {
            "ACTIVO" -> txtEstado.setTextColor(ctx.getColor(android.R.color.holo_green_dark))
            "INACTIVO" -> txtEstado.setTextColor(ctx.getColor(android.R.color.darker_gray))
            "PERDIDO" -> txtEstado.setTextColor(ctx.getColor(android.R.color.holo_orange_dark))
            "BLOQUEADO" -> txtEstado.setTextColor(ctx.getColor(android.R.color.holo_red_dark))
        }

        // Configurar botón Activar/Desactivar
        if (sensor.estado == "ACTIVO") {
            btnActivarDesactivar.text = "Desactivar"
            btnActivarDesactivar.backgroundTintList = ctx.getColorStateList(android.R.color.darker_gray)
        } else {
            btnActivarDesactivar.text = "Activar"
            btnActivarDesactivar.backgroundTintList = ctx.getColorStateList(android.R.color.holo_green_dark)
        }

        // Deshabilitar botones si está bloqueado o perdido
        if (sensor.estado == "BLOQUEADO" || sensor.estado == "PERDIDO") {
            btnActivarDesactivar.isEnabled = false
            btnActivarDesactivar.alpha = 0.5f
        } else {
            btnActivarDesactivar.isEnabled = true
            btnActivarDesactivar.alpha = 1f
        }

        // Listeners
        btnActivarDesactivar.setOnClickListener {
            val nuevoEstado = if (sensor.estado == "ACTIVO") "INACTIVO" else "ACTIVO"
            cambiarEstado(sensor.id_sensor, nuevoEstado)
        }

        btnMarcarPerdido.setOnClickListener {
            cambiarEstado(sensor.id_sensor, "PERDIDO")
        }

        btnMarcarBloqueado.setOnClickListener {
            cambiarEstado(sensor.id_sensor, "BLOQUEADO")
        }

        return view
    }

    private fun cambiarEstado(id: String, nuevoEstado: String) {
        val url = "http://35.168.148.150/actualizarEstadoSensor.php?id_sensor=$id&estado=$nuevoEstado"

        val request = StringRequest(
            Request.Method.GET, url,
            {
                Toast.makeText(ctx, "Estado actualizado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                listener.onEstadoCambiado()  // Callback seguro
            },
            {
                Toast.makeText(ctx, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(ctx).add(request)
    }
}


