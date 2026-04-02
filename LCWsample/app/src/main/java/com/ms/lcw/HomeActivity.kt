package com.ms.lcw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.data.api.ApiResult

class HomeActivity : AppCompatActivity() {

    private lateinit var tvMessageLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvMessageLog = findViewById(R.id.tvMessageLog)

        findViewById<Button>(R.id.btnOpenChat).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val sdk = LiveChatMessaging.getInstance() ?: return
        if (!sdk.isChatInProgress()) return

        sdk.onNewMessage { result ->
            if (result is ApiResult.Success) {
                val text = "New message received Home: ${result.response}"
                Log.d(TAG, text)
                runOnUiThread { tvMessageLog.text = text }
            }
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
