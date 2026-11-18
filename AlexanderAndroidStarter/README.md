# ALEXANDER Android Prototype

Open-source Android prototype for **Alexander**, a personal assistant designed to speak Egyptian Arabic (`ar-EG`).

## Current prototype features
- Jetpack Compose dark chat interface.
- Tap-to-talk speech recognition using Android `SpeechRecognizer`.
- Arabic Text-to-Speech response (availability depends on device voice data).
- Microphone runtime permission handling.
- Transparent prototype replies; **no AI model or phone-control tools are connected yet**.

## Build an APK automatically with GitHub Actions (no Android Studio needed)
1. Create a GitHub account or sign in: https://github.com
2. Create a new repository named `Alexander` (choose Public if you want it open source).
3. Download the project ZIP and extract it. Upload the **contents** of the `AlexanderAndroidStarter` folder to the repository root (the root should contain `settings.gradle.kts`, `build.gradle.kts`, `app/`, and `.github/`).
   - Easiest on mobile: open the repository, use **Add file → Upload files**, select the extracted project files, and commit them to the `main` branch. If uploading folders is inconvenient, use GitHub's web upload or a Git client.
4. Open the repository's **Actions** tab. If prompted, enable Actions/workflows.
5. The workflow **Build Alexander APK** starts automatically after a push to `main`/`master`. You can also select it and press **Run workflow**.
6. Open the completed run. Under **Artifacts**, download **Alexander-APK**. Extract the artifact ZIP to find `app-debug.apk`.
7. Transfer/open the APK on your Android phone and allow your browser/file manager to install unknown apps if Android asks.

The workflow also runs on pull requests to `main`/`master`. It builds a **debug APK** (not a production-signed release). This prototype has not been verified on a physical device in this workspace.

## Build locally
Requires Android Studio and JDK 17. Open the project, sync Gradle, then run the `app` configuration or execute `gradle assembleDebug`.

## Roadmap
- Secure backend AI connection (never embed API keys in the APK).
- Voice conversation loop and interruption handling.
- Allowlisted Android actions with explicit confirmation for sensitive operations.
- Accessibility-based UI automation only for appropriate use cases and with user-granted permission.
- Official integrations where available (Telegram bot API; WhatsApp Business Platform for business messaging).
- Memory, task management, and notification controls.

## Privacy and safety
This prototype does not continuously listen, does not control other apps, and does not transmit chat text to an AI backend. Speech recognition behavior may depend on the Android speech service/device.

## License
MIT.
