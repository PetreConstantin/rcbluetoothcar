package com.example.bluetoothrccar.control

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.bluetoothrccar.MainActivity
import com.example.bluetoothrccar.R
import com.example.bluetoothrccar.blueth.SelectDeviceActivity
import com.example.bluetoothrccar.timer.TimerService
import com.example.bluetoothrccar.ui.auth.login.LoginViewModel
import java.io.IOException
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*
import kotlin.math.roundToInt


class ControlActivity : AppCompatActivity() {

    //Button Variables
    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn_disconnect: Button
    lateinit var btn_stop: Button
    lateinit var btn_up: AppCompatImageButton
    lateinit var btn_down: AppCompatImageButton
    lateinit var btn_left: AppCompatImageButton
    lateinit var btn_right: AppCompatImageButton
    private var button_clicked: Boolean = true

    //Camera Variables

    private var url: String = "rtsp://admin:12344@192.168.176.210:8554/Streaming/Channels/101"
    private lateinit var libVlc: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoLayout: VLCVideoLayout


    //Chronometer Buttons and variables
    lateinit var start_stop_button: Button
    lateinit var reset_button: Button
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    lateinit var chronometer_text: TextView
    //var username_check: String = LoginViewModel::getUsername.toString()



    //Transfer data from fragment to Activity
    private val loginViewModel: LoginViewModel by viewModels()







    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS).toString()


        ConnectToDevice(this).execute()

        //Connect to camera
        libVlc = LibVLC(this)
        mediaPlayer = MediaPlayer(libVlc)
        videoLayout = findViewById(R.id.videoLayout)

        //Declaring buttons for car control
        btn1 = findViewById(R.id.front_leds)
        btn_disconnect = findViewById(R.id.button_disconnect)
        btn_stop = findViewById(R.id.button_stop)
        btn2 = findViewById(R.id.back_leds)
        btn_up = findViewById(R.id.move_forward)
        btn_down = findViewById(R.id.move_backward)
        btn_left = findViewById(R.id.move_left)
        btn_right = findViewById(R.id.move_right)


        //Declaring buttons for chronometer
        start_stop_button = findViewById(R.id.button_chronometer) //start stop button
        reset_button = findViewById(R.id.chronometer_reset) // reset button
        chronometer_text = findViewById(R.id.chronometer_car) //chronometer field





        serviceIntent = Intent(applicationContext,TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        btn1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (button_clicked) {
                    btn1.text = "FRONT LEDS ON"
                    sendCommand("u")
                    button_clicked = false
                } else {
                    btn1.text = "FRONT LEDS OFF"
                    sendCommand("i")
                    button_clicked = true
                }
            }
        })

        btn2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (button_clicked) {
                    btn2.text = "BACK LEDS ON"
                    sendCommand("j")
                    button_clicked = false
                } else {
                    btn2.text = "BACK LEDS OFF"
                    sendCommand("k")
                    button_clicked = true

                }
            }
        })


        btn_disconnect.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                disconnect()

            }
        })

        btn_stop.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                sendCommand("s")
            }
        })


        btn_up.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_DOWN) {
                sendCommand("F")
                true
            } else false
        })

        btn_down.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_DOWN) {
                sendCommand("B")
                true
            } else false
        })

        btn_left.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_DOWN) {
                sendCommand("L")
                true
            } else false
        })

        btn_right.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_DOWN) {
                sendCommand("R")
                true
            } else false
        })


        start_stop_button.setOnClickListener{startStopTimer()}
        reset_button.setOnClickListener{resetTimer()}


    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val intent = Intent(this@ControlActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSucess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }
            } catch (e: IOException) {
                connectSucess = false
                e.printStackTrace()
            }
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSucess) {
                Log.i("data", "couldn't connect")
                Toast.makeText(context, "Couldn't connect to Bluetooth Device ", Toast.LENGTH_SHORT).show()
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }

    }

    // Overridden function to start camera when the app starts

    override fun onStart() {
        super.onStart()

        mediaPlayer.attachViews(videoLayout, null, false, false)

        val media = Media(libVlc, Uri.parse(url))
        media.setHWDecoderEnabled(true, false)
        media.addOption(":network-caching=100")
        media.addOption(":live-caching=0")
        media.addOption(":sout-mux-caching=0")

        try{
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        } catch (e: IOException)
        {
            e.printStackTrace()
            Toast.makeText(this@ControlActivity, "Couldn't connect to IP Camera ", Toast.LENGTH_SHORT).show()

        }





    }

    override fun onStop() {
        super.onStop()

        mediaPlayer.stop()
        mediaPlayer.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.release()
        libVlc.release()
    }


    //Functions necessary for chronometer

    private fun resetTimer()
    {
        stopTimer()
        time = 0.0
        chronometer_text.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        start_stop_button.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        start_stop_button.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
        //username_textview.text =
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            chronometer_text.text = getTimeStringFromDouble(time)

        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val minutes = resultInt/100 % 86400 % 3600 / 60
        val seconds = resultInt/100 % 86400 % 3600 % 60
        val milliseconds = resultInt % 100

        return makeTimeString(minutes, seconds, milliseconds)
    }

    private fun makeTimeString(minutes: Int, seconds: Int, milliseconds: Int): String = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
}