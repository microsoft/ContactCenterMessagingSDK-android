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
import androidx.core.content.ContextCompat
import com.lcw.lsdk.builder.LCWOmniChannelConfigBuilder
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.chat.Responses.GetMessageResponse
import com.lcw.lsdk.data.api.ApiResult
import com.lcw.lsdk.data.api.ConversationDetail
import com.lcw.lsdk.data.model.ErrorResponse
import com.lcw.lsdk.data.requests.ChatSDKConfig
import com.lcw.lsdk.data.requests.ChatSDKMessage
import com.lcw.lsdk.data.requests.LCWSendCustomerMessageRequestBuilder
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.data.requests.TelemetrySDKConfig
import com.lcw.lsdk.enum.ConversationStateEnum
import com.lcw.lsdk.listeners.LCWMessagingDelegate
import com.ms.lcw.Constants.authTkn
import com.ms.lcw.Constants.orgId
import com.ms.lcw.Constants.orgUrl
import com.ms.lcw.Constants.orgWidgetId
import com.ms.lcw.script.ScriptAttributeExtractor
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var etOrgId: EditText
    private lateinit var etWidgetId: EditText
    private lateinit var etUrl: EditText
    private lateinit var etAuth: EditText
    private lateinit var btnText: TextView
    private lateinit var etScript: EditText
    private lateinit var utility: Utility
    private var isMinimised = false
    private lateinit var btnCopyFCM: Button
    private lateinit var btnReset: Button
    private var isNotifictionLanding = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setBinding()
        initParams()
        setDefaultConfig()
        checkNotificationPermission()
        initListener()
        initSDK()
    }

    private fun initListener() {
        btnCopyFCM.setOnClickListener {
            val copiedText = utility.getFCMToken(this, "fcmtoken").let {
                if (it.startsWith("\"") && it.endsWith("\"")) it.substring(1, it.length - 1) else it
            }
            if (copiedText.isNotEmpty()) {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", copiedText)
                clipboard.setPrimaryClip(clip)
                showToast("Copied to Clipboard!")
            } else {
                showToast("Please wait")
            }
        }

        btnReset.setOnClickListener {
            utility.clearOrgs(this, "OC")
            utility.clearOrgs(this, "OCAuth")
            resetFields()
        }
    }

    private fun initParams() {
        utility = Utility()
        isNotifictionLanding = intent.extras?.getBoolean("notificationFlow") ?: false
    }

    private fun setBinding() {
        btnCopyFCM = findViewById(R.id.btnFCM)
        btnReset = findViewById(R.id.btnReset)
        val toolbar = findViewById<TextView>(R.id.toolbar_title)
        toolbar.setText(resources.getString(R.string.app_name))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        etOrgId = findViewById(R.id.et_orgId)
        etWidgetId = findViewById(R.id.et_orgWidgetId)
        etUrl = findViewById(R.id.et_widgetUrl)
        etAuth = findViewById(R.id.et_auth)
        btnText = findViewById(R.id.btnText)
        etScript = findViewById(R.id.etScript)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
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
        val authToken = etAuth.text.toString().trimQuotes()
        val chatSdkConfig = ChatSDKConfig(telemetry = TelemetrySDKConfig(disable = false))

        val lcwOmniChannelConfigBuilder =
            LCWOmniChannelConfigBuilder.EngagementBuilder(omnichannelConfig, chatSdkConfig).build()
        LiveChatMessaging.getInstance().apply {
            initialize(this@ChatActivity, lcwOmniChannelConfigBuilder, authToken, "test")
            fcmToken = utility.getFCMToken(this@ChatActivity, "fcmtoken")
            setLCWMessagingDelegate(createLCWMessagingDelegate())
        }
    }

    private fun createLCWMessagingDelegate() = object : LCWMessagingDelegate {
        override fun onChatMinimizeButtonClick() {
            isMinimised = true
            btnText.text =
                if (LiveChatMessaging.getInstance().isChatInProgress) "Restore" else "Let's Chat"
        }

        override fun onChatCloseButtonClicked() {
            Log.d("ChatWindowStateDelegate:", "onChatCloseButtonClicked")
        }

        override fun onViewDisplayed() {
            Log.d("ChatWindowStateDelegate:", "onViewDisplayed")
        }

        override fun onChatInitiated() {
            Log.d("ChatWindowStateDelegate:", "onChatInitiated")
        }

        override fun onCustomerChatEnded() {
            Log.d("ChatWindowStateDelegate:", "onCustomerChatEnded")
        }

        override fun onAgentChatEnded() {
            Log.d("ChatWindowStateDelegate:", "onAgentChatEnded")
        }

        override fun onAgentAssigned(content: String) {
            Log.d("ChatWindowStateDelegate:", "onAgentAssigned-" + content)
        }

        override fun onLinkClicked(link: String) {
            Log.d("ChatWindowStateDelegate:", "onLinkClicked-" + link)
        }

        override fun onNewCustomerMessage(message: ChatSDKMessage) {
            Log.d("ChatWindowStateDelegate:", "onNewMessageSent-" + message.content)
            if (message.content == "event to C1 from android") {
                sendCustomEventViaOutboundMessage()
            }
        }

        override fun onNewMessageReceived(message: GetMessageResponse?) {
            Log.d("ChatWindowStateDelegate:", "onNewMessageReceived-" + message.toString())
        }

        override fun onError(error: ErrorResponse?) {
            Log.d("ChatWindowStateDelegate:", "ChatWindowStateDelegate-" + error?.errorMessage)
        }

        override fun onPreChatSurveyDisplayed() {
            Log.d("ChatWindowStateDelegate:", "onPreChatSurveyDisplayed")
        }

        override fun onPostChatSurveyDisplayed() {
            Log.d("ChatWindowStateDelegate:", "onPostChatSurveyDisplayed")
        }

        override fun onChatRestored() {
            Log.d("ChatWindowStateDelegate:", "onChatRestored")
        }
    }

    private fun setDefaultConfig() {
        utility.retrieveItem(this, "OC")?.let {
            etUrl.setText(it.orgUrl)
            etWidgetId.setText(it.widgetId)
            etOrgId.setText(it.orgId)
            etAuth.setText(utility.getAuth(this, ""))
        } ?: run {
            etUrl.setText(orgUrl)
            etWidgetId.setText(orgWidgetId)
            etOrgId.setText(orgId)
            etAuth.setText(authTkn)
        }
    }

    private fun resetFields() {
        etUrl.setText("")
        etWidgetId.setText("")
        etOrgId.setText("")
        etAuth.setText("")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun String.trimQuotes(): String = if (this.startsWith("\"") && this.endsWith("\"")) {
        this.substring(1, this.length - 1)
    } else this

    fun launchChat(view: View?) {
        startChatScreen()
    }

    private fun startChatScreen() {
        val extractor = extract(etScript.text.toString())
        val omnichannelConfig = extractor ?: OmnichannelConfig(
            orgId = etOrgId.text.toString(),
            orgUrl = etUrl.text.toString(),
            widgetId = etWidgetId.text.toString()
        )

        utility.storeItem(this, "OC", omnichannelConfig)
        utility.storeAuth(this, "OCAuth", etAuth.text.toString())
        initSDK()
        LiveChatMessaging.getInstance().launchLcwBrandedMessaging(this@ChatActivity)
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) showToast("Notification permission denied.")
        }

    override fun onResume() {
        super.onResume()
        if (isNotifictionLanding) {
            isNotifictionLanding = false
            startChatScreen()
            return
        }

        if (!isMinimised) {
            LiveChatMessaging.getInstance().getConversationDetails { response ->
                runOnUiThread {
                    handleConversationDetailsResponse(response)
                }
            }
        }
    }

    private fun handleConversationDetailsResponse(response: ApiResult) {
        when (response) {
            is ApiResult.Success -> {
                val conversationDetail = response.response as? ConversationDetail
                conversationDetail?.state?.let { state ->
                    updateChatButtonState(state)
                }
            }

            is ApiResult.Error -> updateChatButtonState(null)
        }
    }

    private fun updateChatButtonState(state: String?) {
        val (text, color) = when (state) {
            ConversationStateEnum.Active.key -> "Restore Chat" to com.lcw.chat.R.color.infoColor
            ConversationStateEnum.Closed.key, ConversationStateEnum.WrapUp.key -> "Let's Chat" to R.color.messagingThemeBackground
            else -> "Let's Chat" to R.color.messagingThemeBackground
        }
        btnText.apply {
            setText(text)
            setTextColor(ContextCompat.getColor(this@ChatActivity, color))
        }
    }

    fun extract(scriptTag: String?): OmnichannelConfig? {
        return scriptTag?.let {
            ScriptAttributeExtractor.extractAttributes(it)?.run {
                OmnichannelConfig(
                    widgetId = dataAppId,
                    orgId = dataOrgId,
                    orgUrl = dataOrgUrl
                )
            }
        }
    }

    private fun sendCustomEventViaOutboundMessage() {
        try {
             val metadata = mapOf(
                 "customEvent" to "true",
                 "customEventName" to "TestEvent",
                 "customEventValue" to """{ "stringVar": "Hello world! 123456", "numberVar": -10.5, "boolVar": true, "displayableVar": { "isDisplayable": true, "value": "Hello again!" } }"""
             )

            val customEventData = ChatSDKMessage(
                content = "",
                tags = listOf("ChannelId-lcw", "Hidden"),
                timestamp = Date(System.currentTimeMillis()),
                metadata = metadata,
            )

            // Build the custom event request with the event data
            val customEventRequest = LCWSendCustomerMessageRequestBuilder().buildSendCustomerMessageRequestParams(
                message = customEventData,
                id = "f7a2b8c4-9d6e-4f3a-8b7c-2e1f9a5d6c8b"
            )

            // Create a completion handler that logs the result
            LiveChatMessaging.getInstance().sendMessage(customEventRequest) { result ->
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("CustomEvent", "Message sent successfully: ${result.response}")
                    }
                    is ApiResult.Error -> {
                        Log.d("CustomEvent", "Failed to send message: ${result.errorMessage}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CustomEvent", "Failed to send custom event: ${e.message}")
            showToast("Error: ${e.message}")
        }
    }
}
