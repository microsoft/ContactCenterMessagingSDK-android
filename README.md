# Dynamics 365 Contact Center - Messaging SDK - Android

> ⚠️ The software is available as a Public Preview with release support for select Microsoft
partners and customers. To receive support for your release, contact CC-Mobile-Preview@microsoft.com 
with your release date and plans. Customers must have an agreement with the Contact Center team
to guarantee timely support during the Preview period.

### Building the Sample App - LCWsample

1. Clone the repository git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Download the AAR files - "./gradlew downloadAarFiles" (optional)
4. Sync/configure the app.
5. Build the app based on your build tools.
6. Run the app

## Troubleshooting
If you face build issue related to namespace for randombytes package. Update namespace in its build.gradle.  

namespace 'com.bitgo.randombytes' (path: node_modules -> react-native-randombytes -> android -> build.gradle) 

## Table of Contents

  * [About](#about)
  * [Installation](#installation)
    + [Pre-Requisites](#pre-requisites)
    + [Integrating the SDK.](#Integrating-the-SDK.)
      - [Manual Integration](#manual-integration)
      - [Integration through Gradle](#integration-through-gradle)
    + [Android Studio (Recommended)](#android-studio-recommended)
    + [Instructions for Using the Chat Feature in the Sample Android App](#instructions-for-using-the-chat-feature-in-the-sample-android-app)
  * [Initialization](#initialization)
  * [Messaging Widget](#messaging-widget)
  * [Core Messaging Framework](#core-messaging-framework)
    + [Initialization](#initialization-1)
    + [Authentication](#authentication)
    + [Start Chat](#start-chat)
    + [Get PreChat Survey](#get-prechat-survey)
    + [Get Live Chat Config](#get-live-chat-config)
    + [Get Current Live Chat Context](#get-current-live-chat-context)
    + [Get Data Masking Rules](#get-data-masking-rules)
    + [Get Chat Reconnect Context](#get-chat-reconnect-context)
    + [Get Conversation Details](#get-conversation-details)
    + [Get Chat Token](#get-chat-token)
    + [Send Message](#send-message)
    + [On Typing Event](#on-typing-event)
    + [On New Message](#on-new-message)
    + [On Agent End Session](#on-agent-end-session)
    + [Send Customer Typing](#send-customer-typing)
    + [Email Live Chat Transcript](#email-live-chat-transcript)
    + [Get Live Chat Transcript](#get-live-chat-transcript)
    + [End Chat](#end-chat)
    + [Get Agent Availability](#get-agent-availability)
    + [Download File Attachment](#download-file-attachment)
    + [Upload File Attachment](#upload-file-attachment)
    + [Post Chat Survey Context](#post-chat-survey-context)
    + [Get Reconnect Context](#get-reconnect-context)
    + [LCWMessagingDelegate](#lcwmessagingdelegate)
    + [getConversationDetails Logic in ChatActivity](#getconversationdetails-logic-in-chatactivity)
  * [Troubleshooting](#troubleshooting)

## About

The Dynamics 365 Contact Center Messaging SDK enables developers to add 
[Dynamics 365 Contact Center](https://www.microsoft.com/en-us/dynamics-365/products/contact-center)'s
messaging to their branded mobile applications.

This SDK contains two components:

* **Messaging Widget**: A pre-built, fully featured native messaging interface that the 
customer can use wholesale and easily customize to match their branding. The 
widget is already integrated with Omnichannel and customers that opt to use it do 
not need to interact with the Core Messaging Functions for almost all of the 
messaging lifecycle. 
* **Core Messaging Framework**: Enables a developer to build their own messaging 
interface. This is a full set of functions that interact with the Omnichannel 
Messaging APIs which the customer can connect with an existing or new interface in 
their branded app.

For most messaging programs, **we recommend the use of our out of the box Messaging Widget**:

* Provides messaging with the least amount of development effort
* Configurable look, feel, and customization options so the widget can be branded easily
to match your app. The available options are based on what is available with our Web
Live Chat Widget (LCW), so developers that are familiar with our web application
can quickly match their existing program

If there are any customizations you need that aren't available in our product, please
don't hesitate to reach out to us at CC-Mobile-Preview@microsoft.com and we'll add them
to our roadmap.

## Installation
### Pre-Requisites

*	Dynamics 365 Contact Center License
    *	Provisioned test organization, retrieving these variables:
        *	orgID
        *	orgURL
    * Provisioned Chat Channel & Live Chat Widget configuration, retrieving these variables:
        *	widgetID/applicationID
        *	For reference, see:
            * [Configure a chat widget | Microsoft Learn](https://learn.microsoft.com/en-us/dynamics365/customer-service/administer/add-chat-widget)
            * [Embed chat widget in your website or portal | Microsoft Learn](https://learn.microsoft.com/en-us/dynamics365/customer-service/administer/embed-chat-widget-portal)
*	Android SDK/API target minimum of 26 or above 
*	package.json file (included) 

### Integrating the SDK.

#### Manual Integration:
1. Add package.json to the root folder.
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Manually download the AAR files from release for desired version - https://github.com/microsoft/ContactCenterMessagingSDK-android/releases
4. Place aar files in libs (app -> libs).
5. Build the app based on your build tools.
6. Run the app

#### Integration through Gradle:
1. Add package.json to the root folder.
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Update the desired 'sdkVersion' in the app's build.gradle file.
5. Sync/configure the app.
6. Run the app.

### Android Studio (Recommended)
(These instructions were tested with gradle 8.7, Android Studio Koala | 2024.1.1 Patch 2 , OpenJDK 19.0.2)

Open Android Studio and select File->Open... or from the Android Launcher select Import project (Eclipse ADT, Gradle, etc.) and navigate to the root directory of your project.
Select the directory or drill in and select the file build.gradle in the cloned repo.
Click 'OK' to open the the project in Android Studio.
A Gradle sync should start, but you can force a sync and build the 'app' module as needed.
Gradle (command line)
Build the APK: ./gradlew build

Android Studio
Select Run -> Run 'app' (or Debug 'app') from the menu bar
Select the device you wish to run the app on and click 'OK'

### Instructions for Using the Chat Feature in the Sample Android App:
1. Paste Your Script (taken from the Chat Workstream Page) or Add the Required Information:

In your app’s landing screen, you will find input fields where you need to enter:
orgId
orgUrl
widgetId

Alternatively, you can paste a script, which will automatically fill in these details for you.

2. Click on the "Let's Chat" Button:
Once you've entered the required information (or pasted the script), look for a button labeled "Let's Chat" on your screen.
Tap on this button to initiate the chat. The app will connect to the specified chat system and load the widget for you.

3.Start Interacting with the Chat:
After clicking the button, you will see the chat interface appear on the screen.
You can now type messages, send media, or interact with the chat in real-time. 
The app will allow you to communicate with customer support or any automated services available.

## Initialization

Initialise the SDK with valid OmnichannelConfig parameters.

> ❗ Note: You should not directly instantiate the LCWApiResponse class. Instead use the following Messaging builder class: _Completionhandler is optional interface to get callbacks of APi Response_

//Parameters
val omnichannelConfig = OmnichannelConfig(
    orgId = “YOUR_ORG_ID”,
    orgUrl = “ORG_URL”,
    widgetId = “WIDGET_ID”
)

//Request builder
val lcwOmniChannelConfigBuilder =
    LCWOmniChannelConfigBuilder.EngagementBuilder(omnichannelConfig, chatSdkConfig).build()

//Invoking
LiveChatMessaging.getInstance()
   . initialize(this, lcwOmniChannelConfigBuilder, "auth_token", "environment")
LiveChatMessaging.getInstance().launchLcwBrandedMessaging(this)
```
## Messaging Widget
Customizations available in the out of the box messaging widget are documented here:

[Android Widget Customizations](Android_Widget_Customizations.pdf)

## Core Messaging Framework
This section describes the messaging lifecycle functions in the SDK.
### Initialization
You need to initialize the Omnichannel SDK with your omnichannel credentials before doing any other operations. You can call this method either at startup, or at the desired point in your application flow.

This requires your org details and authentication details for the customer. See Authentication section for more information.

API
```kotlin
LiveChatMessaging.getInstance()
   . initialize(this, lcwOmniChannelConfigBuilder, "auth_token", "environment")
```

Builders
```kotlin
val omnichannelConfig = OmnichannelConfig(
    orgId = “YOUR_ORG_ID”,
    orgUrl = “ORG_URL”,
    widgetId = “WIDGET_ID”
)
```

### Authentication
Dynamics Contact Center always recommends using persistent chat for mobile, which requires customer authentication. You identify the customer during initialization by providing an auth token containing an identifier such as a Dynamics Record ID, with the token signed by your identity provider.

Details on setting up a token are available here: https://learn.microsoft.com/en-us/dynamics365/customer-service/administer/create-chat-auth-settings

### Start Chat
This starts a Contact Center session, and is used when the customer opens the messaging app or tries to start a new conversation.

#### Method
```kotlin
val request = StartChatRequestBuilder().buildStartChatRequestParams(
    StartChatOptionalParams(
        liveChatContext = // EXISTING chat context data
        preChatResponse = // PreChatSurvey response,
        customContext =//// EXISTING custom context data
        os = "Android",
        locale = null,
        device = Build.DEVICE,
        browser = null,
        initContext = null,
        reconnectId = reconnectId,
        sendDefaultInitContext = false,
        isProactiveChat = false,
        latitude = null,
        longitude = null,
        portalContactId = null
  )
public void startChat(requestParam, completionHandler) {}
```

### Get PreChat Survey
Retrieves the Pre-Chat Survey configured in Customer Service Admin Center for the selected Org and App ID.
#### Method
```kotlin
LiveChatMessaging.getInstance().getPreChatSurvey(request, completionhandler) {}
```

### Get Live Chat Config
Retrieves the customized behavior and style settings from Customer Service Admin Center for the selected Org and App ID.
#### Method
```kotlin
val optionalRequest =  GetLiveChatConfigOptionalParams(
    sendCacheHeaders = false, // Boolean
    useRuntimeCache = null // Boolean
)

LiveChatMessaging.getInstance(). getLiveChatConfig (optionalRequest, completionhandler) {}
```

### Get Current Live Chat Context
Gets the current live chat context information to be used to reconnect to the same conversation.
#### Method
```kotlin
LiveChatMessaging.getInstance().getLiveChatContextFromApi(CompletionHandler handler) {}
```


### Get Data Masking Rules
Retrieves the data masking regex patterns that are configured in the Customer Service Admin Center for the selected Org and App ID. See here: https://learn.microsoft.com/en-us/dynamics365/customer-service/administer/data-masking-settings
#### Method
```kotlin
LiveChatMessaging.getInstance().getDataMaskingRules(LCWRequest requestParam, CompletionHandler handler) {}
```

### Get Chat Reconnect Context
It gets the current reconnectable chat context information to connect to a previous existing chat session.
Setting reconnection options is required, see here: https://learn.microsoft.com/en-us/dynamics365/customer-service/administer/configure-reconnect-chat?tabs=customerserviceadmincenter#enable-reconnection-to-a-previous-chat-session

#### Method
```kotlin
val optionalRequest = LCWChatReconnectContextRequestBuilder().buildChatReconnectContextRequestParams(
    reconnectId = "" 
)

LiveChatMessaging.getInstance().getChatReconnectContext(optionalRequest)
```

### Get Conversation Details
Gets the details of the current conversation such as its state & when the agent joined the conversation.
#### Method
```kotlin
val optionalParams = GetConversationDetailsOptionalParams(
    liveChatContext = LiveChatContext(), // EXISTING chat context data
)

val optionalRequest =
 LCWGetConversationDetailsRequestBuilder().buildGetConversationDetailsRequestParams(params);
LiveChatMessaging.getInstance(). getConversationDetails (optionalRequest)
```

### Get Chat Token
Retrievs the token used to initiate a chat with Contact Center.
#### Method
```kotlin
LiveChatMessaging.getInstance().getChatToken (CompletionHandler handler) {}
```

### Send Message
Sends message to the Agent or Customer Service Rep from the current customer.
#### Method
```kotlin
val option = ChatSDKMessage(
    content = text,
    tags = tags, // list of strings (tags)
    timestamp = timestamp,
    metadata = metadata
)
val messageId = “” //unique message id, or random generated id

val request = LCWSendCustomerMessageRequestBuilder().buildSendCustomerMessageRequestParams(option, messageId)

LiveChatMessaging.getInstance().sendMessage(request, completionHandler) {}
```

### On Typing Event
Subscribes to an Agent typing event. These events are sent on start of agent typing but no end event is sent, so only render a typing animation for 3-5 seconds.
#### Method
```kotlin
LiveChatMessaging.getInstance().onAgentTyping(completionHandler) {}
```

### On New Message
Subscribes to new incoming messages of the current conversation such as system messages, client messages, agent messages, adaptive cards and attachments.
#### Method
```kotlin
val  optional =  OnNewMessageOptionalParams (
   rehydrate = true/false
) ;
val request= LCWOnNewMessageRequestBuilder().buildOnNewMessageRequestParams(optional);
LiveChatMessaging.getInstance().onNewMessage(request, copletionalhandler) {} 

// Completion handler is optional.
```

### On Agent End Session
Subscribes to an agent ending the session of the conversation.
#### Method
```kotlin
LiveChatMessaging.getInstance().onAgentEndSession (completionHandler) {}
```

### Send Customer Typing
Tells the agent the customer is typing. Only sent at start of typing activity, not end.
#### Method
```kotlin
LiveChatMessaging.getInstance().sendTyping () {} 

// OR

val request = LCWSentTypingRequestBuilder().buildSentTypingRequestParams();
LiveChatMessaging.getInstance().onNewMessage(request,  copletionalhandler) {}
```

### Email Live Chat Transcript
Sends customer an email with a transcript of the conversation. You must collect and supply the customer's email.
#### Method
```kotlin
val  param=  ChatTranscriptBody(
    emailAddress = “johnsmith@outlook.com”, // valid email
    attachmentMessage: String, // custom body
    locale = Locale.getDefault().toString() //optional
)

val  optional =  EmailLiveChatTranscriptOptionaParams(
   liveChatContext = LiveChatContext()
)

val request = LCWEmailLiveChatTranscriptRequestBuilder()
    .buildEmailLiveChatTranscriptRequestParams(param, optional)


LiveChatMessaging.getInstance().onAgentEndSession (request, completionHandler) {}
```

### Get Live Chat Transcript
Fetches current conversation transcript data in a JSON. Used for populating the transcript 
for customers returning to an ongoing conversation.
#### Method
```kotlin
LiveChatMessaging.getInstance().getLiveChatTranscript (completionHandler) {}
OR
val  optional =  GetLiveChatTranscriptOptionalParams (
   liveChatContext = LiveChatContext()
)
val request = LCWGetLiveChatTranscriptRequestBuilder().buildGetLiveChatTranscriptRequestParams(optional)

LiveChatMessaging.getInstance().getLiveChatTranscript (request, completionHandler) {}
```

### End Chat
Ends the current Contact Center conversation.
#### Method
```kotlin
```

### Get Agent Availability
Gets information on whether a queue is available, and whether there are agents available in that queue, as well as queue position and average wait time. 
This call is only supported in an authenticated chat.
#### Method
```kotlin
val  optional =  GetAgentAvailabilityOptionalParams()
val request = LCWGetAgentAvailabilityRequestBuilder().buildAgentAvailabilityRequestParams(param)

LiveChatMessaging.getInstance().getAgentAvailability (request, completionHandler) {}
OR
LiveChatMessaging.getInstance().getAgentAvailability (completionHandler) {}
```

### Download File Attachment
Downloads the file attachment of the incoming message as a Base64 string response.
#### Method
```kotlin
val  fileMataData= FileMetadata (id, name, size, type, url, fileSharingProtocolType)

val request = LCWDownloadAttachmentRequestBuilder().buildDownloadAttachmentRequestParams(fileMetadata)

LiveChatMessaging.getInstance().downloadFileAttachment(request, completionHandler) {}
```

### Upload File Attachment
Sends a file attachment to the current conversation.
#### Method
```kotlin
val  fileMataData=  IFileInfo (name, type, size, data)

val request = LCWUploadAttachmentRequestBuilder().buildUploadAttachmentRequestParams(fileMetadata, “base64String")

LiveChatMessaging.getInstance().uploadFileAttachment(request, completionHandler) {}
```

### Post Chat Survey Context
Gets the participant type that should be used for the survey and both the default and bot survey details.
#### Method
```kotlin
LiveChatMessaging.getInstance().getPostChatSurveyContext (request, completionHandler) {}
```

### Get Reconnect Context
Retrieves the chat context needed for a reconnect
#### Method
```kotlin
val request = LCWChatReconnectContextRequestBuilder().buildChatReconnectContextRequestParams(
    reconnectId = ""
) 
LiveChatMessaging.getInstance(). getChatReconnectContext (request, completionHandler) {}
```

### LCWMessagingDelegate
To enable your application to monitor various SDK events, provide an instance of the LCWMessagingDelegate interface and implement the delegate methods. The following delegate methods are available to capture these events.

Example: `LiveChatMessaging.getInstance().setLCWMessagingDelegate(object: LCWMessagingDelegate())`

#### Methods

* `override fun onChatMinimizeButtonClick()`
  * Called when the chat is minimize button is clicked.
* `override fun onViewDisplayed()`
  * Called when the chat screen starts to visualise.
* `override fun onChatInitiated()`
  *	Called when the chat is initiated.
* `override fun onCustomerChatEnded()`
  *	Called when the chat is ended by the customer.
* `override fun onAgentChatEnded()`
  *	Called when the agent ends the chat.
* `override fun onAgentAssigned(content: String)`
  *	Called when live agent is assigned also gives related system message.
* `override fun onLinkClicked()`
  *	Called when url link in message is clicked also gives url.
* `override fun onNewCustomerMessage (message: ChatSDKMessage)`
  *	Called when new message has arrived including system messages.
* `override fun onNewMessageReceived(message: GetMessageResponse?)`
  *	Called when new message has arrived including system messages.
* `override fun onError(error: ErrorResponse?)`
  *	Called when an error occurs with Network, api or generic.
* `override fun onPreChatSurveyDisplayed()`
  *	Called when a Pre-chat survey is displayed.
* `override fun OnPostChatSurveyDisplayed()`
  *	Called when a Post-chat survey is displayed.
* `override fun onChatRestored()`
  *	Called when chat is restored or transcript reloaded.

### getConversationDetails Logic in ChatActivity
This code snippet retrieves the details of the current live chat conversation and updates the sample app UI accordingly based on the conversation's state.

* **Fetching Conversation Details**: The LiveChatMessaging.getInstance().getConversationDetails method is called to fetch the current conversation details. This returns a response wrapped in an ApiResult object.
* **UI Updates**: Update the chat button state or text depending upon the conversation state, ex. If active-> “Restore Chat” else “New Chat”

#### Sample
```kotlin
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
                            btnText.setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.messagingThemeBackground))
                        }
                        ConversationStateEnum.Active.key -> {

                            btnText.setText("Restore Chat")
                            btnText.setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.infoColor))
                        }

                        else -> {
                            btnText.setText("Let's Chat")
                            btnText.setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.messagingThemeBackground))
                        }
                    }
                }

                OLog.d("ChatActivity@getConversationDetails: ${response.response}")
            }

            is ApiResult.Error -> {
                btnText.setText("Let's Chat")
                btnText.setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.messagingThemeBackground))
            }
        }
    }
}
```
