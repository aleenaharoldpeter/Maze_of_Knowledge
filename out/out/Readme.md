# 🧠 Maze of Knowledge

**Maze of Knowledge** is a gamified learning application designed to make mastering **Java**, **aptitude**, and **logical reasoning** both fun and immersive. Built using **JavaFX**, the app blends traditional quiz formats with engaging arcade-style gameplay — from pixel-art runners to quiz-fueled combat. Every interaction is enhanced with custom audio, sprite animations, and intuitive UI design.

---

## 📹 Demo

Check out a quick walkthrough of **Maze of Knowledge**:  
🎥 [Watch Demo on YouTube](https://www.youtube.com/watch?v=bwtE8abALCU)


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
- [Notice](#notice)
- [Troubleshooting](#troubleshooting)
- [Acknowledgements](#acknowledgements)
- [Credits](#credits)

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
Apache License 2.0 – see [LICENSE](LICENSE).

## Notice
Maze of Knowledge
Copyright 2025 Aleena Harold Peter

This product includes software developed by:
- JDoodle (for code execution via API)
- Supabase (for authentication and database)
- JavaFX (UI toolkit)
- Google Gson (JSON parsing)

This product includes multimedia assets (sprites, sound, and design elements) licensed under:
- Liberated Pixel Cup (LPC): CC BY-SA 3.0 – See CREDITS.csv
- Itch.io assets by Clembod, chierit, LuizMelo, Brullov, etc. – See linked licenses in README
- Pixabay Sound Effects: Pixabay License – Royalty-free

No modifications were made to these assets unless otherwise stated in the documentation.

Additional thanks to:
- Chris Courses – Inspiration for fighting game mechanics
- Microsoft Designer – For generating specific UI assets

Apache License, Version 2.0 applies to the source code unless otherwise noted.


## Troubleshooting
- **JavaFX issues**: Validate module paths and SDK version.
- **API errors**: Check environment variables and network connectivity.
- **Cache loading**: Ensure JSON validity and correct file paths.

## Acknowledgements
- **JavaFX** – UI framework  
- **Supabase** – Backend service  
- **JDoodle API** – Code execution  
- **Google Gson** – JSON parsing

## Credits

## Quiz & Mascot

### Player
- [*SHADOW Series – The Blind Huntress Escape*](https://itch.io/queue/c/4691964/heroes?game_id=2599294&password=)  
- [*Professor Grumps*](https://liberatedpixelcup.github.io/Universal-LPC-Spritesheet-Character-Generator/#?body=Body_color_light&head=Human_male_light)  
- [*Sarah Holy Knight*](https://liberatedpixelcup.github.io/Universal-LPC-Spritesheet-Character-Generator/#?body=Body_color_light&head=Human_male_light)  

### Computer
- [*Bringer Of Death (Free)*](https://clembod.itch.io/bringer-of-death-free) by Clembod  
- [*Demon King*](https://chierit.itch.io/boss-demon-slime) by chierit  
- [*Boss: Frost Guardian*](https://chierit.itch.io/boss-frost-guardian) by chierit  

*Professor Grumps* and *Sarah Holy Knight* were generated using the *Liberated Pixel Cup (LPC) Character Generator*.  

<details>
<summary>Click to expand full list of LPC contributors</summary>

Sprites by: Johannes Sjölund (wulax), Michael Whitlock (bigbeargames), Matthew Krohn (makrohn), Nila122, David Conway Jr. (JaidynReiman), Carlo Enrico Victoria (Nemisys), Thane Brimhall (pennomi), laetissima, bluecarrot16, Luke Mehl, Benjamin K. Smith (BenCreating), MuffinElZangano, Durrani, kheftel, Stephen Challener (Redshrike), William Thompsonj, Marcel van de Steeg (MadMarcel), TheraHedwig, Evert, Pierre Vigier (pvigier), Eliza Wyatt (ElizaWy), Sander Frenken (castelonia), dalonedrau, Lanea Zimmerman (Sharm), Manuel Riecke (MrBeast), Barbara Riviera, Joe White, Mandi Paugh, Shaun Williams, Daniel Eddeland (daneeklu), Emilio J. Sanchez-Sierra, drjamgo, gr3yh47, tskaufma, Fabzy, Yamilian, Skorpio, Tuomo Untinen (reemax), Tracy, thecilekli, LordNeo, Stafford McIntyre, PlatForge project, DCSS authors, DarkwallLKE, Charles Sanchez (CharlesGabriel), Radomir Dopieralski, macmanmatty, Cobra Hubbard (BlueVortexGames), Inboxninja, kcilds/Rocetti/Eredah, Napsio (Vitruvian Studio), The Foreman, AntumDeluge  

Source: Sprites contributed as part of the [Liberated Pixel Cup project](https://opengameart.org/content/lpc-collection)  
License: [Creative Commons Attribution-ShareAlike 3.0](http://creativecommons.org/licenses/by-sa/3.0/)  
Detailed credits: [See full credits](CREDITS.csv)

</details>

---

## 1v1 Fighting Sprites
- [*Martial Hero*](https://luizmelo.itch.io/martial-hero) by LuizMelo  
- [*Martial Hero 2*](https://luizmelo.itch.io/martial-hero-2) by LuizMelo  
- [*Fireball*](https://pixabay.com/vectors/fireball-comet-meteor-fire-417899/)  
- [*Oak Woods Environment Asset*](https://brullov.itch.io/oak-woods) by Brullov  

---

## Leaderboard
- arena_bg and bulb — Generated using *Microsoft Designer*

---

## Tutorials & Inspiration
- [*Chris Courses – JavaScript Fighting Game Tutorial*](https://www.youtube.com/watch?v=vyqbNFMDRGQ)

---

# 🎵 Sound Effects

| Sound      | Author                 | Link |
|------------|------------------------|------|
| Tile Flip  | u_y3wk5ympz8          | [Listen](https://pixabay.com/sound-effects/flip-switch-304548/) |
| Wrong      | KevinVG207 (Freesound) | [Listen](https://pixabay.com/sound-effects/wrong-buzzer-6268/) |
| Snow       | JCI-21                 | [Listen](https://pixabay.com/sound-effects/wind-blowing-sfx-12809/) |
| Rain       | JCI-21                 | [Listen](https://pixabay.com/sound-effects/rain-sfx-12819/) |
| Summer     | Nasrx15               | [Listen](https://pixabay.com/sound-effects/anime-cicadas-chirping-satisfying-261810/) |
| Autumn     | MIGHTUSER             | [Listen](https://pixabay.com/sound-effects/sound-of-rustling-leaves-in-a-light-breeze-hd-260729/) |
| Applause   | Gronkjaer (Freesound) | [Listen](https://pixabay.com/sound-effects/rightanswer-95219/) |

*License:* [Pixabay License](https://pixabay.com/service/license/) (royalty-free, attribution not required but appreciated)

---

## Licenses Summary
- *Liberated Pixel Cup (LPC):* [CC BY-SA 3.0](http://creativecommons.org/licenses/by-sa/3.0/)  
- *Pixabay Sounds & Graphics:* [Pixabay License](https://pixabay.com/service/license/)  
- *Other Itch.io Assets:* License as per respective creators (linked above)