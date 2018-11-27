package com.example.android.mandown

import android.Manifest
import android.content.Context
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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.sqrt
import android.os.CountDownTimer
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Vibrator
import android.telephony.SmsManager
import kotlinx.android.synthetic.main.content_man_down.*


class ManDown2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SensorEventListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gravity: Sensor
    private lateinit var gyroscope: Sensor
    private lateinit var linearAcceleration: Sensor
    private lateinit var rotationVector: Sensor
    private var valuesLinAcceleration = floatArrayOf(0.0f,0.0f,0.0f)
    private var valuesGravity = floatArrayOf(0.0f,0.0f,0.0f)
    private var accelGravity = floatArrayOf(0.0f,0.0f,0.0f)
    private var valuesGyroscope = floatArrayOf(0.0f,0.0f,0.0f)
    private var valuesRotation = floatArrayOf(0.0f,0.0f,0.0f)
    private var accelLin = floatArrayOf(0.0f,0.0f,0.0f)
    private var stepCounter = floatArrayOf(0.0f,0.0f,0.0f)
    private var  mAccel = 0.0f ;
    private var  mAccelCurrent= 0.0f ;
    private var  mAccelLast= 0.0f ;
    private var messageSent: Boolean = false
    private var gravityDetectionLevel: Int = 5
    private var motionCounter: Int = 0
    private var isInMovement: Boolean = true
    private var timer: Long = 30000
    private lateinit var objectTimer:CountDownTimer
    private val PERMISSION_REQUEST_CODE = 1
    var timeLeft = 30
    private lateinit var area : String

    private fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, null, null)
    }
         override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mandown2)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
             area = intent.getStringExtra("area")

        nav_view.setNavigationItemSelectedListener(this)
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        //Call the start sensor Method
        lblSensibility.text = gravityDetectionLevel.toString()
        seekBar!!.setOnSeekBarChangeListener(this)
        startingSensor()
             mProgressBar.setProgress(timeLeft)
             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_DENIED) {

            Log.d("permission", "permission denied to SEND_SMS - requesting it");
             val permissions = arrayOf(Manifest.permission.SEND_SMS)
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)

        }
    }

         objectTimer =    object : CountDownTimer(timer, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                lblCountDown.setText("seconds remaining: " + millisUntilFinished / 1000)
                timeLeft--
                mProgressBar.setProgress(timeLeft * 100 / (30000 / 1000))

                if(millisUntilFinished <= 10000 && millisUntilFinished >=5000)
                {
                    Log.d("","entro en el if del vibrador")
                    val vibe:Vibrator = this@ManDown2.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibe.vibrate(500)
                }


            }
            override fun onFinish() {
                lblCountDown.setText("done!")
             //   val intent = Intent(applicationContext,MainActivity::class.java)
             //   val pi = PendingIntent.getActivity(applicationContext, 0, intent, 0)
            //    val sms = SmsManager.getDefault()
              //  sms.sendTextMessage(txbPhoneNumber.text.toString(), null, "help me", pi, null)
                timeLeft--
                mProgressBar.setProgress(0)
                if(!messageSent)
                {

                    sendSMS(txbPhoneNumber.text.toString(), "¡Ayuda! Hombre caído en: "+area);


                    Toast.makeText(applicationContext, "Message Sent successfully!",
                            Toast.LENGTH_LONG).show()

                    lblMessageSent.text = "Si"
                    Log.d("Message Sent","Message Sent")
                    messageSent = true

                }


            }
        }.start()




    }


    //Starting the sensors
    fun startingSensor(){
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
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

    }

    //Sensor Events Listener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                Log.d("MainActivity","Linear_acceleration")
                valuesLinAcceleration[0] = event.values[0]
                valuesLinAcceleration[1] = event.values[1]
                valuesLinAcceleration[2] = event.values[2]

                tvLinearAccelX.text = event.values[0].toString()
                tvLinearAccelY.text = event.values[1].toString()
                tvLinearAccelZ.text = event.values[2].toString()
            }

            Sensor.TYPE_GRAVITY -> {
                Log.d("MainActivity","Gravity")
                valuesGravity[0] = event.values[0]
                valuesGravity[1] = event.values[1]
                valuesGravity[2] = event.values[2]

                tvGravity.text = event.values[0].toString()
                tvGravityY.text = event.values[1].toString()
                tvGravityZ.text = event.values[2].toString()
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
              //  Log.d("MainActivity","Acelerometro")
                accelGravity[0] = alpha * accelGravity[0] + (1 - alpha) * event.values[0]
                accelGravity[1] = alpha * accelGravity[1] + (1 - alpha) * event.values[1]
                accelGravity[2] = alpha * accelGravity[2] + (1 - alpha) * event.values[2]

                accelLin[0] = event.values[0] - accelGravity[0]
                accelLin[1] = event.values[1] - accelGravity[1]
                accelLin[2] = event.values[2] - accelGravity[2]

                //tvAccelerometerX.text = accelLin[0].toString()
                //tvAccelerometerY.text = accelLin[1].toString()
                //tvAccelerometerZ.text = accelLin[2].toString()

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
                   // tvAccelerometerMovement.text = "Mestoymoviendo"
                    motionCounter ++
                    lblMotionCounter.text = motionCounter.toString()
                    isInMovement = true
                    objectTimer.cancel()
                    timeLeft=30
                    objectTimer.start()

                  //  val mp = MediaPlayer.create (this, R.raw.heylisten)
                  //  mp.start ()
                }
                else{
                   // tvAccelerometerMovement.text = "Toyquieto"
                }



            }
            Sensor.TYPE_GYROSCOPE -> {
                Log.d("MainActivity","Gyroscopio")
                valuesGyroscope[0] = event.values[0]
                valuesGyroscope[1] = event.values[1]
                valuesGyroscope[2] = event.values[2]

                tvGyroscope.text = event.values[0].toString()
                tvGyroscopeY.text = event.values[1].toString()
                tvGyroscopeZ.text = event.values[2].toString()
            }

            Sensor.TYPE_ROTATION_VECTOR -> {
                Log.d("MainActivity","rotation")
                valuesRotation[0] = event.values[0]
                valuesRotation[1] = event.values[1]
                valuesRotation[2] = event.values[2]

                 tvRotationVector.text = event.values[0].toString()
                 tvRotationVectorY.text = event.values[1].toString()
                 tvRotationVectorZ.text = event.values[2].toString()
            }
            Sensor.TYPE_STEP_COUNTER -> {
                Log.d("MainActivity","stepCounter")
                stepCounter[0] = event.values[0]

                tvStepCounter.text = event.values[0].toString()
            }
        }

    }


    override fun onDestroy () {
        super.onDestroy ()
    }

    //Register and unregister eventos

    override fun onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);

        /* sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 100);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), 100);



        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), 100);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 100);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 100);
*/

    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


//-----------------------------
    //Other main listeners
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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



    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        // called when progress is changed
        lblSensibility.text = seekBar.progress.toString()
        gravityDetectionLevel = seekBar.progress

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is started
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // called when tracking the seekbar is stopped
    }


    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        objectTimer.cancel()
    }


}
