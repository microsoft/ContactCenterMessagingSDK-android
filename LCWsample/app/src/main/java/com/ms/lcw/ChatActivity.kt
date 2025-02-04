package com.ms.lcw

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.ms.lcw.Constants.authTkn
import com.ms.lcw.databinding.ActivityChatBinding


class ChatActivity : AppCompatActivity() {
    private lateinit var utility: Utility
    private var isMinimised = false
    private lateinit var binding: ActivityChatBinding
    private val newChatText by lazy { resources.getText(R.string.label_new_chat) }
    private val restoreChatText by lazy { resources.getText(R.string.label_restore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUtility()
    }

    private fun initUtility() {
        utility = Utility()
        isMinimised = false
        setDefaultConfig()
    }

    private fun initSDK() {
        val omnichannelConfig = OmnichannelConfig(
            orgId = binding.etOrgId.text.toString(),
            orgUrl = binding.etWidgetUrl.text.toString(),
            widgetId = binding.etOrgWidgetId.text.toString()
        )

        val authToken = binding.etAuth.text.toString().trim('"')
        val chatSdkConfig = ChatSDKConfig(
            telemetry = TelemetrySDKConfig(disable = false)
        )

        val lcwOmniChannelConfig = LCWOmniChannelConfigBuilder.EngagementBuilder(omnichannelConfig, chatSdkConfig).build()

        LiveChatMessaging.getInstance().initialize(this, lcwOmniChannelConfig, authToken, "test")
    }

    private fun setDefaultConfig() {
        val omnichannelConfig = utility.retrieveItem(this, "OC")
        val auth = utility.getAuth(this, "")

        with(binding) {
            if (omnichannelConfig != null) {
                etWidgetUrl.setText(omnichannelConfig.orgUrl)
                etOrgWidgetId.setText(omnichannelConfig.widgetId)
                etOrgId.setText(omnichannelConfig.orgId)
                etAuth.setText(auth)
            } else {
                etWidgetUrl.setText(Constants.Org.url)
                etOrgWidgetId.setText(Constants.Org.widgetId)
                etOrgId.setText(Constants.Org.id)
                etAuth.setText(authTkn)
            }
        }
        initSDK()
    }

    fun launchChat(view: View?) {
        val omnichannelConfig = utility.extract(binding.etScript.text.toString()) ?: OmnichannelConfig(
            orgId = binding.etOrgId.text.toString(),
            orgUrl = binding.etWidgetUrl.text.toString(),
            widgetId = binding.etOrgWidgetId.text.toString()
        )

        utility.storeItem(this, "OC", omnichannelConfig)
        utility.storeAuth(this, "OCAuth", binding.etAuth.text.toString())

        LiveChatMessaging.getInstance().launchLcwBrandedMessaging(this)
        setUpMessagingDelegate()
    }

    private fun setUpMessagingDelegate() {
        LiveChatMessaging.getInstance().setLCWMessagingDelegate(object : LCWMessagingDelegate {
            override fun onChatMinimizeButtonClick() {
                isMinimised = true
                binding.btnText.setText(
                    if (LiveChatMessaging.getInstance().isChatInProgress) restoreChatText else newChatText
                )
            }

            override fun onChatCloseButtonClicked() = logChatEvent("onChatCloseButtonClicked")
            override fun onViewDisplayed() = logChatEvent("onViewDisplayed")
            override fun onChatInitiated() = logChatEvent("onChatInitiated")
            override fun onCustomerChatEnded() = logChatEvent("onCustomerChatEnded")
            override fun onAgentChatEnded() = logChatEvent("onAgentChatEnded")
            override fun onAgentAssigned(content: String) = logChatEvent("onAgentAssigned: $content")
            override fun onLinkClicked(link: String) = logChatEvent("onLinkClicked: $link")
            override fun onNewCustomerMessage(message: ChatSDKMessage) = logChatEvent("onNewMessageSent: ${message.content}")
            override fun onNewMessageReceived(message: GetMessageResponse?) = logChatEvent("onNewMessageReceived: $message")
            override fun onError(error: ErrorResponse?) = logChatEvent("onError: ${error?.errorMessage}")
            override fun onPreChatSurveyDisplayed() = logChatEvent("onPreChatSurveyDisplayed")
            override fun onPostChatSurveyDisplayed() = logChatEvent("onPostChatSurveyDisplayed")
            override fun onChatRestored() = logChatEvent("onChatRestored")
        })
    }

    private fun logChatEvent(message: String) {
        Log.d("ChatWindowStateDelegate", message)
    }

    override fun onResume() {
        super.onResume()
        if (!isMinimised) {
            LiveChatMessaging.getInstance().getConversationDetails { response ->
                runOnUiThread {
                    handleConversationState(response)
                }
            }
        }
    }

    private fun handleConversationState(response: ApiResult) {
        when (response) {
            is ApiResult.Success -> {
                val conversationDetail = response.response as? ConversationDetail
                val state = conversationDetail?.state
                binding.btnText.setText(
                    when (state) {
                        ConversationStateEnum.Closed.key,
                        ConversationStateEnum.WrapUp.key -> newChatText
                        ConversationStateEnum.Active.key -> restoreChatText
                        else -> newChatText
                    }
                )
            }
            is ApiResult.Error -> binding.btnText.setText(newChatText)
        }
    }
}