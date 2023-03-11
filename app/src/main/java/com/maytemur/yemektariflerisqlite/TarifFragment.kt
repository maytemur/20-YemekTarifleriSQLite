package com.maytemur.yemektariflerisqlite

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import androidx.navigation.Navigation
import com.maytemur.yemektariflerisqlite.databinding.ActivityMainBinding
import com.maytemur.yemektariflerisqlite.databinding.FragmentTarifBinding
import kotlinx.android.synthetic.main.fragment_tarif.*
import kotlinx.android.synthetic.main.fragment_tarif.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class TarifFragment : Fragment(R.layout.fragment_tarif) {
    private var tarifinBinding: FragmentTarifBinding? = null
    var secilenGorsel:Uri? = null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTarifBinding.bind(view)
        tarifinBinding = binding
        val imageAl = registerForActivityResult(ActivityResultContracts.GetContent(),ActivityResultCallback {
            secilenGorsel = it
            imageView5.setImageURI(it)})

        context?.let {
            button2!!.setOnClickListener {
//                kaydet(it)
                println("kaydet tıklandı")
                val yemekIsmi = yemekIsmiText5.text.toString()
                val yemekMalzemesi = yemekMalzemeText5.text.toString()
                if (secilenGorsel != null ) {
                    if(Build.VERSION.SDK_INT >=28) {
//                        println(secilenGorsel.toString())
                        val kaynak =ImageDecoder.createSource(requireContext().contentResolver, secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(kaynak)
                    }else {
                        secilenBitmap = android.provider.MediaStore.Images.Media.getBitmap(requireContext().contentResolver,
                            secilenGorsel)
                        println("burası çalıştı")
                    }
                }

                if ( secilenBitmap!= null) {
                    val kucukBitmap = kucukResimyap(secilenBitmap!!,300)
                    println("kucuk bitmap ok")
                    val vericikis = ByteArrayOutputStream()
                    kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,vericikis)
                    val byteDizisi = vericikis.toByteArray()
                    try {
                        context?.let {
                            val veritabani = it.openOrCreateDatabase("YemekKitabi", Context.MODE_PRIVATE,null)
                            veritabani.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")
                            val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES(?, ?, ?)"
                            val statement = veritabani.compileStatement(sqlString)
                            statement.bindString(1,yemekIsmi)
                            statement.bindString(2,yemekMalzemesi)
                            statement.bindBlob(3,byteDizisi)
                            statement.execute()
                            println("database tariff calisti")
                            statement.close()
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
                    Navigation.findNavController(view).navigate(action)
                }

            }
            imageView5!!.setOnClickListener {
                println("görsel seç tıklandı")
                imageAl.launch("image/*")
                //                gorselSEC(it)
            }
        }

        arguments?.let {
            var gelenBilgi= TarifFragmentArgs.fromBundle(it).bilgi

            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir yemek eklemeye geldi
                yemekIsmiText5.setText("")
                yemekMalzemeText5.setText("")
                button2.visibility = View.VISIBLE

                val gorselSecmeArkaPlani =BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                imageView5.setImageBitmap(gorselSecmeArkaPlani)

                }else {
                    //daha once olusturulan yemegi gormeye geldi
                    button2.visibility = View.INVISIBLE

                    val secilenId = TarifFragmentArgs.fromBundle(it).id

                    context?.let {
                        try {
                            val db = it.openOrCreateDatabase("YemekKitabi", Context.MODE_PRIVATE,null)
                            val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id=?", arrayOf(secilenId.toString()))

                            val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                            val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                            val yemekGorseli = cursor.getColumnIndex("gorsel")

                            while (cursor.moveToNext()){
                                yemekIsmiText5.setText(cursor.getString(yemekIsmiIndex))
                                yemekMalzemeText5.setText(cursor.getString(yemekMalzemeIndex))

                                val byteDizisi = cursor.getBlob(yemekGorseli)
                                val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                                imageView5.setImageBitmap(bitmap)
                            }
                            cursor.close()

                        } catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
        }
    }
    fun kucukResimyap (kullanicininBitmapi: Bitmap, maxBoyut: Int) : Bitmap {
        var genislik = kullanicininBitmapi.width
        var yukseklik = kullanicininBitmapi.height
        val bitmapOrani : Double = genislik.toDouble()/ yukseklik.toDouble()
        if (bitmapOrani >=1){
                // gorsel yatay
            genislik = maxBoyut
            val kisaltilmisYukseklik = genislik / bitmapOrani
            
            }else {
                //gorsel dikey
                yukseklik= maxBoyut
                val kisaltilmisGenislik= yukseklik*bitmapOrani
            }
        return Bitmap.createScaledBitmap(kullanicininBitmapi,genislik,yukseklik,true)
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        tarifinBinding = null
        super.onDestroyView()
    }
}












//                tarifinBinding!!.imageView5.setImageURI(it)
//            println("uri değeriResultCall Back $it")
//                val galeriIntent = Intent (Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                println(galeriIntent.toString())//
//                secilenGorsel = galeriIntent.data
//    fun kaydet(view: View) {
//        //SQLite'a Kaydetme
//    }
//       fun gorselSEC(view: View) {
//        if (activity?.let {
//                ContextCompat.checkSelfPermission(
//                    it.applicationContext,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            } != PackageManager.PERMISSION_GRANTED
//        ) {
//            //izin verilmedi, izin istememiz gerekiyor
//       requireActivity().requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
//            //requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        } else {
//                    //izin zaten verilmiş,tekrar istemeden galeriye git
//            val galeriIntent=Intent(Intent.ACTION_PICK,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            //startActivityForResult(galeriIntent,2)
//            //activityResultLauncher.launch(galeriIntent,2)
//            //val getImage=registerForActivityResult(ActivityResultContracts.GetContent(),
//            //ActivityResultCallback {
//            //    imageView2.setImageURI(it)
//            //})
//            //getImage.launch("image/*")
//
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
    // Inflate the layout for this fragment
    //        binding= ActivityMainBinding.inflate(layoutInflater)
    //        setContentView(binding.root)
    //        val getImage= registerForActivityResult(
    //        ActivityResultContracts.GetContent(), ActivityResultCallback {
    //            binding.fragmentContainerView.imageView5.setImageURI(it)
    //        }
    //    )
    //        context?.let {
    //        binding.fragmentContainerView.imageView5.setOnClickListener {
    //            getImage.launch("image/*")
    //        }
    //    }
    //        button.setOnClickListener {
//            activityResultLauncher.launch( arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE))
//        }
//    }
//    private val activityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()){
//        permissions -> //handle permissions granted/rejected
//        permissions.entries.forEach {
//            val permissionName = it.key
//            val isGranted = it.value
//            if(isGranted){
//                //permission is granted
//            } else {
//                //permission is denied
//            }
//        }


