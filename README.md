# Dynamics 365 Contact Center - Messaging SDK - Android

> ⚠️ The software is available as a Public Preview with release support for select Microsoft
partners and customers. To receive support for your release, contact CC-Mobile-Preview@microsoft.com 
with your release date and plans. Customers must have an agreement with the Contact Center team
to guarantee timely support during the Preview period.

### Building the Sample App - LCWsample(v1.1.0)

1. Clone the repository  
   `git clone --branch 1.1.0 https://github.com/microsoft/ContactCenterMessagingSDK-android.git`

2. Sync gradle, build and run the app.


### Building the Sample App - LCWsample(< v1.1.0)

1. Clone the repository  
   `git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git`

2. Open the terminal and run `npm install` in the root folder (the provided `package.json` is copied to the root folder).

3. Download the AAR files (optional)  
   `./gradlew downloadAarFiles`

4. Sync/configure the app.

5. Build the app based on your build tools.

6. Run the app.

### Android Studio (Recommended)
(These instructions were tested with Gradle 8.7, Android Studio Koala | 2024.1.1 Patch 2, OpenJDK 19.0.2, minSdkVersion 26, compileSdkVersion 32, targetSdkVersion 32, kotlinVersion 2.1.0)

1. Open Android Studio and select `File -> Open...`, or from the Android Launcher, select `Import project (Eclipse ADT, Gradle, etc.)` and navigate to the root directory of your project.

2. Select the directory or drill into the project and select the `build.gradle` file in the cloned repo.

3. Click 'OK' to open the project in Android Studio.

4. A Gradle sync should start. You can force a sync and build the 'app' module as needed.

### Gradle (command line)
Build the APK:  
`./gradlew build`

### Android Studio
1. Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar.

2. Select the device you wish to run the app on and click 'OK'.

### Instructions for Using the Chat Feature in the Sample Android App:

1. **Paste Your Script (taken from the Chat Workstream Page) or Add the Required Information:**

   In your app’s landing screen, you will find input fields where you need to enter:
    - `orgId`
    - `orgUrl`
    - `widgetId`

   Alternatively, you can paste a script, which will automatically fill in these details for you.

2. **Click on the "Let's Chat" Button:**

   Once you've entered the required information (or pasted the script), look for a button labeled "Let's Chat" on your screen.

   Tap on this button to initiate the chat. The app will connect to the specified chat system and load the widget for you.

3. **Start Interacting with the Chat:**

   After clicking the button, you will see the chat interface appear on the screen.  
   You can now type messages, send media, or interact with the chat in real-time.  
   The app will allow you to communicate with customer support or any automated services available.

## Troubleshooting

If you face a build issue related to the namespace for the `randombytes` package, update the namespace in its `build.gradle`:

```gradle
   namespace 'com.bitgo.randombytes'  // (path: node_modules -> react-native-randombytes -> android -> build.gradle)
```
## Table of Contents

  * [About](#about)
  * [Installation](#installation)
    + [Pre-Requisites](#pre-requisites)
    + [Integration](#integration)
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
  * [Troubleshooting](#troubleshooting-1)
  * [Push Notifications](#push-notifications)
  * [Custom font & font-family](#custom-font--font-family)
    + [Agent font & font-family](#agent-font--font-family)
    + [Customer font & font-family](#customer-font--font-family)
    + [Image adaptive card font & font-family](#image-adaptive-card-font--font-family)
    + [Video adaptive card font & font-family](#video-adaptive-card-font--font-family)
    + [Basic adaptive card font & font-family](#basic-adaptive-card-font--font-family)
  * [Markdown parsing limitations](#markdown-parsing-limitations)
   

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
*	package.json file (included in sample app) 

### Integration
**Important (from version 1.1.0 onward):**

- The `randombytes` and `randomvalues` dependency is no longer required.  
  Ensure any references to `randombytes` and `randomvalues` are removed from your `build.gradle` and/or `settings.gradle` files.

- You no longer need to run `npm install` as part of the setup.

- Add the following dependencies to your **app-level `build.gradle`** file and verify they are downloaded correctly:
```kotlin
    implementation(files("libs/ContactCenterMessagingWidget.aar"))
    implementation(files("libs/OmnichannelChatSDK.aar"))
    implementation("com.facebook.react:react-android:0.80.0") // As react native version upgraded to 0.80.0
    implementation("com.facebook.soloader:soloader:0.10.5")
    implementation("io.github.react-native-community:jsc-android:2026004.0.1") //As JSC would be the default engine and Hermes is disabled
    implementation ("io.adaptivecards:adaptivecards-android:3.7.1") //Optional
```

(For versions < 1.1.0 only - React Native version 0.70.6) :

**Adding/Configuring `package.json`**

1. **Open your project directory:** Navigate to the root folder of your Android project.
2. **Initialize npm:** Run the following command in the terminal inside your project directory. This will create a new `package.json` file:
    ```bash
    npm init
    ```
3. This will ask a series of questions to configure your `package.json` file, such as:
    - Project name
    - Version
    - Description
    - Entry point
    - Repository information (if any)
    - License type

    - You can just hit "Enter" for most prompts to accept the default values, or you can customize the values as you see fit.
    - Alternatively, you can skip all questions by using:
    ```bash
    npm init -y
    ```

4. **Install dependencies:**
    ```bash
    npm install react-native@0.70
    npm install react-native-randombytes@3.6.1
    npm install react-native-get-random-values@1.8.0
    ```

5. **Alternatively, you can simply refer/copy `package.json` from a sample app and run:**
    ```bash
    npm install
    ```


#### Adding/Configuring build.gradle depedencies

1. Open the build.gradle.kts (project's) or settings.gradle.kt file. Add below code for dependency resolution
    ```kotlin
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
        repositories {
            mavenCentral()
            maven(url = "$rootDir/node_modules")
            maven(url = "$rootDir/node_modules/react-native/android")
            maven(url = "$rootDir/node_modules/jsc-android/dist")
            maven(url = "https://maven.google.com")
            google()
        }
    }

    include(":randombytes")
    project(":randombytes").projectDir = file("./node_modules/react-native-randombytes/android")

    include(":randomvalues")
    project(":randomvalues").projectDir = file("./node_modules/react-native-get-random-values/android")
    ```
2. **Open the `build.gradle.kts` (app's) file of your app module.**

3. **Add the SDK dependencies under the dependencies section and other snippet:**

    ```kotlin
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
    dependencies {
        implementation(files("libs/ContactCenterMessagingWidget.aar"))
        implementation(files("libs/OmnichannelChatSDK.aar"))   
        implementation("com.google.code.gson:gson:2.10.1")  
        implementation("com.facebook.react:react-native:+")  
        implementation("org.webkit:android-jsc:+")  
        implementation(project(":randombytes"))  
        implementation(project(":randomvalues"))
        implementation("com.google.android.flexbox:flexbox:3.0.0")
        implementation("io.adaptivecards:adaptivecards-android:2.9.0")
    }
    ```

4. **You can automate the AAR download task by adding the following code in the app's `build.gradle.kts`:**

    ```kotlin
    // Download AAR files from GitHub 
    // (Run the command ./gradlew downloadAarFiles)
    val sdkVersion = "v1.0.1"
    task("downloadAarFiles") {
        doLast {
            println("Download AARs task started...")
            val aar1Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/ContactCenterMessagingWidget.aar"
            val aar2Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/OmnichannelChatSDK.aar"
            val aar1File = file("${project.rootDir}/app/libs/ContactCenterMessagingWidget.aar")
            val aar2File = file("${project.rootDir}/app/libs/OmnichannelChatSDK.aar")

            URL(aar1Url).openStream().use { input -> aar1File.outputStream().use { output -> input.copyTo(output) } }
            URL(aar2Url).openStream().use { input -> aar2File.outputStream().use { output -> input.copyTo(output) } }
        }
    }

    tasks.named("preBuild") {
        dependsOn("downloadAarFiles")
    }
    ```

5. **Build the app based on your build tools.**

6. **Run the app.**

> ❗ **You can also manually download the AAR files for the desired version**  
> [Download AAR files here](https://github.com/microsoft/ContactCenterMessagingSDK-android/releases)

> ❗ **Place the AAR files in the `libs` folder**  
> Navigate to `app -> libs` and place the downloaded AAR files there.

### Troubleshooting
If you face build issue related to flexbox dependency, add below code to project level buld.gradle (inside allprojects block)
```kotlin
    dependencies{
        modules {
            module("com.google.android:flexbox") { 
                replacedBy("com.google.android.flexbox:flexbox")
            }
        } 
    }
```
## Initialization

Initialise the SDK with valid OmnichannelConfig parameters.

```kotlin
    val omnichannelConfig = OmnichannelConfig(
        orgId = “YOUR_ORG_ID”,
        orgUrl = “YOUR_ORG_URL”,
        widgetId = “YOUR_APP_ID”
    )
```

```kotlin
    //Request builder
    val lcwOmniChannelConfigBuilder =
    LCWOmniChannelConfigBuilder.EngagementBuilder(omnichannelConfig, chatSdkConfig).build()

    //Invoking
     LiveChatMessaging.getInstance().initialize(this, lcwOmniChannelConfigBuilder, "auth_token", "environment")
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
    LiveChatMessaging.getInstance().initialize(this, lcwOmniChannelConfigBuilder, "auth_token", "environment")
```

Builders
```kotlin
    val omnichannelConfig = OmnichannelConfig(
        orgId = “YOUR_ORG_ID”,
        orgUrl = “YOUR_ORG_URL”,
        widgetId = “YOUR_WIDGET_ID”
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
LiveChatMessaging.getInstance().endChat(CompletionHandler handler) {}
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
    - Called when the chat minimize button is clicked.
* `override fun onViewDisplayed()`
    - Called when the chat screen starts to visualize.
* `override fun onChatInitiated()`
    - Called when the chat is initiated.
* `override fun onCustomerChatEnded()`
    - Called when the chat is ended by the customer.
* `override fun onAgentChatEnded()`
    - Called when the agent ends the chat.
* `override fun onAgentAssigned(content: String)`
    - Called when a live agent is assigned, also gives related system message.
* `override fun onLinkClicked()`
    - Called when a URL link in the message is clicked, also gives the URL.
* `override fun onNewCustomerMessage(message: ChatSDKMessage)`
    - Called when a new message has arrived, including system messages.
* `override fun onNewMessageReceived(message: GetMessageResponse?)`
    - Called when a new message has arrived, including system messages.
* `override fun onError(error: ErrorResponse?)`
    - Called when an error occurs with network, API, or generic issues.
* `override fun onPreChatSurveyDisplayed()`
    - Called when a pre-chat survey is displayed.
* `override fun onPostChatSurveyDisplayed()`
    - Called when a post-chat survey is displayed.
* `override fun onChatRestored()`
    - Called when chat is restored or transcript reloaded.


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

## Push Notifications
### Requirements
Our Android push notifications require accounts with [Azure Notification Hub](https://azure.microsoft.com/en-us/products/notification-hubs) and [Google Firebase](https://firebase.google.com/).

Instructions:
* [Setting up Azure Notification Hub](https://learn.microsoft.com/en-us/azure/notification-hubs/create-notification-hub-portal) 
* [Configuring Google Firebase](https://learn.microsoft.com/en-us/azure/notification-hubs/configure-google-firebase-cloud-messaging)

You will also need:
* The **App Id** for the workstream used by your application.
* The **org_url** for your Contact Center dataverse
* An authorization **token** for your Contact Center dataverse API

### Contact Center Configuration
> ⚠️ Configuration is currently only available through the API. In the near future we will add a configuration
interface to the Customer Service Admin Center. Your configurations will appear there when it is released
and it will not require any changes on your part.

> Configuration settings only need to be set once per workstream to enable both Android and iOS applications.

1. Create a ChannelInstanceSecret entry:

```bash
curl --request POST \
  --url https://{{org_url}}/api/data/v9.2/msdyn_channelinstancesecrets \
  --header 'authorization: Bearer {{token}}' \
  --header 'content-type: application/json' \
  --data '{
}'
```

2. Get ChannelInstance Secret Id (msdyn_channelinstancesecretid):

```bash
curl --request GET \
  --url https://{{org_url}}/api/data/v9.2/msdyn_channelinstancesecrets \
  --header 'authorization: Bearer {{token}}'
```

3. Update ChannelInstanceSecret entry with the following values:

```bash
curl --request PATCH \
  --url 'https://{{org_url}}/api/data/v9.2/msdyn_channelinstancesecrets({{msdyn_channelinstancesecretid}})' \
  --header 'authorization: Bearer {{token}}' \
  --header 'content-type: application/json' \
  --data '{
  "msdyn_name": "notificationHubConnectionString",
  "msdyn_secretvalue": "{{azurenotificationhub_connection_string}}"
}'
```

4. Create an Azure Notification Hub entry:

```bash
curl --request POST \
  --url https://{{org_url}}/api/data/v9.2/msdyn_azurenotificationhubs \
  --header 'authorization: Bearer {{token}}' \
  --header 'content-type: application/json' \
  --data '{
  
}'
```

5. Get Azure Notification Hub Id (msdyn_azurenotificationhubid):

```bash
curl --request GET \
  --url https://{{org_url}}/api/data/v9.2/msdyn_azurenotificationhubs \
  --header 'authorization: Bearer {{token}}'
```

6. Update Azure Notification Hub entry with the following values:

```bash
curl --request PATCH \
  --url 'https://{{org_url}}/api/data/v9.2/msdyn_azurenotificationhubs({{msdyn_azurenotificationhubid}})' \
  --header 'authorization: Bearer {{token}}' \
  --header 'content-type: application/json' \
  --data '{
  "msdyn_connectionstringid@odata.bind": "msdyn_channelinstancesecrets({{msdyn_channelinstancesecretid}})",
  "msdyn_defaultnotificationbody": "Default Notification Content",
  "msdyn_shownotificationtitle": false,
  "msdyn_notificationtitle": null,
  "msdyn_showmessagepreview": true,
  "msdyn_azurenotificationhubname": "{{azurenotificationhub_name}}"
}'
```
> **msdyn_defaultnotificationbody**: String, this is the default message shown in preview<br>
**msdyn_shownotificationtitle**: Bool, defines whether or not we show a title above the preview message<br>
**msdyn_notificationtitle**: String, the title shown if notificationtitle is true<br>
**msdyn_showmessagepreview**: Bool, defines whether the rep's message is shown in the push notification. If
false, defaultnotificationbody is always used. If true, defaultnotificationbody is only used when the agent
sends a message without text, such as an attachment.<br>
**msdyn_azurenotificationhubname**: String, your hub name in Azure Notification Hub.


7. Get LiveChatConfig id (msdyn_livechatconfigid) entry linked to the workstream

```bash
curl --request GET \
  --url 'https://{{org_url}}/api/data/v9.2/msdyn_livechatconfigs?%24filter=msdyn_widgetappid%2520eq%2520{{widget_app_id}}' \
  --header 'authorization: Bearer {{token}}'
```

8. Link the Azure Notification Hub to the LiveChatConfig entry:

```bash
curl --request PATCH \
  --url 'https://{{org_url}}/api/data/v9.2/msdyn_livechatconfigs({{msdyn_livechatconfigid}})' \
  --header 'authorization: Bearer {{token}}' \
  --header 'content-type: application/json' \
  --data '{
  "msdyn_azurenotificationhubid@odata.bind": "msdyn_azurenotificationhubs({{msdyn_azurenotificationhubid}})"
}'
```

## Configuring Application

**Initializing the Token inside SDK:**

Once your FCM project is created and the Firebase setup is complete, you can initialize the SDK within your application.  
Add the following line of code to set the device token before launching the chat:

   ```java
   LiveChatMessaging.getInstance().setFcmToken("YOUR_DEVICE_TOKEN")
   ```
## Custom font & font-family
To use a custom font in your application, follow these steps:

Steps:
1. Place the font_name.ttf file inside the res/font/ folder.
2. Override the following style in your application's res/values/styles.xml:

### Agent font & font-family

```xml
    <style name="AgentBubbleTextViewDefault" parent="@android:style/TextAppearance.Medium">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">wrap_content</item>
        <item name="android:textColor">@color/agentBubbleTextColor</item>
        <item name="android:textSize">@dimen/agentBubbleTextSize</item>
        <item name="android:textColorLink">@color/agentTextLinkColor</item>
        <item name="android:gravity">center_vertical</item>
        <item name="agentIcon">@drawable/ic_agent_avatar</item>
        <item name="android:textAllCaps">false</item>
        <item name="android:fontFamily">@font/arial</item>
        <item name="android:padding">@dimen/agentTextViewPadding</item>
    </style>
```
### Customer font & font-family
```xml
    <style name="CustomerBubbleTextViewDefault" parent="@android:style/TextAppearance.Medium">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">wrap_content</item>
        <item name="android:textColor">@color/customerBubbleTextColor</item>
        <item name="android:textSize">@dimen/customerBubbleTextSize</item>
        <item name="android:textAlignment">textStart</item>
        <item name="android:textAllCaps">false</item>
        <item name="android:fontFamily">@font/arial</item>
        <item name="android:padding">@dimen/customerTextViewPadding</item>
    </style>
```

### Image adaptive card font & font-family
```xml
<style name="ImageCardTitleDefault">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:textSize">@dimen/imageCardTitleTextSize</item>
        <item name="android:textColor">@color/imageCardTitleTextColor</item>
        <item name="android:padding">@dimen/imageCardTitlePadding</item>
        <item name="android:textColorLink">@color/agentTextLinkColor</item>
        <item name="android:textAllCaps">false</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>

    <style name="ImageCardSubTitleDefault">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:textSize">@dimen/imageCardSubTitleTextSize</item>
        <item name="android:padding">@dimen/imageCardSubTitlePadding</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>

    <style name="ImageCardTextDefault">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:textSize">@dimen/imageCardTextSize</item>
        <item name="android:padding">@dimen/imageCardTextPadding</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>
```
### Video adaptive card font & font-family
```xml
<style name="VideoCardTitleDefault">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">match_parent</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:layout_gravity">start</item>
        <item name="android:textColor">@color/txt_color_black</item>
        <item name="android:textSize">@dimen/videoCardTitleTextSize</item>
        <item name="android:textStyle">bold</item>
        <item name="android:layout_marginTop">@dimen/videoCardTitleMarginTop</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>

    <style name="VideoCardSubtitleDefault">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">match_parent</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:layout_gravity">left</item>
        <item name="android:textSize">@dimen/videoCardSubtitleTextSize</item>
        <item name="android:layout_marginTop">@dimen/videoCardSubtitleTextMarginTop</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>

    <style name="VideoCardTextDefault">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">match_parent</item>
        <item name="android:gravity">start|center_vertical</item>
        <item name="android:layout_gravity">left</item>
        <item name="android:textColor">@color/txt_color_black</item>
        <item name="android:textSize">@dimen/videoCardDescriptionTextSize</item>
        <item name="android:textStyle">bold</item>
        <item name="android:layout_marginTop">@dimen/videoCardSubtitleTextMarginTop</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>

    <style name="VideoCardButtonDefault">
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_width">match_parent</item>
        <item name="android:gravity">center</item>
        <item name="android:textAllCaps">false</item>
        <item name="android:singleLine">false</item>
        <item name="android:textStyle">bold</item>
        <item name="android:textSize">@dimen/videoCardButtonTextSize</item>
        <item name="android:fontFamily">@font/arial</item>
    </style>
```
### Basic adaptive card font & font-family
To support custom font for basic adaptive card text, override HostConfig.json file inside asset folder
```json
{
  "fontFamily": "arial",
  "fontTypes": {
    "default": {
      "fontSizes": {
        "small": 12,
        "default": 17,
        "medium": 15,
        "large": 17,
        "extraLarge": 19
      }
    },
    "monospace": {
      "fontSizes": {
        "small": 12,
        "default": 17,
        "medium": 15,
        "large": 17,
        "extraLarge": 19
      }
    }
  },
  "containerStyles": {
    "default": {
      "foregroundColors": {
        "default": {
          "default": "#000000"
        }
      },
      "backgroundColor": "#FFFFFF"
    }
  },
  "actions": {
    "actionsOrientation": "Vertical",
    "actionAlignment": "stretch"
  }
}
```

### Markdown parsing limitations
Android currently has limited support for Markdown, particularly with blockquotes.

Single-line blockquotes (e.g., > This is a quote) are generally supported.

However, complex combinations of Markdown elements within a blockquote — such as bold, lists, or nested formatting — may not render correctly.

Multi-line blockquotes or blockquotes containing rich Markdown may display inconsistently across devices or Android versions.






 



