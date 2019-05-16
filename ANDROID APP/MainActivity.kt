package com.example.ashwinikakde.homeautomation1

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        try {
        checkPermission()
        var app1: String = "https://test.ngrok.io/app1" // ""http://chandanhomeauto2.pagekite.me/app1"
        var app2: String = "https://test.ngrok.io/app2"//"http://chandanhomeauto2.pagekite.me/app2"
        var app3: String = "https://test.ngrok.io/app3"
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference1 = firebaseDatabase.getReference()
        val reference2 = firebaseDatabase.getReference()
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        reference1.child("light1").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                val status = p0?.getValue()
                Toast.makeText(applicationContext, "Light 1: " + status.toString(), Toast.LENGTH_SHORT).show()
                switch1.isChecked = status.toString() == "true"

            }
        })

        reference1.child("light2").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                val status = p0?.getValue()
                Toast.makeText(applicationContext, "Light 2: " + status.toString(), Toast.LENGTH_SHORT).show()
                switch2.isChecked = status.toString() == "true"

            }
        })

        switch1.setOnCheckedChangeListener { compoundButton, b ->

            if (switch1.isChecked) {

                val url = app1 + "/true"
                url.httpGet().responseString { request, response, result ->

                    when(result) {

                        is Result.Success -> { Toast.makeText(this, "Result: ${result.get()}", Toast.LENGTH_SHORT).show() }
                        is Result.Failure -> {Toast.makeText(this, "Bad Request or Connection Error", Toast.LENGTH_SHORT).show()}
                    }
                }
            }

            if (!switch1.isChecked) {

                val url = app1 + "/false"
                Toast.makeText(applicationContext, url, Toast.LENGTH_LONG).show()
                url.httpGet().responseString { request, response, result ->

                    when(result) {

                        is Result.Success -> { Toast.makeText(this, "Result: ${result.get()}", Toast.LENGTH_SHORT).show() }
                        is Result.Failure -> {Toast.makeText(this, "Bad Request or Connection Error", Toast.LENGTH_SHORT).show()}
                    }
                }
            }
        }

        switch2.setOnCheckedChangeListener { compoundButton, b ->

            if(switch2.isChecked) {
                val url = app2 + "/true"
                Toast.makeText(applicationContext, url, Toast.LENGTH_LONG).show()
                url.httpGet().responseString { request, response, result ->

                    when(result)  {

                        is Result.Success -> {Toast.makeText(this, "Result: ${result.get()}", Toast.LENGTH_SHORT).show()}
                        is Result.Failure -> {Toast.makeText(this, "Bad Request or Connection Error", Toast.LENGTH_SHORT).show()}
                    }
                }

            }

            if(!switch2.isChecked) {

                val url = app2 + "/false"
                url.httpGet().responseString { request, response, result ->

                    when(result)  {

                        is Result.Success -> {Toast.makeText(this, "Result: ${result.get()}", Toast.LENGTH_SHORT).show()}
                        is Result.Failure -> {Toast.makeText(this, "Bad Request or Connection Error", Toast.LENGTH_SHORT).show()}
                    }
                }

            }
        }
        speech.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                speechRecognizer.startListening(speechRecognizerIntent)
                speechRecognizer.setRecognitionListener(object: RecognitionListener{
                    override fun onReadyForSpeech(p0: Bundle?) {
                        print("Ready")
                    }

                    override fun onBeginningOfSpeech() {
                        print("Beginning")
                    }

                    override fun onRmsChanged(p0: Float) {
                        print("RMS")
                    }

                    override fun onBufferReceived(p0: ByteArray?) {
                        print("BUFFER")
                    }

                    override fun onEndOfSpeech() {
                        print("END")
                    }

                    override fun onError(p0: Int) {
                        print("ERROR: " + p0.toString())
                    }

                    override fun onResults(p0: Bundle) {
                        val matches = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null) {
                            field.setText(matches[0])
                        }
                        speechRecognizer.stopListening()
                        val text = field.text
                        val url = app3 + "/" + text
                        url.httpGet().responseString { request, response, result ->
                            when (result) {
                                is Result.Success -> {Toast.makeText(applicationContext, "Result: ${result.get()}", Toast.LENGTH_SHORT).show()}
                                is Result.Failure -> {Toast.makeText(applicationContext, "Bad Request or Connection Error", Toast.LENGTH_SHORT).show()}
                            }
                        }

                    }

                    override fun onPartialResults(p0: Bundle?) {
                        print("Partial")
                    }

                    override fun onEvent(p0: Int, p1: Bundle?) {
                        print("Event")
                    }
                })

            }

        })

//        }
//        catch(e: Exception) {
//            print("ERROR: " + e.message)
//        }

        ngrok.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                 val id = field.text
                 app1 = "https://$id.ngrok.io/app1" // ""http://chandanhomeauto2.pagekite.me/app1"
                 app2 = "https://$id.ngrok.io/app2"//"http://chandanhomeauto2.pagekite.me/app2"
                 app3 = "https://$id.ngrok.io/app3"
                 Toast.makeText(applicationContext,app1,Toast.LENGTH_LONG).show()
            }
        })

    }
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: " + packageName))
                startActivity(intent)
                finish()
            }
        }
    }


}







