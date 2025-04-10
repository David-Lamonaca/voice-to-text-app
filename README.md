# 🗣️ Voice Control - Desktop Voice-to-Text App

A voice-to-text Java desktop application that allows you to control your system using spoken commands. Built with JavaFX and Vosk for offline speech recognition, and supports customization features including push-to-talk keys and keyword-triggered actions.

[Download Installer](https://www.dropbox.com/scl/fi/1t4aibshe9nnj27u0b1xr/VoiceControl_Installer-1.0.0.zip?rlkey=857k6m8tr0yb2y2eic4ypco90&st=ki4w6wu3&dl=0)

---

## 🚀 Features

- 🎙️ **Push-to-Talk Voice Input** – Hold a configurable key (e.g. `Shift`) to speak commands.
- 🧠 **Custom Keywords** – Define keywords that can:
  - Launch apps (e.g., "Notepad" opens Notepad)
  - Type out text or URLs (e.g., "Email" types an address)
  - Simulate key presses (e.g., "Space" simulates a space key)
- 🔒 **Global Key Hooking** – Monitors input across the entire OS.
- 🧩 **Modular Settings System** – Easily configurable via JSON settings file or in-app UI.
- 🌙 **Dark, Sleek UI** – Minimalist and distraction-free design.

---

## 🧠 Keyword Types

| Type      | Description                                      |
|-----------|--------------------------------------------------|
| Execute   | Opens an app (e.g., `"notepad"` ➝ Notepad)       |
| Typing    | Types a phrase or sentence                       |
| KeyPress  | Simulates a specific key                         |

Hold the **Push-to-Talk** key and say the keyword to trigger the action.

---

## ⚙️ Configuration

Settings are managed in `settings.json`. You can change:

- `pushToTalkKey`: e.g., `"SHIFT"`
- `keywordActivationKey`: e.g., `"CRTL"`
- Add/edit keywords under `keywords`

Example:
```json
{
  "pushToTalkKey": "SHIFT",
  "keywords": [
    { "word": "notepad", "type": "Execute", "value": "notepad.exe" },
    { "word": "email", "type": "Typing", "value": "example@email.com" },
    { "word": "space", "type": "KeyPress", "value": "SPACE" }
  ]
}
```

---

## 📦 Dependencies

- [JavaFX](https://openjfx.io/)
- [Vosk API](https://alphacephei.com/vosk/)
- [JNA](https://github.com/java-native-access/jna) – for native input hooks
- [GSON](https://github.com/google/gson)

---

## 🧪 TODOs / Coming Soon

- 🖱️ Global Mouse Hook Support
- 🖮 Multi KeyPress execution for keyboard shortcuts
- 🔊 Better Speech Recognition

---

## 💬 License

MIT License — feel free to fork, improve, and use!
