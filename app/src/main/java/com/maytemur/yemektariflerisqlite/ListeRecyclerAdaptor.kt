package com.maytemur.yemektariflerisqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdaptor (val yemekListesi : ArrayList<String>,
                            val idListesi: ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerAdaptor.YemekListesiHolder> (){

    class YemekListesiHolder (itemView: View): RecyclerView.ViewHolder (itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekListesiHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return YemekListesiHolder(view)
    }

    override fun onBindViewHolder(holder: YemekListesiHolder, position: Int) {
        holder.itemView.recycle_row_text.text = yemekListesi[position]
        holder.itemView.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

}