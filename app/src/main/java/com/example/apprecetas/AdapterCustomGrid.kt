package com.example.apprecetas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AdapterCustomGrid(var contexto: Context, items:ArrayList<Receta>) : BaseAdapter() {
    // Lista de contactos que se mostrará
    var laListaDeContactos: ArrayList<Receta>? = null
    //copia de la lista de contacto
    var laCopiaDeListaContacto: ArrayList<Receta>? = null

    // Inicializador donde se asigna la lista de contactos al adaptador
    init {
        this.laListaDeContactos = ArrayList(items)
        this.laCopiaDeListaContacto = laListaDeContactos
    }
    // Método para obtener la vista de cada elemento en la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder:ViewHolder? = null // ViewHolder para optimizar la carga de vistas
        var vista:View? = convertView
        // Si la vista es nula, se infla una nueva vista
        if (vista == null){
            vista = LayoutInflater.from(contexto).inflate(R.layout.item_receta_grid, null)
            viewHolder = ViewHolder(vista)
            vista.tag = viewHolder // Se guarda el ViewHolder en la vista
        }else{
            viewHolder = vista.tag as? ViewHolder
        }
        val item = getItem(position) as Receta

        viewHolder?.nombre?.text = item.nombre

        return vista!!
    }
    override fun getCount(): Int {
        return this.laListaDeContactos?.count() ?: 0
    }

    fun addItem(item:Receta){
        laCopiaDeListaContacto?.add(item)
        laListaDeContactos = ArrayList(laCopiaDeListaContacto)
        notifyDataSetChanged()
    }

    fun removeItem(item:Int){
        laCopiaDeListaContacto?.removeAt(item)
        laListaDeContactos = ArrayList(laCopiaDeListaContacto)
        notifyDataSetChanged()
    }

    fun updateItem(item:Int, newItem:Receta){
        laCopiaDeListaContacto?.set(item, newItem)
        laListaDeContactos = ArrayList(laCopiaDeListaContacto)
        notifyDataSetChanged()
    }

    //funcion para filtrar cuando se utiliza la busqueda
    fun filtrar(elValoraBuscar:String){
        // Limpiamos la lista de contactos existente
        laListaDeContactos?.clear()

        // Si el valor a buscar está vacío, restauramos la lista original
        if (elValoraBuscar.isEmpty()) {
            laListaDeContactos = laCopiaDeListaContacto?.let { ArrayList(it) }
        } else {
            // Convertimos la búsqueda a minúsculas para comparación insensible a mayúsculas
            val busqueda = elValoraBuscar.lowercase()

            // Filtramos la lista original según el nombre del contacto
            laListaDeContactos = laCopiaDeListaContacto?.filter {
                it.nombre.lowercase().contains(busqueda)
            }?.toMutableList() as ArrayList<Receta>?
        }

        // Notificamos cambios en la lista
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any? {
        return this.laListaDeContactos?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Clase ViewHolder para almacenar las vistas de los elementos y mejorar el rendimiento
    private class ViewHolder(vista:View){
        var nombre: TextView? = null
        var foto: ImageView? = null

        // Inicialización de las vistas obtenidas de la vista de contacto
        init {
            nombre = vista.findViewById(R.id.tvRecetaName)
            foto = vista.findViewById(R.id.imgRecetaPhoto)
        }
    }
}