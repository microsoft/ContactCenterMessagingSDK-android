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
    private var isMinimised = false
    private lateinit var btnCopyFCM: Button
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        etOrgId = findViewById(R.id.et_orgId)
        etWidgetId = findViewById(R.id.et_orgWidgetId)
        etUrl = findViewById(R.id.et_widgetUrl)
        etAuth = findViewById(R.id.et_auth)
        btnText = findViewById(R.id.btnText)
        etScript = findViewById(R.id.etScript)
        isMinimised = false
        btnCopyFCM = findViewById(R.id.btnFCM)
        btnReset = findViewById(R.id.btnReset)
        utility = Utility()
        setDefaultConfig()

        val toolbar = findViewById<View>(R.id.MessagingToolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setLogo(R.drawable.ic_launcher_background)
        initFCMData()
        initSDK()
    }

    private fun initFCMData() {
        btnCopyFCM.setOnClickListener {
            var copiedText = utility.getFCMToken(this@ChatActivity, "fcmtoken")

            if (!copiedText.isEmpty()) {
                if (copiedText.startsWith("\"") && copiedText.endsWith("\"")) {
                    copiedText = copiedText.substring(
                        1,
                        copiedText.length - 1
                    );  // Remove first and last character
                }
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", copiedText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@ChatActivity, "Copied to Clipboard!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@ChatActivity, "Please wait", Toast.LENGTH_SHORT)
                    .show()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            }
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
        /*<script v2 id="Microsoft_Omnichannel_LCWidget" src="https://oc-cdn-public.azureedge.net/livechatwidget/scripts/LiveChatBootstrapper.js" data-app-id="4d3cddac-5432-453b-97ef-3b3d60f99252" data-lcw-version="prod" data-org-id="ce4db5f6-1c20-ee11-a66d-000d3a0a02f3" data-org-url="https://m-ce4db5f6-1c20-ee11-a66d-000d3a0a02f3.ca.omnichannelengagementhub.com"></script></body></html>*/
        var authToken = etAuth.text.toString()
        if (authToken.startsWith("\"") && authToken.endsWith("\"")) {
            authToken =
                authToken.substring(1, authToken.length - 1);  // Remove first and last character
        }
        val chatSdkConfig = ChatSDKConfig(
            telemetry = TelemetrySDKConfig(
                disable = false,
            ),
        )
        val lcwOmniChannelConfigBuilder =
            LCWOmniChannelConfigBuilder.EngagementBuilder(omnichannelConfig, chatSdkConfig).build()
        LiveChatMessaging.getInstance()
            .initialize(this@ChatActivity, lcwOmniChannelConfigBuilder, authToken, "test")
        LiveChatMessaging.getInstance().fcmToken =
            utility.getFCMToken(this@ChatActivity, "fcmtoken")
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
        val extractor = extract(etScript.text.toString())
        val omnichannelConfig = if (extractor == null) {
            etScript.setText("")
            OmnichannelConfig(
                orgId = etOrgId.text.toString(),
                orgUrl = etUrl.text.toString(),
                widgetId = etWidgetId.text.toString()
            )
        } else {
            extractor
        }
        var authToken = etAuth.text.toString()
        utility.storeItem(this, "OC", omnichannelConfig)
        utility.storeAuth(this, "OCAuth", authToken)
        LiveChatMessaging.getInstance().launchLcwBrandedMessaging(this@ChatActivity)
        LiveChatMessaging.getInstance().setLCWMessagingDelegate(object : LCWMessagingDelegate {
            override fun onChatMinimizeButtonClick() {
                isMinimised = true
                btnText.setText(if (LiveChatMessaging.getInstance().isChatInProgress) "Restore" else "Lets's Chat")
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

        })
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, you can now send notifications
            } else {
                // Permission denied, handle accordingly
            }
        }

    override fun onResume() {
        super.onResume()
        val isChatInProgress = LiveChatMessaging.getInstance().isChatInProgress
        OLog.d("ChatActivity onResume: $isChatInProgress")
        if (!isMinimised) {
            LiveChatMessaging.getInstance().getConversationDetails { response ->
                runOnUiThread {
                    OLog.d("ChatActivity getConversationDetails: $response")

                    when (response) {
                        is ApiResult.Success -> {

                            val conversationDetail = response.response as? ConversationDetail
                            conversationDetail?.state?.let {
                                when (it) {
                                    ConversationStateEnum.Closed.key,
                                    ConversationStateEnum.WrapUp.key -> {
                                        btnText.setText("Let's Chat")
                                        btnText.setTextColor(
                                            ContextCompat.getColor(
                                                this@ChatActivity,
                                                R.color.colorProgressBackgroundNormal
                                            )
                                        )
                                    }

                                    ConversationStateEnum.Active.key -> {

                                        btnText.setText("Restore Chat")
                                        btnText.setTextColor(
                                            ContextCompat.getColor(
                                                this@ChatActivity,
                                                R.color.colorPreChatActionButtonDefault
                                            )
                                        )
                                    }

                                    else -> {
                                        btnText.setText("Let's Chat")
                                        btnText.setTextColor(
                                            ContextCompat.getColor(
                                                this@ChatActivity,
                                                R.color.colorPreChatActionButtonDestructive
                                            )
                                        )
                                    }
                                }
                            }

                            OLog.d("ChatActivity@getConversationDetails: ${response.response}")
                        }

                        is ApiResult.Error -> {
                            btnText.setText("Let's Chat")
                            btnText.setTextColor(
                                ContextCompat.getColor(
                                    this@ChatActivity,
                                    R.color.colorPreChatActionButtonDestructive
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun extract(scriptTag: String?): OmnichannelConfig? {
        if (scriptTag.isNullOrEmpty()) {
            return null
        }
        val attributes = ScriptAttributeExtractor.extractAttributes(scriptTag)

        attributes?.let {
            println("ID: ${it.id}")
            println("SRC: ${it.src}")
            println("Data App ID: ${it.dataAppId}")
            println("Data LCW Version: ${it.dataLcwVersion}")
            println("Data Org ID: ${it.dataOrgId}")
            println("Data Org URL: ${it.dataOrgUrl}")
            return OmnichannelConfig(
                widgetId = it.dataAppId,
                orgId = it.dataOrgId,
                orgUrl = it.dataOrgUrl
            )
        } ?: run {
            println("No valid script attributes found.")
            return null
        }
    }

//=====================Sample request===========================
    /*
// On agent typing listener
LiveChatMessaging.getInstance().dispatchEvent(
    OnAgentTypingRequestBuilder().buildOnAgentTypingRequestParams()
)
// On agent end session listener
LiveChatMessaging.getInstance().dispatchEvent(
    OnAgentEndSessionRequestBuilder().buildOnAgentTypingRequestParams()
)
// get live chat context
LiveChatMessaging.getInstance().dispatchEvent(
    GetCurrentLiveChatContextRequestBuilder().buildCurrentLiveChatContextRequestParams()
)
// Get messages
LiveChatMessaging.getInstance().dispatchEvent(
    GetMessagesRequestBuilder().buildGetMessagesRequestRequestParams()
)

// Get get data-masking rules
LiveChatMessaging.getInstance().dispatchEvent(
    GetDataMaskingRulesLcwRequestRequestBuilder().buildGetPreChatSurveyLcwRequestRequestParams()
)
  // Get pre-chat survey
  LiveChatMessaging.getInstance().dispatchEvent(
      GetPreChatSurveyLcwRequestRequestBuilder().buildGetPreChatSurveyLcwRequestRequestParams()
  )
  // Get post-chat survey
  LiveChatMessaging.getInstance().dispatchEvent(
      GetPostChatSurveyLcwRequestRequestBuilder().buildGetPreChatSurveyLcwRequestRequestParams()
  )

  // Get current live chat context
  LiveChatMessaging.getInstance().dispatchEvent(
      GetCurrentLiveChatContextRequestBuilder().buildCurrentLiveChatContextRequestParams()
  )
  // Get current live chat config
  LiveChatMessaging.getInstance().dispatchEvent(
      GetLiveChatConfigRequestBuilder().buildGetLiveChatConfigRequestParams()
  )
  // Email live chat transcript, use this on popup input email
  LiveChatMessaging.getInstance().dispatchEvent(
      EmailLiveChatTranscriptRequestBuilder().buildEmailLiveChatTranscriptRequestParams(
          ChatTranscriptBody(
              emailAddress = "takeuser@email.com",
              attachmentMessage = "email transcript")))
// Get conversation details
LiveChatMessaging.getInstance().dispatchEvent(
    GetConversationDetailsRequestBuilder().buildGetConversationDetailsRequestParams()
)

// Download file attachment
LiveChatMessaging.getInstance().dispatchEvent(
    DownloadAttachmentRequestBuilder().buildGetConversationDetailsRequestParams()
)
// Get agent availability
LiveChatMessaging.getInstance().dispatchEvent(
    GetLiveChatConfigRequestBuilder().buildGetLiveChatConfigRequestParams()
)

// Get live chat transcript
LiveChatMessaging.getInstance().dispatchEvent(
    GetLiveChatConfigRequestBuilder().buildGetLiveChatConfigRequestParams()
)
// Upload file attachment
LiveChatMessaging.getInstance().dispatchEvent(
    UploadAttachmentRequestBuilder().buildUploadAttachmentRequestParams(
    )
)
*/
//======================Comment END=================================================
}
