package com.example.android.mandown

import android.os.Bundle
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.sqrt
import android.content.Intent



class Settings : Activity(), SensorEventListener, SeekBar.OnSeekBarChangeListener {
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
    private lateinit var objectTimer: CountDownTimer
    private val PERMISSION_REQUEST_CODE = 1
    var timeLeft = 30


    lateinit var oSettings: tSettings

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);
    }
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {


            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
                //  Log.d("MainActivity","Acelerometro")
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
                    motionCounter ++
                    isInMovement = true
                    objectTimer.cancel()
                    timeLeft=30
                    objectTimer.start()
                }
                else{
                }


            }

        }


    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        lblSensibility.text = seekBar?.progress.toString()
        gravityDetectionLevel = seekBar?.progress!!


        if(gravityDetectionLevel == 0 ){
            seekBar.setProgress(1)
            lblSensibility.text = seekBar?.progress.toString()
            gravityDetectionLevel = seekBar?.progress!!

        }

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        startingSensor()


        objectTimer =    object : CountDownTimer(timer, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft--
                mProgressBar.setProgress(timeLeft * 100 / (30000 / 1000))
            }
            override fun onFinish() {
                timeLeft--
                mProgressBar.setProgress(0)

            }
        }.start()
        seekBar!!.setOnSeekBarChangeListener(this)
        readFromDB()

    }

    fun readFromDB(){
      //  Main2Activity.database =  Room.databaseBuilder(this, TasksDatabase::class.java, "tasks-db").build()

        doAsync {


            if (Main2Activity.database.taskDao().getAllSettings().size != 0){
                oSettings = Main2Activity.database.taskDao().getAllSettings().first()

            uiThread {
                seekBar.setProgress(oSettings.sensibility)
                gravityDetectionLevel = seekBar?.progress!!
                lblSensibility.text = oSettings.sensibility.toString()
                txbPhoneNumber.setText(oSettings.cel.toString())
            }
        }
        }
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

    fun  onSave (view: View)  {
        Log.d("Settings", "Save")
        doAsync {
                Main2Activity.database.taskDao().updateSettings(tSettings(1,txbPhoneNumber.text.toString(),seekBar.progress))


                uiThread {
                        toast("Configuraciones guardadas con éxito")

                }
            }
        }

    fun goBack(view: View){
        val returnIntent = Intent()
        returnIntent.putExtra("result", "Hey, I received your intent!")
        setResult(1, returnIntent)
        finish() // this finish method has to be called in order for the MainActivity to receive the result

    }

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()





}
