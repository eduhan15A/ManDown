package com.example.android.mandown

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_man_down.*
import kotlinx.android.synthetic.main.app_bar_man_down.*
import android.os.CountDownTimer
import android.util.Log
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_man_down.*
import kotlin.math.sqrt


//First Screen With Area Selection

class ManDown : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    lateinit var mCountDownTimer: CountDownTimer
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gravity: Sensor
    private lateinit var gyroscope: Sensor
    private lateinit var linearAcceleration: Sensor
    private lateinit var rotationVector: Sensor
    var i = 30
    private val PERMISSION_REQUEST_CODE = 1
    private var accelGravity = floatArrayOf(0.0f,0.0f,0.0f)
    private var accelLin = floatArrayOf(0.0f,0.0f,0.0f)
    private var  mAccel = 0.0f ;
    private var  mAccelCurrent= 0.0f ;
    private var  mAccelLast= 0.0f ;
    private var messageSent: Boolean = false
    private var gravityDetectionLevel: Int = 5
    private var motionCounter: Int = 0
    private var isInMovement: Boolean = true
    private var timer: Long = 30000
    private var mLight: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_man_down)
        setSupportActionBar(toolbar)

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        //Call the start sensor Method
       // lblSensibility.text = gravityDetectionLevel.toString()
        startingSensor()

        mProgressBar.setProgress(i)



        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                val permissions = arrayOf(Manifest.permission.SEND_SMS)
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)

            }

        }
        mCountDownTimer = object : CountDownTimer(30000, 1000) {


            override fun onTick(millisUntilFinished: Long) {
                Log.d("Log_tag", "Tick of Progress$i$millisUntilFinished")
                i--
                mProgressBar.setProgress(i * 100 / (30000 / 1000))

            }

            override fun onFinish() {
                //Do what you want
                i--
                mProgressBar.setProgress(0)
            }
        }
    }
    //Starting the sensors
    fun startingSensor(){
        Log.d("Sensor","Starting")
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
            Log.d("Sensor","Started Accelerometer")
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let {
            this.gravity = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            this.gyroscope = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            this.linearAcceleration = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            this.rotationVector = it
        }
        // Get the default sensor of specified type
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    //Sensor Events Listener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("MY_APP", event.toString())
        Log.d("Sensor","Sensor Changed")
        Log.d("Sensor",event?.sensor?.type.toString())

        when (event?.sensor?.type) {


            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
                Log.d("MainActivity","Acelerometro")
                accelGravity[0] = alpha * accelGravity[0] + (1 - alpha) * event.values[0]
                accelGravity[1] = alpha * accelGravity[1] + (1 - alpha) * event.values[1]
                accelGravity[2] = alpha * accelGravity[2] + (1 - alpha) * event.values[2]

                accelLin[0] = event.values[0] - accelGravity[0]
                accelLin[1] = event.values[1] - accelGravity[1]
                accelLin[2] = event.values[2] - accelGravity[2]

                tvAccelerometerX.text = accelLin[0].toString()
                tvAccelerometerY.text = accelLin[1].toString()
                tvAccelerometerZ.text = accelLin[2].toString()

                val x = accelGravity[0]
                val y = accelGravity[1]
                val z = accelGravity[2]
                mAccelLast = mAccelCurrent
                mAccelCurrent = sqrt((x * x + y * y + z * z))
                val delta = mAccelCurrent - mAccelLast
                mAccel = mAccel * 0.9f + delta
                // Make this higher or lower according to how much
                // motion you want to detect
                if (mAccel > gravityDetectionLevel) {
                    // do something
                    Log.d("Accelerometer","Is in movement")
                    tvAccelerometerMovement.text = "Mestoymoviendo"
                    motionCounter ++
                    lblMotionCounter.text = motionCounter.toString()
                    isInMovement = true
                   // mCountDownTimer.cancel()
                   // mCountDownTimer.start()

                    //  val mp = MediaPlayer.create (this, R.raw.heylisten)
                    //  mp.start ()
                }
                else{
                    tvAccelerometerMovement.text = "Toyquieto"
                }
            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.man_down, menu)
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

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

}
