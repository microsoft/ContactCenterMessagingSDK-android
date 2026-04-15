package com.ms.lcw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lcw.lsdk.builder.LCWOmniChannelConfigBuilder
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.data.api.ApiResult
import com.lcw.lsdk.chat.Responses.GetMessageResponse
import com.lcw.lsdk.constants.MessageTypes
import com.lcw.lsdk.data.requests.ChatSDKConfig
import com.lcw.lsdk.data.requests.LCWStartChatRequest
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.data.requests.TelemetrySDKConfig
import com.lcw.lsdk.events.ChatEventDispatcher
import com.lcw.lsdk.events.LCWChatEvents

// Home screen that shows live SDK events. Chat config and launch are handled in ChatActivity.
class HomeActivity : AppCompatActivity() {

    // event TextViews
    private lateinit var tvNewMessage: TextView
    private lateinit var tvAgentAssigned: TextView
    private lateinit var tvChatInitiated: TextView
    private lateinit var tvChatEnded: TextView
    private lateinit var tvChatRestored: TextView
    private lateinit var tvMinimized: TextView
    private lateinit var tvClosed: TextView
    private lateinit var tvBotSignIn: TextView
    private lateinit var tvError: TextView

    // track last shown message to avoid re-displaying seen messages (LiveData is sticky)
    private var lastShownMessage: String? = null
    // suppress newMessage events after the session is closed
    private var chatSessionEnded = false

    // buttons
    private lateinit var btnOpenChat: Button
    private lateinit var btnClearLog: Button
    private lateinit var btnClearNewMessage: Button
    private lateinit var btnClearAgentAssigned: Button
    private lateinit var btnClearChatInitiated: Button
    private lateinit var btnClearChatEnded: Button
    private lateinit var btnClearChatRestored: Button
    private lateinit var btnClearMinimized: Button
    private lateinit var btnClearClosed: Button
    private lateinit var btnClearBotSignIn: Button
    private lateinit var btnClearError: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bindViews()
        setupButtons()
        observeChatEvents()
    }

    override fun onResume() {
        super.onResume()
        if (!sdkReady) {
            tryInitSdkFromSavedConfig()
        }
    }

    private fun bindViews() {
        tvNewMessage    = findViewById(R.id.tvNewMessage)
        tvAgentAssigned = findViewById(R.id.tvAgentAssigned)
        tvChatInitiated = findViewById(R.id.tvChatInitiated)
        tvChatEnded     = findViewById(R.id.tvChatEnded)
        tvChatRestored  = findViewById(R.id.tvChatRestored)
        tvMinimized     = findViewById(R.id.tvMinimized)
        tvClosed        = findViewById(R.id.tvClosed)
        tvBotSignIn     = findViewById(R.id.tvBotSignIn)
        tvError         = findViewById(R.id.tvError)

        btnOpenChat           = findViewById(R.id.btnOpenChat)
        btnClearLog           = findViewById(R.id.btnClearLog)
        btnClearNewMessage    = findViewById(R.id.btnClearNewMessage)
        btnClearAgentAssigned = findViewById(R.id.btnClearAgentAssigned)
        btnClearChatInitiated = findViewById(R.id.btnClearChatInitiated)
        btnClearChatEnded     = findViewById(R.id.btnClearChatEnded)
        btnClearChatRestored  = findViewById(R.id.btnClearChatRestored)
        btnClearMinimized     = findViewById(R.id.btnClearMinimized)
        btnClearClosed        = findViewById(R.id.btnClearClosed)
        btnClearBotSignIn     = findViewById(R.id.btnClearBotSignIn)
        btnClearError         = findViewById(R.id.btnClearError)
    }

    private fun setupButtons() {
        btnOpenChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        btnClearLog.setOnClickListener { clearAll() }

        btnClearNewMessage.setOnClickListener    { resetRow(tvNewMessage) }
        btnClearAgentAssigned.setOnClickListener { resetRow(tvAgentAssigned) }
        btnClearChatInitiated.setOnClickListener { resetRow(tvChatInitiated) }
        btnClearChatEnded.setOnClickListener     { resetRow(tvChatEnded) }
        btnClearChatRestored.setOnClickListener  { resetRow(tvChatRestored) }
        btnClearMinimized.setOnClickListener     { resetRow(tvMinimized) }
        btnClearClosed.setOnClickListener        { resetRow(tvClosed) }
        btnClearBotSignIn.setOnClickListener     { resetRow(tvBotSignIn) }
        btnClearError.setOnClickListener         { resetRow(tvError) }
    }

    private fun observeChatEvents() {
        LCWChatEvents.newMessage.observe(this) { message ->
            if (chatSessionEnded) return@observe
            if (ChatActivity.isActive) return@observe
            val text = (message?.getProperty("content") ?: message)?.toString() ?: "null"
            if (text == lastShownMessage) return@observe
            lastShownMessage = text
            Log.d(TAG, "newMessage: $text")
            updateEventView(tvNewMessage, text)
        }

        LCWChatEvents.agentAssigned.observe(this) { agentName ->
            Log.d(TAG, "agentAssigned: $agentName")
            updateEventView(tvAgentAssigned, agentName)
        }

        LCWChatEvents.chatInitiated.observe(this) {
            Log.d(TAG, "chatInitiated")
            updateEventView(tvChatInitiated, "true")
            chatSessionEnded = false
        }

        LCWChatEvents.chatEnded.observe(this) { byAgent ->
            val who = if (byAgent) "by agent" else "by customer"
            Log.d(TAG, "chatEnded: $who")
            updateEventView(tvChatEnded, who)
        }

        LCWChatEvents.chatRestored.observe(this) {
            Log.d(TAG, "chatRestored")
            updateEventView(tvChatRestored, "true")
            chatSessionEnded = false
            fetchLastMessage()
        }

        LCWChatEvents.minimized.observe(this) {
            Log.d(TAG, "minimized")
            updateEventView(tvMinimized, "true")
        }

        LCWChatEvents.closed.observe(this) {
            Log.d(TAG, "closed")
            updateEventView(tvClosed, "true")
            chatSessionEnded = true
            resetRow(tvNewMessage)
        }

        LCWChatEvents.botSignIn.observe(this) { content ->
            Log.d(TAG, "botSignIn: $content")
            updateEventView(tvBotSignIn, content)
        }

        LCWChatEvents.error.observe(this) { error ->
            val text = error?.errorMessage ?: "unknown error"
            Log.d(TAG, "error: $text")
            updateEventView(tvError, text, isError = true)
        }
    }

    // green for events, red for errors
    private fun updateEventView(tv: TextView, value: String, isError: Boolean = false) {
        tv.text = value
        tv.setTextColor(
            ContextCompat.getColor(
                this,
                if (isError) R.color.lcw_txt_color_error else R.color.lcw_txt_color_green
            )
        )
    }

    private fun resetRow(tv: TextView) {
        tv.text = "—"
        tv.setTextColor(ContextCompat.getColor(this, R.color.lcw_colorPreChatTextSubtitle))
    }

    private fun clearAll() {
        listOf(
            tvNewMessage, tvAgentAssigned, tvChatInitiated, tvChatEnded,
            tvChatRestored, tvMinimized, tvClosed, tvBotSignIn, tvError
        ).forEach { resetRow(it) }
        lastShownMessage = null
        chatSessionEnded = false
    }

    // Init SDK from saved config so we can detect new messages on the home screen
    // without the user needing to open ChatActivity first. Runs once per process.
    private fun tryInitSdkFromSavedConfig() {
        val utility = Utility()
        val savedConfig = utility.retrieveItem(this, "OC") ?: run {
            Log.d(TAG, "tryInitSdkFromSavedConfig: no saved config, skipping")
            return
        }

        val authToken = utility.getAuth(this, "OCAuth")

        val omnichannelConfig = OmnichannelConfig(
            orgId    = savedConfig.orgId,
            orgUrl   = savedConfig.orgUrl,
            widgetId = savedConfig.widgetId
        )
        val lcwConfig = LCWOmniChannelConfigBuilder
            .EngagementBuilder(omnichannelConfig, ChatSDKConfig(telemetry = TelemetrySDKConfig(disable = false)))
            .build()

        LiveChatMessaging.getInstance().initialize(this, lcwConfig, authToken, "test")
        ChatEventDispatcher.attach()
        sdkReady = true

        try {
            LiveChatMessaging.getInstance().initChat { initResult ->
                Log.d(TAG, "tryInitSdkFromSavedConfig: initChat result=${initResult.javaClass.simpleName}")
                if (initResult !is ApiResult.Success) {
                    Log.d(TAG, "tryInitSdkFromSavedConfig: initChat failed, aborting")
                    return@initChat
                }
                try {
                    LiveChatMessaging.getInstance().startChat(LCWStartChatRequest()) { startResult ->
                        val event = (startResult as? ApiResult.Success)?.event
                        Log.d(TAG, "tryInitSdkFromSavedConfig: startChat result=${startResult.javaClass.simpleName}, event=$event")
                        // fetch messages — empty for new sessions, history for restored ones
                        if (startResult is ApiResult.Success) {
                            fetchLastMessage()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "tryInitSdkFromSavedConfig: startChat threw — ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "tryInitSdkFromSavedConfig: initChat threw — ${e.message}", e)
        }
    }

    // Fetch messages and show the last one on the dashboard
    private fun fetchLastMessage() {
        Log.d(TAG, "fetchLastMessage: calling getMessages")
        try {
            LiveChatMessaging.getInstance().getMessages { msgResult ->
                Log.d(TAG, "fetchLastMessage: callback result=$msgResult")
                @Suppress("UNCHECKED_CAST")
                val messages = (msgResult as? ApiResult.Success)?.response as? List<*>
                Log.d(TAG, "fetchLastMessage: message count=${messages?.size}")
                val last = messages
                    ?.filterIsInstance<GetMessageResponse>()
                    ?.filter { it.messageType != MessageTypes.TYPE_SYSTEM }
                    ?.lastOrNull { !it.getProperty("messageText")?.toString().isNullOrBlank() }
                    ?: return@getMessages
                val content = last.getProperty("messageText")?.toString() ?: return@getMessages
                val senderName = last.getProperty("agent.alias")?.toString()
                val display = buildString {
                    senderName?.takeIf { it.isNotEmpty() }?.let { append("$it: ") }
                    append(content)
                }
                Log.d(TAG, "fetchLastMessage: showing '$display'")
                runOnUiThread {
                    lastShownMessage = display
                    updateEventView(tvNewMessage, display)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchLastMessage: getMessages threw — ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "###LCW_CHAT"
        private var sdkReady = false
    }
}
