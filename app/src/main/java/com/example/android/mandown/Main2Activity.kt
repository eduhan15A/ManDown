package com.example.android.mandown

import android.Manifest
import android.arch.persistence.room.Room
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.telephony.SmsManager
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.android.mandown.Main2Activity.Companion.database
import com.example.android.mandown.R.id.*
import kotlinx.android.synthetic.main.content_main2.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      }


    companion object {
         lateinit var  inst: Main2Activity
        fun instances(): Main2Activity {
            return inst
        }

        lateinit var database: TasksDatabase
    }


     lateinit var areas:  MutableList<TaskEntity>
    private val PERMISSION_REQUEST_CODE = 1
    lateinit var oSettings: tSettings

    override fun onStart() {
        super.onStart()
        inst = this
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
          //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show()

            //  Toast.makeText(this, "holi", Toast.LENGTH_SHORT).show()
          //  val myIntent = Intent(this, ManDown2::class.java)
          //   myIntent.putExtra("area", spinner!!.selectedItem.toString()) //Optional parameters
           //startActivity(myIntent)


                       // if(lblphone.text != ""){
                          //  sendSMS(oSettings.cel,"Solicitando autorización para ingresar al area de: "+ spinner!!.selectedItem.toString())
                          //  val sms = SmsManager.getDefault()

            doAsync {

                if (Main2Activity.database.taskDao().getAllSettings().size != 0) {
                    oSettings = Main2Activity.database.taskDao().getAllSettings().first()
                }

                if(oSettings.cel != ""){
                            var message: String = "Solicitando autorizacion para ingresar al area de: "+ spinner!!.selectedItem.toString()
                            var oSms: SmsSender = SmsSender()
                            oSms.sendSMS(oSettings.cel,message)

                            Snackbar.make(view, "Se ha enviado mensaje solicitando autorización al número: " + oSettings.cel , Snackbar.LENGTH_LONG)
                                   .setAction("Mensaje", null).show()

                        }
                        else{
            Snackbar.make(view, "No se ha configurado ningún celular, configúralo en Settings", Snackbar.LENGTH_LONG)
                    .setAction("Mensaje", null).show()
        }

            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        spinner!!.setOnItemSelectedListener(this)
        createDatabase()
        toolbar.setNavigationIcon(null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                val permissions = arrayOf(Manifest.permission.SEND_SMS)
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)

            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                val permissions = arrayOf(Manifest.permission.RECEIVE_SMS)
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)

            }
        }



      /*  doAsync {
            if (Main2Activity.database.taskDao().getAllSettings().size != 0){
                oSettings = Main2Activity.database.taskDao().getAllSettings().first()

                uiThread {
                  lblphone.text = oSettings.cel
                }
            }
        }
        */
        configureReceiver()
    }

    private fun configureReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECIEVED")
        val receiver = MyReceiver()
        registerReceiver(receiver, filter)
    }

     fun goForward(cellphoneAux: String , messageAux: String){


         doAsync {
             if (Main2Activity.database.taskDao().getAllSettings().size != 0){
                 oSettings = Main2Activity.database.taskDao().getAllSettings().first()

                 uiThread {
                    var celsender :String = oSettings.cel

                     if(celsender == cellphoneAux){
                         if(messageAux.contains("Ok")){

                             Toast.makeText(applicationContext,"Se ha recibido la autorización por parte del número guardado en la aplicación, se puede iniciar la actividad",Toast.LENGTH_LONG).show()
                             val myIntent = Intent(applicationContext, ManDown2::class.java)
                             myIntent.putExtra("area", spinner!!.selectedItem.toString()) //Optional parameters
                             startActivity(myIntent)
                         }
                         else{

                             Toast.makeText(applicationContext,"Se esperaba un mensaje de confirmación con una respuesta que contuviera la palabra Ok",Toast.LENGTH_LONG).show()

                         }



                     }else{

                         Toast.makeText(applicationContext,"Se recibió un mensaje por parte de otro celular diferente al guardado en la aplicación. Favor de enviarlo desde ese número o cambiar el numero en Settings" ,Toast.LENGTH_LONG).show()

                     }

                 }
             }
         }


      //  Log.d("Tag","Going forward")
    }


    fun fillSpinner(){
         var areasNombres:  MutableList<String> = arrayListOf()
        var cellphones: MutableList<String> = arrayListOf()
        areas.forEach {
            areasNombres.add(it.area.toString())
            cellphones.add(it.cel.toString())

        }


        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, areasNombres)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)

    }


    fun createDatabase(){
        Main2Activity.database =  Room.databaseBuilder(this, TasksDatabase::class.java, "tasks-db").build()

        doAsync {

            if( database.taskDao().getAllTasks().size == 0 )
                database.taskDao().insertAll(*populateData())

            if(database.taskDao().getAllSettings().size==0)
                database.taskDao().addSetting( tSettings(1,"",3))


             areas =  database.taskDao().getAllTasks().toMutableList()
            uiThread {
                fillSpinner()
            }
        }



    //   Log.d("database", database.taskDao().getAllTasks().size.toString() )



    }


    fun populateData(): Array<TaskEntity> {
        return arrayOf<TaskEntity>(
                TaskEntity(1, "BO Cuarto de resinas norte", "1234"),
                TaskEntity(2, "BO Cuarto de resinas sur", "1234"),
                TaskEntity(3, "BO Trinchera Norte", "1234"),
                TaskEntity(4, "BO Trinchera Sur", "1234"),
                TaskEntity(5, "BO Cuarto de Utilities BO Norte", "1234"),
                TaskEntity(6, "BO Cuarto de Utilities BO Sur", "1234"),
                TaskEntity(7, "BO Mezzanine Norte (Bombas de resinas)", "1234"),
                TaskEntity(8, "BO Mezzanine Sur (Bombas de resinas)", "1234"),
                TaskEntity(9, "BO Subestacion Electrica BO 2", "1234"),
                TaskEntity(10, "BO Subestacion Electrica BO 1", "1234"),
                TaskEntity(11, "BO Bodega de limpieza BO", "1234"),
                TaskEntity(12, "BO Bodega de mantenimiento", "1234"),
                TaskEntity(13, "BO Cuarto Generador de Vapor", "1234"),
                TaskEntity(14, "BO Techumbre de BO ", "1234"),
                TaskEntity(15, "BO BEDATEC (Hojas) ", "1234"),
                TaskEntity(16, "BO P.K.S (Hojas)", "1234"),
                TaskEntity(17, "BO Cuarto de Servicios de Blades", "1234"),
                TaskEntity(18, "BO Cuarto de residuos orgánicos BO", "1234"),
                TaskEntity(19, "BO Cuarto de IT BO", "1234"),
                TaskEntity(20, "Exterior Planta de emergencia de BO", "1234"),
                TaskEntity(21, "Exterior Camino periferico de planta", "1234"),
                TaskEntity(22, "Exterior Parte superior Silos Sur", "1234"),
                TaskEntity(23, "Exterior Parte superior Silos Norte", "1234"),
                TaskEntity(24, "Exterior Torres de enfriamiento", "1234"),
                TaskEntity(25, "Exterior Cuarto de bombeo", "1234"),
                TaskEntity(26, "Exterior Pozo", "1234"),
                TaskEntity(27, "Exterior Planta de Agua Potable", "1234"),
                TaskEntity(28, "Exterior Planta de Tratamiento de aguas residuales", "1234"),
                TaskEntity(29, "Exterior Área de tanques de distribución de agua del sistema de contra incendio PC", "1234"),
                TaskEntity(30, "Exterior Cuarto de Bombas 1 FP", "1234"),
                TaskEntity(31, "Exterior Cuarto de Bombas 2 FP", "1234"),
                TaskEntity(32, "Exterior Canal Norte para agua pluvial", "1234"),
                TaskEntity(33, "Exterior Canal Sur para agua pluvial", "1234"),
                TaskEntity(34, "Exterior Tanque de Hidrogeno", "1234"),
                TaskEntity(35, "Exterior Tanque de Nitrogeno", "1234"),
                TaskEntity(36, "Exterior Cuarto de Residuos Peligrosos", "1234"),
                TaskEntity(37, "Exterior Cuarto de Materiales Peligrosos", "1234"),
                TaskEntity(38, "Exterior Cuarto de Obsoletos", "1234"),
                TaskEntity(39, "Exterior Subestacion Principal ", "1234"),
                TaskEntity(40, "Exterior Estacion de Gas LP ", "1234"),
                TaskEntity(41, "Exterior Bomba 1 de descarga de Diesel", "1234"),
                TaskEntity(42, "Exterior Bomba 2 de descarga de Diesel", "1234"),
                TaskEntity(43, "Exterior Area de tanques pulmon BO-PC", "1234"),
                TaskEntity(44, "Exterior Cuarto de lubricantes", "1234"),
                TaskEntity(45, "Exterior Bodega de Control de plagas", "1234"),
                TaskEntity(46, "Exterior Laguna de coleccion de agua pluvial", "1234"),
                TaskEntity(47, "Exterior Bodega de Jardineria", "1234"),
                TaskEntity(48, "Exterior Subestacion electrica de torres de enfriamiento", "1234"),
                TaskEntity(49, "Exterior Antena de TELCEL ", "1234"),
                TaskEntity(50, "Exterior Sala de capacitacion de contratistas", "1234"),
                TaskEntity(51, "Exterior Evaporadora", "1234"),
                TaskEntity(52, "Exterior Area de residuos (exterior)", "1234"),
                TaskEntity(53, "PC Bodega de Limpieza PC ", "1234"),
                TaskEntity(54, "PC Cuarto de Utilities PC", "1234"),
                TaskEntity(55, "PC Techumbre PC ", "1234"),
                TaskEntity(56, "PC Cuarto de residuos organicos PC", "1234"),
                TaskEntity(57, "PC Cuarto de baterias ", "1234"),
                TaskEntity(58, "PC Cuarto de IT PC ", "1234"),
                TaskEntity(59, "PC Planta de emergencia de PC ", "1234"),
                TaskEntity(60, "PC Laboratorio de Pack Dev ", "1234"),
                TaskEntity(61, "PC Area de residuos (interior) ", "1234")



        )
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onSendClick(view: View){
      //  Toast.makeText(this, "holi", Toast.LENGTH_SHORT).show()
      //  val myIntent = Intent(this, ManDown::class.java)
       // myIntent.putExtra("key", value) //Optional parameters
       // startActivity(myIntent)



    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {Log.d("Settings","test")
                val myIntent = Intent(this, Settings::class.java)
                // myIntent.putExtra("key", value) //Optional parameters
                startActivity(myIntent)

            return true}
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
