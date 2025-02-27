# Dynamics 365 Contact Center - Messaging SDK - Android

> ⚠️ The software is available as a Public Preview with release support for select Microsoft
partners and customers. To receive support for your release, contact CC-Mobile-Preview@microsoft.com 
with your release date and plans. Customers must have an agreement with the Contact Center team
to guarantee timely support during the Preview period.

## Table of Contents

  * [About](#about)
  * [Installation](#installation)
    + [Pre-Requisites](#pre-requisites)
    + [Building the Sample App - LCWsample](#building-the-sample-app---lcwsample)
      - [Manual Integration](#manual-integration)
      - [Integration through Gradle](#integration-through-gradle)
    + [Android Studio (Recommended)](#android-studio-recommended)
    + [Instructions for Using the Chat Feature in the Sample Android App](#instructions-for-using-the-chat-feature-in-the-sample-android-app)
  * [Initialization](#initialization)
    + [Troubleshooting](#troubleshooting)

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

### Building the Sample App - LCWsample

#### Manual Integration:
1. Clone the repository. 
    git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Manually download the AAR files from release for desired version - https://github.com/microsoft/ContactCenterMessagingSDK-android/releases
4. Place aar files in libs.
5. Build the app based on your build tools.
6. Run the app

#### Integration through Gradle:
1. Clone the repository. 
    git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Update the desired 'sdkVersion' in the app's build.gradle file.
4. Download the AAR files - "./gradlew downloadAarFiles"
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

The engagement is initialized with the LCWApiResponse API call for bot Copilot Studio conversations and Customer Service Rep conversations. 

Upon a successful request, the CompletionHandler's onResponse method is invoked (Optional), returning an ApiResult with either a success or error response.

> ❗ Note: You should not directly instantiate the LCWApiResponse class. Instead use the following Messaging builder class: _Completionhandler is optional interface to get callbacks of APi Response_

```.kt
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

### Troubleshooting
If you face build issue related to namespace for randombytes package. Update namespace in its build.gradle.  

namespace 'com.bitgo.randombytes' (path: node_modules -> react-native-randombytes -> android -> build.gradle) 
