# 🧠 Maze of Knowledge

**Maze of Knowledge** is a gamified learning application designed to make mastering **Java**, **aptitude**, and **logical reasoning** both fun and immersive. Built using **JavaFX**, the app blends traditional quiz formats with engaging arcade-style gameplay — from pixel-art runners to quiz-fueled combat. Every interaction is enhanced with custom audio, sprite animations, and intuitive UI design.

---

## 🎮 Game Modes

- **Classic Quiz:**  
  Timed multiple‑choice questions with combat-style sprite animations and sound cues. Includes code-based questions and a built-in code editor powered by the JDoodle API for real-time code execution.

- **Adventure Mode:**  
   Adventure Mode was planned as a 5th module but was postponed for better gameplay and stability. It may be released as a standalone game later.

- **Matching Tiles:**  
  Flip-and-match style memory game. Players uncover tiles to find matching question-answer pairs, testing logic and retention.

- **Pixel Retro Runner:**  
  A fast-paced side-scrolling runner inspired by retro arcade games. Dodge obstacles, collect power-ups, and score points.

- **Fighting Quiz:**  
  A quiz-powered fighting game where correct answers unleash special attacks and animations.

---

## 🌐 Other Features

- **Leaderboard:**  
  Track global rankings with Supabase backend integration.

- **Settings:**  
  Personalize your experience with theme selection, sound and music volume controls, and quiz timer customization.

- **Live Code Execution:**  
  Solve Java or python programming challenges directly within the quiz using JDoodle’s API.

- **Animated UI:**  
  Polished main menu with ambient audio and a looping sprite animation. Intuitive navigation and visual feedback throughout.

---

## 🚀 Tech Stack

- JavaFX (UI & Game Logic)  
- Supabase (Authentication & Leaderboard)  
- JDoodle API (Code Execution)  
- FXML / CSS (UI Design)

---


## Table of Contents
- [Project Overview](#project-overview)
- [Folder Structure](#folder-structure)
- [Project Description](#project-description)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Download Required Libraries](#2-download-required-libraries)
  - [3. Configure Your IDE](#3-configure-your-ide)
  - [4. Supabase & JDoodle Setup](#4-supabase--jdoodle-setup)
  - [5. Compile and Run](#5-compile-and-run)
- [Project Modules & Detailed Documentation](#project-modules--detailed-documentation)
- [Assets & Resources](#assets--resources)
- [Customization](#customization)
- [Contributing](#contributing)
- [License](#license)
- [Troubleshooting](#troubleshooting)
- [Acknowledgements](#acknowledgements)

## Folder Structure
```
Maze_of_Knowledge/
├── Readme.md
├── assets/
│   ├── Icon/
│   │   ├── Computer/
│   │   │   ├── Bringer of Death/
│   │   │   │   └── Bringer of Death.png
│   │   │   ├── Demon King/
│   │   │   │   └── Demon King.png
│   │   │   └── Frost Guardian/
│   │   │       └── Frost Guardian.png
│   │   └── Player/
│   │       ├── Professor Grumps/
│   │       │   └── icon.png
│   │       └── Sarah Holy Knight/
│   │           └── icon.png
│   ├── images/
│   │   ├── arena_bg.jpeg
│   │   ├── bulb.png
│   ├── sounds/
│   │   ├── applause.mp3
│   │   ├── Autumn.mp3
│   │   ├── Rain.mp3
│   │   ├── Snow.mp3
│   │   ├── Summer.mp3
│   │   ├── tileflip.mp3
│   │   └── wrong.mp3
│   └── Sprite/
│       ├── Computer/
│       │   ├── Bringer of Death/
│       │   │   ├── Attack/
│       │   │   │   └── Cleave/{...PNG files...}
│       │   │   ├── Hurt/{...PNG files...}
│       │   │   └── Idle/{...PNG files...}
│       │   ├── Demon King/
│       │   │   ├── Attack/Cleave/{...PNG files...}
│       │   │   ├── Hurt/{...PNG files...}
│       │   │   └── Idle/{...PNG files...}
│       │   └── Frost Guardian/
│       │       ├── Attack/Cleave/{...PNG files...}
│       │       ├── Hurt/{...PNG files...}
│       │       └── Idle/{...PNG files...}
│       ├── FightingGame/
│       │   ├── background.png
│       │   ├── fireball.png
│       │   ├── shop.png
│       │   ├── kenji/{...PNG files...}
│       │   └── samuraiMack/{...PNG files...}
│       └── Player/
│           ├── Professor Grumps/{...subfolders...}
│           ├── Sarah Holy Knight/{...subfolders...}
│           └── The Blind Huntress/{...subfolders...}
├── docs/
│   └── Documentation.docx
├── lib/
│   └── JAR/
│       └── json-20230227.jar
├── out/
│   └── (compiled .class files mirrored from src/)
├── resources/
│   ├── css/
│   │   ├── dashboard.css
│   │   └── style.css
│   ├── fxml/
│   │   ├── Dashboard.fxml
│   │   ├── LoginView.fxml
│   │   └── SignupView.fxml
│   └── questions/
│       └── questions_cache.json
└── src/
    └── main/
        ├── api/
        │   └── JDoodleClient.java
        ├── app/
        │   ├── MainMenu.java
        │   └── VTUGamifiedQuizApp.java
        ├── auth/
        │   ├── AuthService.java
        │   ├── LoginController.java
        │   ├── SessionManager.java
        │   └── SignupController.java
        ├── dashboard/
        │   └── DashboardController.java
        ├── game/
        │   ├── Adventure/
        │   │   └── AdventureMode.java
        │   ├── Classic/
        │   │   ├── Fighting/{...Java files...}
        │   │   └── RetroRunning/PixelRetroRunner.java
        ├── leaderboard/
        │   ├── LeaderboardEntry.java
        │   ├── LeaderboardScene.java
        │   └── LeaderboardService.java
        ├── quiz/
        │   ├── FullQuestionPopup.java
        │   ├── MatchingTitlesScene.java
        │   ├── Question.java
        │   ├── QuestionService.java
        │   ├── QuizScene.java
        │   └── WrongAnswerRecord.java
        ├── sprites/
        │   ├── CatFaceBuilder.java
        │   └── individualSpriteAnimation.java
        ├── ui/
        │   ├── ClassicModeSelectionPopup.java
        │   ├── CodeChallengeScene.java
        │   ├── SettingsScene.java
        │   ├── SpriteAnimationPane.java
        │   └── ThemeManager.java
        └── utils/
            ├── AudioManager.java
            ├── PreferencesManager.java
            └── SceneManager.java
```

## Project Description
This version (V3) extends the VTU Gamified Quiz App with:
- **Adventure Mode:** A platformer with integrated quiz prompts.
- **Matching Titles Game:** Pair questions and answers in a tile-matching format.
- **Code Challenge Scene:** Write and execute code snippets via JDoodle API.
- **Enhanced Asset Management:** Structured folders for icons, sprites, images, and sound.
- **Comprehensive Documentation:** `Documentation.docx` with in-depth, per-file explanations.

## Features
- **Multiple Game Modes**: Classic Quiz, Adventure Platformer, Matching Game, Code Execution.
- **User Accounts**: Signup/login backed by Supabase.
- **Online Leaderboard**: Real-time score tracking and rankings.
- **UI Theming & Audio**: Light/dark themes and background music/SFX controls.
- **Offline Cache**: Local JSON question fallback via `questions_cache.json`.
- **Animations & Sprites**: Custom character and enemy animations for immersive gameplay.

## Prerequisites
- **JDK** 11 or higher.
- **JavaFX SDK** matching your JDK.
- **Gson** library (`json-20230227.jar`).
- **Supabase** account and project.
- **JDoodle** credentials (Client ID & Secret).
- **Maven/Gradle** (optional but recommended).

## Setup Instructions
1. **Clone the repo**  
   `https://github.com/aleenaharoldpeter/Maze_of_Knowledge.git`
2. **Place JAR**  
   Copy `json-20230227.jar` into `lib/JAR/`.
3. **Configure IDE**  
   - Add JavaFX modules (`javafx.controls`, `javafx.fxml`, etc.).  
   - Include `lib/JAR/json-*.jar`.  
   - Mark `resources/` as resources root.
4. **Environment Variables**  
   ```bash
   export SUPABASE_URL="your_supabase_url"
   export SUPABASE_KEY="your_anon_or_service_key"
   export JD_CLIENT_ID="your_jdoodle_client_id"
   export JD_CLIENT_SECRET="your_jdoodle_client_secret"
   ```
5. **Build & Run**  
   ```bash
   mvn clean javafx:run
   # or compile and run MainApp.java in your IDE
   ```

## Project Modules & Detailed Documentation

> **See `docs/Documentation.docx`** for an in‑depth, line‑by‑line breakdown.  
Below is a summary of all Java source files and their responsibilities.

### api
- **`JDoodleClient.java`**  
  Handles HTTP requests and JSON serialization to execute user‑submitted code via the JDoodle REST API.

### app
- **`VTUGamifiedQuizApp.java`**  
  Main application entry point; initializes preferences, audio, themes, and launches the primary scene.
- **`MainMenu.java`**  
  Constructs and displays the dashboard UI with navigation buttons for all game modes and settings.

### auth
- **`AuthService.java`**  
  Provides methods to call Supabase auth endpoints for signup, login, and token management.
- **`SessionManager.java`**  
  Manages the in‑memory session state and persists the current user token.
- **`SignupController.java`**  
  JavaFX controller for the signup form; validates input and invokes `AuthService`.
- **`LoginController.java`**  
  JavaFX controller for the login form; handles authentication flow and error display.

### dashboard
- **`DashboardController.java`**  
  Controller for the main menu scene; routes user actions to the chosen game mode or settings.

### game

####  Adventure
- **`AdventureMode.java`**  
  Implements the platformer quiz mode: player movement, collision detection, and quiz question triggers.

#### Classic
- **`Fighter.java`**  
  Model for a combatant character in the fighting mini‑game (health, attack, animations).
- **`FightingGame.java`**  
  Orchestrates the classic fighting game loop, enemy spawning, and round outcomes.
- **`Fireball.java`**  
  Represents a projectile entity with movement logic and collision handling.
- **`Question.java`**  
  In‑game question model for the fighting game variant; pairs quiz prompts with combat events.
- **`QuestionManager.java`**  
  Loads and tracks question usage within the fighting game context to avoid repeats.
- **`Sprite.java`**  
  Base class for animated game sprites, handling frame updates and rendering positions.
- **`SpriteInfo.java`**  
  Encapsulates metadata for sprite sheets (frame size, count, animation speed).
- **`Utility.java`**  
  Shared helper methods for coordinate transforms, timing, and randomization.

#### RetroRunning
- **`PixelRetroRunner.java`**  
  Implements a side‑scroll “retro runner” mode with obstacles, power‑ups, and score tracking.

### leaderboard
- **`LeaderboardEntry.java`**  
  JavaFX data model (with properties) representing a single leaderboard row.
- **`LeaderboardScene.java`**  
  Controller for the leaderboard view; fetches entries and displays them in a sorted table.
- **`LeaderboardService.java`**  
  Communicates with Supabase REST API to post new scores and retrieve top rankings.

### quiz
- **`Question.java`**  
  Core quiz question model (text, options, answer, hint, explanation).
- **`QuestionService.java`**  
  Loads questions from Supabase or local cache (`questions_cache.json`) and handles fallback logic.
- **`QuizScene.java`**  
  Controller for the classic quiz mode: displays questions, manages timer, scoring, and hints.
- **`FullQuestionPopup.java`**  
  Displays a modal with the complete question text and all options for better readability.
- **`MatchingTitlesScene.java`**  
  Implements the matching game: drag‑and‑drop pairing of questions and answers.
- **`WrongAnswerRecord.java`**  
  Model for logging incorrectly answered questions for review or retry.

### sprites
- **`CatFaceBuilder.java`**  
  Utility to assemble and animate cat‑face avatars from individual sprite parts.
- **`individualSpriteAnimation.java`**  
  Low‑level frame animation helper for any sprite set.

### ui
- **`ClassicModeSelectionPopup.java`**  
  Popup UI to choose between the classic quiz and matching titles game modes.
- **`SettingsScene.java`**  
  Controller for the settings view; lets users toggle themes, audio, and timer preferences.
- **`ThemeManager.java`**  
  Applies CSS themes (light/dark/custom) across all scenes based on user choice.
- **`SpriteAnimationPane.java`**  
  Reusable JavaFX Pane that plays looping sprite animations for UI flair.
- **`CodeChallengeScene.java`**  
  Scene controller for the code challenge mode: code editor, run button, and output viewer.

### utils
- **`AudioManager.java`**  
  Loads and controls background music and sound effects (applause, tile flip, wrong answer).
- **`SceneManager.java`**  
  Centralized helper to switch between JavaFX scenes, handling transitions and cleanup.
- **`PreferencesManager.java`**  
  Persists and retrieves user settings (volume, theme, timer duration) via the Java Preferences API.


## Assets & Resources
- **`assets/`**: All images, sounds, sprites, and icons.
- **`resources/css` & `resources/fxml`**: UI styling and layouts.
- **`resources/questions/questions_cache.json`**: Offline question cache.
- **`docs/Documentation.docx`**: Developer guide.

## Customization
- **Add/Edit Questions**: Modify `questions_cache.json` or integrate new endpoints.
- **UI/Theme**: Update CSS or extend `ThemeManager`.
- **Game Modes**: Tweak speed, difficulty, or add new modes.
- **API Endpoints**: Adjust in `LeaderboardService` or `QuestionService`.

## Contributing
1. Fork & branch (`feature/xyz`).
2. Commit with descriptive messages.
3. Pull Request for review.

## License
MIT License – see [LICENSE](LICENSE).

## Troubleshooting
- **JavaFX issues**: Validate module paths and SDK version.
- **API errors**: Check environment variables and network connectivity.
- **Cache loading**: Ensure JSON validity and correct file paths.

## Acknowledgements
- **JavaFX** – UI framework  
- **Supabase** – Backend service  
- **JDoodle API** – Code execution  
- **Google Gson** – JSON parsing
