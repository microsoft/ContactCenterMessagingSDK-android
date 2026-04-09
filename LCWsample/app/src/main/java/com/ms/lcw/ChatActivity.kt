package com.ms.lcw

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.lcw.lsdk.builder.LCWOmniChannelConfigBuilder
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.data.api.ApiResult
import com.lcw.lsdk.data.api.ConversationDetail
import com.lcw.lsdk.data.requests.ChatSDKConfig
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.data.requests.TelemetrySDKConfig
import com.lcw.lsdk.enum.ConversationStateEnum
import com.lcw.lsdk.events.ChatEventDispatcher
import com.lcw.lsdk.events.LCWChatEvents
import com.lcw.lsdk.logger.OLog
import com.ms.lcw.Constants.authTkn
import com.ms.lcw.Constants.orgId
import com.ms.lcw.Constants.orgUrl
import com.ms.lcw.Constants.orgWidgetId
import com.ms.lcw.script.ScriptAttributeExtractor

class ChatActivity : AppCompatActivity() {

    private lateinit var etOrgId: EditText
    private lateinit var etWidgetId: EditText
    private lateinit var etUrl: EditText
    private lateinit var etAuth: EditText
    private lateinit var btnText: TextView
    private lateinit var etScript: EditText
    private lateinit var utility: Utility
    private lateinit var btnCopyFCM: Button
    private lateinit var btnReset: Button
    private var isMinimised = false

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Handle permission result if needed
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        setupToolbar()
        utility = Utility()
        setDefaultConfig()

        initFCMData()
        initSDK()
        observeChatEvents()
    }

    private fun initViews() {
        etOrgId = findViewById(R.id.et_orgId)
        etWidgetId = findViewById(R.id.et_orgWidgetId)
        etUrl = findViewById(R.id.et_widgetUrl)
        etAuth = findViewById(R.id.et_auth)
        btnText = findViewById(R.id.btnText)
        etScript = findViewById(R.id.etScript)
        btnCopyFCM = findViewById(R.id.btnFCM)
        btnReset = findViewById(R.id.btnReset)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.MessagingToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initFCMData() {
        btnCopyFCM.setOnClickListener {
            var copiedText = utility.getFCMToken(this, "fcmtoken").stripQuotes()

            if (copiedText.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", copiedText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
            }
        }

        btnReset.setOnClickListener {
            utility.clearOrgs(this, "OC")
            utility.clearOrgs(this, "OCAuth")
            etUrl.setText("")
            etWidgetId.setText("")
            etOrgId.setText("")
            etAuth.setText("")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun initSDK() {
        val omnichannelConfig = OmnichannelConfig(
            orgId = etOrgId.text.toString(),
            orgUrl = etUrl.text.toString(),
            widgetId = etWidgetId.text.toString()
        )

        val authToken = etAuth.text.toString().stripQuotes()

        val chatSdkConfig = ChatSDKConfig(
            telemetry = TelemetrySDKConfig(disable = false)
        )

        val lcwOmniChannelConfigBuilder = LCWOmniChannelConfigBuilder
            .EngagementBuilder(omnichannelConfig, chatSdkConfig)
            .build()

        with(LiveChatMessaging.getInstance()) {
            // Development phase
             initialize(this@ChatActivity, lcwOmniChannelConfigBuilder, authToken, "test")
            // Production Live
            // initialize(this@ChatActivity, lcwOmniChannelConfigBuilder, authToken, "prod")
            fcmToken = utility.getFCMToken(this@ChatActivity, "fcmtoken")
        }

        // Attach the global dispatcher — routes all SDK callbacks into LCWChatEvents LiveData.
        // Any Activity observing LCWChatEvents will receive events from this point forward.
        ChatEventDispatcher.attach()
    }

    private fun setDefaultConfig() {
        val omnichannelConfig = utility.retrieveItem(this, "OC")
        val auth = utility.getAuth(this, "OCAuth")

        if (omnichannelConfig != null) {
            etUrl.setText(omnichannelConfig.orgUrl)
            etWidgetId.setText(omnichannelConfig.widgetId)
            etOrgId.setText(omnichannelConfig.orgId)
            etAuth.setText(auth)
        } else {
            etUrl.setText(orgUrl)
            etWidgetId.setText(orgWidgetId)
            etOrgId.setText(orgId)
            etAuth.setText(authTkn)
        }
    }

    fun launchChat(view: View?) {
        val omnichannelConfig = extract(etScript.text.toString()) ?: run {
            etScript.setText("")
            OmnichannelConfig(
                orgId = etOrgId.text.toString(),
                orgUrl = etUrl.text.toString(),
                widgetId = etWidgetId.text.toString()
            )
        }

        val authToken = etAuth.text.toString()

        utility.storeItem(this, "OC", omnichannelConfig)
        utility.storeAuth(this, "OCAuth", authToken)

        LiveChatMessaging.getInstance().launchLcwBrandedMessaging(this@ChatActivity)
    }

    private fun observeChatEvents() {
        // New agent or bot message received
        LCWChatEvents.newMessage.observe(this) { message ->
            val text = (message?.getProperty("content") ?: message)?.toString() ?: "null"
            Log.d(TAG, "newMessage: $text")
        }

        // Minimize button tapped — update the chat launch button label
        LCWChatEvents.minimized.observe(this) {
            isMinimised = true
            btnText.text = if (LiveChatMessaging.getInstance()?.isChatInProgress == true) "Restore" else "Let's Chat"
        }

        // Close button tapped
        LCWChatEvents.closed.observe(this) {
            Log.d(TAG, "onChatCloseButtonClicked")
        }

        // Agent assigned to the conversation
        LCWChatEvents.agentAssigned.observe(this) { content ->
            Log.d(TAG, "onAgentAssigned: $content")
        }

        // Chat session started
        LCWChatEvents.chatInitiated.observe(this) {
            Log.d(TAG, "onChatInitiated")
        }

        // Chat ended — true = agent ended, false = customer ended
        LCWChatEvents.chatEnded.observe(this) { byAgent ->
            Log.d(TAG, if (byAgent) "onAgentChatEnded" else "onCustomerChatEnded")
        }

        // Chat restored from a previous session
        LCWChatEvents.chatRestored.observe(this) {
            Log.d(TAG, "onChatRestored")
        }

        // Bot sign-in auth required
        LCWChatEvents.botSignIn.observe(this) { content ->
            Log.d(TAG, "onBotSignInAuth: $content")
        }

        // Link tapped inside a chat message
        LCWChatEvents.linkClicked.observe(this) { link ->
            Log.d(TAG, "onLinkClicked: $link")
        }

        // Transcript download completed
        LCWChatEvents.transcriptReceived.observe(this) { transcript ->
            Log.d(TAG, "onTranscriptReceived: $transcript")
        }

        // SDK or network error
        LCWChatEvents.error.observe(this) { error ->
            Log.d(TAG, "onError: ${error?.errorMessage}")
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isMinimised) {
            LiveChatMessaging.getInstance().getConversationDetails { response ->
                runOnUiThread {
                    when (response) {
                        is ApiResult.Success -> {
                            val detail = response.response as? ConversationDetail
                            val state = detail?.state
                            val textRes = when (state) {
                                ConversationStateEnum.Closed.key,
                                ConversationStateEnum.WrapUp.key -> R.color.lcw_colorProgressBackgroundNormal

                                ConversationStateEnum.Active.key -> R.color.lcw_colorPreChatActionButtonDefault

                                else -> R.color.lcw_colorPreChatActionButtonDestructive
                            }

                            btnText.text = if (state == ConversationStateEnum.Active.key) "Restore Chat" else "Let's Chat"
                            btnText.setTextColor(ContextCompat.getColor(this, textRes))
                        }

                        is ApiResult.Error -> {
                            btnText.text = "Let's Chat"
                            btnText.setTextColor(
                                ContextCompat.getColor(this, R.color.lcw_colorPreChatActionButtonDestructive)
                            )
                        }
                    }
                }
            }
        }
    }

    fun extract(scriptTag: String?): OmnichannelConfig? {
        if (scriptTag.isNullOrEmpty()) return null
        return ScriptAttributeExtractor.extractAttributes(scriptTag)?.let {
            OmnichannelConfig(
                widgetId = it.dataAppId,
                orgId = it.dataOrgId,
                orgUrl = it.dataOrgUrl
            )
        }
    }

    private fun String.stripQuotes(): String {
        return if (startsWith("\"") && endsWith("\"") && length > 1) {
            substring(1, length - 1)
        } else this
    }

    companion object {
        private const val TAG = "###LCW_CHAT"
    }
}
