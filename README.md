# Corona Defense Game Client

Client for the Corona Defense game, written in Kotlin and using the [libGDX](https://libgdx.com/) game development framework. Server implemented in [`swa-group1/corona-defense-server`](https://github.com/swa-group1/corona-defense-server).

## Project Structure

In `core/src/com/coronadefense`:

- `api` communicates with the server.
- `receiver` recieves messages from the server broadcaster about game events.
- `states` contains the different game states (menus, play states, etc.).
- `types` contains type definitions for various game objects.
- `utils` provides utility classes.
  - `Constants` contains global constants for the game client.

## How To Run

### Desktop

- Download the .jar file from https://github.com/swa-group1/corona-defense-client/releases/tag/desktop-1.0
- Run it (your system must have Java configured to run the .jar file)

### Android

- Download the .apk file from https://github.com/swa-group1/corona-defense-client/releases/tag/android-1.0
- Connect your device to your computer by USB, and turn on “File transfer” (should be a pop-up in your device’s notification list)
- Transfer the file to your device’s Internal Storage (your device should be visible under “This PC” on Windows)
- On your device, navigate to the internal storage (on modern Android devices, there should be an app called “Files”, where you can scroll down to “Internal Storage”)
- Click the .apk file there, and click Install
- Click “Install anyway” at the warning from Play Protect
- Click “Don’t send” at Android’s second warning
- Now there should be an app on your device called “Corona Defense”, ready to play!
