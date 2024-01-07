package com.asharilabs.iotfarmingmqtt

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var _streaming_humi: TextView
    private lateinit var _streaming_suhu: TextView

    private lateinit var _streaming_chart_suhu: AAChartView
    private lateinit var _streaming_chart_humi: AAChartView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //
        _streaming_humi = findViewById(R.id.streaming_humi)
        _streaming_suhu = findViewById(R.id.streaming_temp)
        // -------------------------------------------------------

        _streaming_chart_suhu = findViewById(R.id.streaming_chart_suhu)
        _streaming_chart_humi = findViewById(R.id.streaming_chart_humi)

        // -------------------------------------------------------
        val _btn = findViewById<Button>(R.id.streaming_connectBroker)
        _btn.setOnClickListener {
            connect(this)
        }

        val _btn2 = findViewById<Button>(R.id.streaming_publishtext)
        var _input = findViewById<EditText>(R.id.streaming_textinput)
        _btn2.setOnClickListener {
            publish("galihashari", _input.text.toString())
        }
    }

    var _dataSuhu : ArrayList<Double> = ArrayList<Double>()
    var _dataHumi : ArrayList<Double> = ArrayList<Double>()

    fun BikininGrafik(_nilai: Double, _charview: AAChartView, _data: ArrayList<Double>){
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title("Monitoring Suhu")
            .subtitle("mqtt data")
            .dataLabelsEnabled(false)

        // bikin datanya
        _data.add(_nilai)

        var _aaseries : Array<AASeriesElement> =
            arrayOf(AASeriesElement()
                .name("Suhu").data(_data.toArray()))

        aaChartModel.series(arrayOf(_aaseries))

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        _charview.post(Runnable {
            _charview.aa_drawChartWithChartModel(aaChartModel)
            _charview.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(_aaseries)
        })
    }

    fun connect(applicationContext : Context) {

        mqttAndroidClient = MqttAndroidClient ( applicationContext,
            "tcp://broker.hivemq.com",
            "45565" )

        mqttAndroidClient.setCallback(object : MqttCallback{
            override fun connectionLost(cause: Throwable?) {
                Log.d("galihasharir", "connection lost: " + cause?.message.toString())
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("galihasharir", "incoming: " + message.toString())

                // isikan data mqtt ke UI
                if( topic.equals("asharilabs/suhu")){
                    _streaming_suhu.setText(message.toString())
                    BikininGrafik(message.toString().toDouble(), _streaming_chart_suhu, _dataSuhu)
                }
                else if( topic.equals("asharilabs/humi")){
                    _streaming_humi.setText(message.toString())
                    BikininGrafik(message.toString().toDouble(), _streaming_chart_humi, _dataHumi)
                }
                else if( topic.equals("asharilabs/humi")){
                    _streaming_humi.setText(message.toString())
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("galihasharir", "complete: " + token.toString())
            }

        })

        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken){
                    Log.d("galihasharir", "Broker Connected")
                    subscribe("asharilabs/suhu")
                    subscribe("asharilabs/humi")
                    //connectionStatus = true
                    // Give your callback on connection established here
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.d("galihasharir", "Broker Failure")
                    // Give your callback on connection failure here
                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("galihasharir", "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("galihasharir", "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttAndroidClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("galihasharir", "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("galihasharir", "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttAndroidClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("galihasharir", "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("galihasharir", "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttAndroidClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("galihasharir", "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("galihasharir", "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}