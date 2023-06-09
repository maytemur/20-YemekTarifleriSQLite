package com.maytemur.yemektariflerisqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.navArgs
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.yemek_ekle,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.yemek_ekleme_item) {

            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
        }
        if(item.itemId ==R.id.cikis_item){
            finish()
            exitProcess(0)
        }
        return super.onOptionsItemSelected(item)
    }
}