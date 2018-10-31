package com.example.android.mandown

import android.arch.persistence.room.Room
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
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_main2.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener  {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      }

    companion object {
        lateinit var database: TasksDatabase
    }

     lateinit var areas:  MutableList<TaskEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
          //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show()

            //  Toast.makeText(this, "holi", Toast.LENGTH_SHORT).show()
            val myIntent = Intent(this, ManDown::class.java)
            // myIntent.putExtra("key", value) //Optional parameters
            startActivity(myIntent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        spinner!!.setOnItemSelectedListener(this)


        createDatabase()



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

             areas =  database.taskDao().getAllTasks().toMutableList()
            uiThread {
                fillSpinner()
            }
        }



    //   Log.d("database", database.taskDao().getAllTasks().size.toString() )



    }


    fun populateData(): Array<TaskEntity> {
        return arrayOf<TaskEntity>(TaskEntity(1, "Area 1", "1234"),
                TaskEntity(2, "Area 2", "00000"), TaskEntity(3, "Area 3", "5555"))
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
        val myIntent = Intent(this, ManDown::class.java)
       // myIntent.putExtra("key", value) //Optional parameters
        startActivity(myIntent)

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
            R.id.action_settings -> return true
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
