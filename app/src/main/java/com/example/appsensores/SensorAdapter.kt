package com.example.appsensores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SensorAdapter(
    private val ctx: Context,
    private val sensores: MutableList<Sensor>
) : ArrayAdapter<Sensor>(ctx, 0, sensores) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(ctx)
            .inflate(R.layout.item_sensor, parent, false)

        val sensor = sensores[position]

        val txtCodigo = view.findViewById<TextView>(R.id.txtCodigo)
        val txtTipo = view.findViewById<TextView>(R.id.txtTipo)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        val btnCambiar = view.findViewById<Button>(R.id.btnCambiar)

        txtCodigo.text = "Código: ${sensor.codigo_sensor}"
        txtTipo.text = "Tipo: ${sensor.tipo}"
        txtEstado.text = "Estado: ${sensor.estado}"

        if (sensor.estado != "ACTIVO") {
            btnCambiar.isEnabled = false
            btnCambiar.alpha = 0.5f
        } else {
            btnCambiar.isEnabled = true
            btnCambiar.alpha = 1f
        }


        btnCambiar.setOnClickListener {
            cambiarEstado(sensor.id_sensor)
        }

        return view
    }

    private fun cambiarEstado(id: String) {
        val url = "http://54.144.226.230/cambiar_estado_sensor.php?id_sensor=$id"

        val request = StringRequest(
            Request.Method.GET, url,
            {
                Toast.makeText(ctx, "Estado actualizado", Toast.LENGTH_SHORT).show()
                (ctx as GestionSensores).cargarSensores()  // REFRESCA LA LISTA
            },
            {
                Toast.makeText(ctx, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(ctx).add(request)
    }
}


