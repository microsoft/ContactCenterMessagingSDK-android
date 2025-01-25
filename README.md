## Building the Sample App

Manual Integration:
1. Clone the repository. 
    git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
4. Manually download the AAR files from release for desired version - https://github.com/microsoft/ContactCenterMessagingSDK-android/releases
5. Place aar files in libs.
6. Build the app based on your build tools.
7. Run the app

Integration through gradle:
1. Clone the repository. 
    git clone https://github.com/microsoft/ContactCenterMessagingSDK-android.git
2. Open the terminal and run "npm install" on root folder (provided package.json copied to root folder). 
3. Update the desired 'sdkVersion' in the app's build.gradle file.
4. Download the AAR files - "./gradlew downloadAarFiles"
5. Sync/configure the app.
6. Run the app.

## Android Studio (Recommended)
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

## Instructions for Using the Chat Feature in the Sample Android App:
1. Paste Your Script or Add the Required Information:
In your app’s landing screen, you will find input fields where you need to enter:
orgId
orgUrl
widgetId

Alternatively, you can paste a script, that automatically fills in these details for you.

2. Click on the "Let's Chat" Button:
Once you've entered the required information (or pasted the script), look for a button labeled "Let's Chat" on your screen.
Tap on this button to initiate the chat. The app will connect to the specified chat system and load the widget for you.

3.Start Interacting with the Chat:
After clicking the button, you will see the chat interface appear on the screen.
You can now type messages, send media, or interact with the chat in real-time. 
The app will allow you to communicate with customer support or any automated services available.

## App Screenshot

Here's a screenshot of the app in action:

![Screenshot of the App](LCWsample/screenshot_01.png)
![Screenshot of the App](LCWsample/screenshot_02.png)


## How to Use

1. Paste your script or add orgId, orgUrl, and widgetId separately.
2. Click on the "Let's Chat" Button.
3. Start interacting with the chat.

## Troubleshoot
If you face build issue related to namespace for randombytes package. Update namespace in its build.gradle.  

namespace 'com.bitgo.randombytes' (path: node_modules -> react-native-randombytes -> android -> build.gradle) 

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
