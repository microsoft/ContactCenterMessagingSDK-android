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

    // When the user returns to HomeActivity (after navigating away from ChatActivity),
    // register a lightweight delegate so that any new incoming message is logged here.
    //
    // This works because LiveChatMessaging is a singleton — the SDK always delivers
    // callbacks to whichever activity last called setLCWMessagingDelegate().
    // ChatActivity clears the delegate in its onPause, so by the time onResume fires
    // here the slot is free.
    override fun onResume() {
        super.onResume()
        // The SDK is initialized inside ChatActivity. Guard with ?. so that on first launch
        // (before the user has ever opened ChatActivity) this is safely skipped.
        LiveChatMessaging.getInstance()?.setLCWMessagingDelegate(object : LCWMessagingDelegate {

            override fun onNewMessageReceived(message: GetMessageResponse?) {
                val text = "New message received: $message"
                Log.d(TAG, text)
                // Update UI on the main thread so a real app can show a badge/highlight.
                runOnUiThread { tvMessageLog.text = text }
            }

            // The remaining callbacks are not relevant while on the home screen.
            // A real app can leave these as no-ops or handle them globally.
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

    // Clear the delegate when leaving HomeActivity so callbacks are not delivered
    // to a paused activity. ChatActivity will re-register its own delegate in its onResume.
    override fun onPause() {
        super.onPause()
        LiveChatMessaging.getInstance()?.setLCWMessagingDelegate(null)
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
