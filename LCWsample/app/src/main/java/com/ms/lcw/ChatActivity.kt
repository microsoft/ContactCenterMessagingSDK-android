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
import com.lcw.lsdk.chat.Responses.GetMessageResponse
import com.lcw.lsdk.data.api.ApiResult
import com.lcw.lsdk.data.api.ConversationDetail
import com.lcw.lsdk.data.model.ErrorResponse
import com.lcw.lsdk.data.requests.ChatSDKConfig
import com.lcw.lsdk.data.requests.ChatSDKMessage
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.data.requests.TelemetrySDKConfig
import com.lcw.lsdk.enum.ConversationStateEnum
import com.lcw.lsdk.listeners.LCWMessagingDelegate
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
        //setupToolbar()
        utility = Utility()
        setDefaultConfig()

        initFCMData()
        initSDK()
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
            initialize(this@ChatActivity, lcwOmniChannelConfigBuilder, authToken, "test")
            fcmToken = utility.getFCMToken(this@ChatActivity, "fcmtoken")
        }
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

        LiveChatMessaging.getInstance().apply {
            launchLcwBrandedMessaging(this@ChatActivity)
            setLCWMessagingDelegate(object : LCWMessagingDelegate {
                override fun onChatMinimizeButtonClick() {
                    isMinimised = true
                    btnText.text = if (isChatInProgress) "Restore" else "Let's Chat"
                }

                override fun onChatCloseButtonClicked() {
                    Log.d(TAG, "onChatCloseButtonClicked")
                }

                override fun onViewDisplayed() {
                    Log.d(TAG, "onViewDisplayed")
                }

                override fun onChatInitiated() {
                    Log.d(TAG, "onChatInitiated")
                }

                override fun onCustomerChatEnded() {
                    Log.d(TAG, "onCustomerChatEnded")
                }

                override fun onAgentChatEnded() {
                    Log.d(TAG, "onAgentChatEnded")
                }

                override fun onAgentAssigned(content: String) {
                    Log.d(TAG, "onAgentAssigned-$content")
                }

                override fun onLinkClicked(link: String) {
                    Log.d(TAG, "onLinkClicked-$link")
                }

                override fun onNewCustomerMessage(message: ChatSDKMessage) {
                    Log.d(TAG, "onNewMessageSent-${message.content}")
                }

                override fun onNewMessageReceived(message: GetMessageResponse?) {
                    Log.d(TAG, "onNewMessageReceived-$message")
                }

                override fun onError(error: ErrorResponse?) {
                    Log.d(TAG, "onError-${error?.errorMessage}")
                }

                override fun onPreChatSurveyDisplayed() {
                    Log.d(TAG, "onPreChatSurveyDisplayed")
                }

                override fun onPostChatSurveyDisplayed(isExternalLink: Boolean) {
                }

                override fun onChatRestored() {
                    Log.d(TAG, "onChatRestored")
                }

                override fun onHeaderUtilityClicked() {
                    TODO("Not yet implemented")
                }

                override fun onBotSignInAuth(content: String) {
                    TODO("Not yet implemented")
                }
            })
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
                                ConversationStateEnum.WrapUp.key -> R.color.txt_notification_color

                                ConversationStateEnum.Active.key -> R.color.colorAccent

                                else -> R.color.colorPrimary
                            }

                            btnText.text = if (state == ConversationStateEnum.Active.key) "Restore Chat" else "Let's Chat"
                            btnText.setTextColor(ContextCompat.getColor(this, textRes))
                        }

                        is ApiResult.Error -> {
                            btnText.text = "Let's Chat"
                            btnText.setTextColor(
                                ContextCompat.getColor(this, com.lcw.chat.R.color.lcw_reconnect_title_description_color)
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
        private const val TAG = "ChatWindowStateDelegate"
    }
}
