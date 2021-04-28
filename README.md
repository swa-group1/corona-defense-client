# Welcome to Corona Defense frontend
Kotlin/LibGDX frontend.

## How to run

### Desktop
* Download the .jar file from https://github.com/SWA-group-1/frontend/releases/tag/desktop-1.0
* Run it (your system must have Java configured to run the .jar file)

### Android
* Download the .apk file from https://github.com/SWA-group-1/frontend/releases/tag/android-1.0
* Connect your device to your computer by USB, and turn on “File transfer” (should be a pop-up in your device’s notification list)
* Transfer the file to your device’s Internal Storage (your device should be visible under “This PC” on Windows)
* On your device, navigate to the internal storage (on modern Android devices, there should be an app called “Files”, where you can scroll down to “Internal Storage”)
* Click the .apk file there, and click Install
* Click “Install anyway” at the warning from Play Protect
* Click “Don’t send” at Android’s second warning
* Now there should be an app on your device called “Corona Defense”, ready to play!

## Structure

### API
Communicates with the backend server.

### Reciever
Recieves messages from the backend broadcaster about game events.

### States
The different game states (menus, play states, etc.).

### Types
Type definitions for different game objects.

### Utilities
Utility classes.
See Constants.kt for game global constants.
