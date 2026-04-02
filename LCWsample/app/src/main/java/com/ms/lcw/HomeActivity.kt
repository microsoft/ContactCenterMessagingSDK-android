package com.ms.lcw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.chat.Responses.GetMessageResponse
import com.lcw.lsdk.data.model.ErrorResponse
import com.lcw.lsdk.data.requests.ChatSDKMessage
import com.lcw.lsdk.listeners.LCWMessagingDelegate

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
        // sdk is only init'd after ChatActivity opens, so null check here
        LiveChatMessaging.getInstance()?.setLCWMessagingDelegate(object : LCWMessagingDelegate {

            override fun onNewMessageReceived(message: GetMessageResponse?) {
                val text = "New message received: $message"
                Log.d(TAG, text)
                runOnUiThread { tvMessageLog.text = text }
            }

            override fun onChatMinimizeButtonClick() {}
            override fun onChatCloseButtonClicked() {}
            override fun onViewDisplayed() {}
            override fun onChatInitiated() {}
            override fun onCustomerChatEnded() {}
            override fun onAgentChatEnded() {}
            override fun onAgentAssigned(content: String) {}
            override fun onLinkClicked(link: String) {}
            override fun onNewCustomerMessage(message: ChatSDKMessage) {}
            override fun onError(error: ErrorResponse?) {}
            override fun onPreChatSurveyDisplayed() {}
            override fun onPostChatSurveyDisplayed(isExternalLink: Boolean) {}
            override fun onChatRestored() {}
            override fun onHeaderUtilityClicked() {}
            override fun onBotSignInAuth(content: String) {}
        })
    }

    override fun onPause() {
        super.onPause()
        // clear when leaving so callbacks don't fire on a paused activity
        LiveChatMessaging.getInstance()?.setLCWMessagingDelegate(null)
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
