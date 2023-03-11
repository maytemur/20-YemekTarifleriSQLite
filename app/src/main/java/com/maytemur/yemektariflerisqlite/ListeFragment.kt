package com.maytemur.yemektariflerisqlite

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception

class ListeFragment : Fragment() {
    var yemekIsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecyclerAdaptor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeAdapter = ListeRecyclerAdaptor(yemekIsmiListesi,yemekIdListesi)
        recyclerView.layoutManager = LinearLayoutManager (context)
        recyclerView.adapter = listeAdapter

        sqlVeriAlma()
//        println("tariff on view")
    }

    fun sqlVeriAlma(){
        println("sql veri al")
        try {
            activity?.let {
                val veritabani = it.openOrCreateDatabase("YemekKitabi", Context.MODE_PRIVATE, null)
                val cursor = veritabani.rawQuery("SELECT * FROM yemekler",null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIdIndex = cursor.getColumnIndex("id")
                yemekIdListesi.clear() //onceden kalan birsey varsa siliyoruz
                yemekIsmiListesi.clear()

                while (cursor.moveToNext()){
//                    println(cursor.getString(yemekIsmiIndex))
                    yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))


                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }


}