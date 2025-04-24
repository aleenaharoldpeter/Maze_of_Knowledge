// package src.main;
package src.main.app;

import src.main.utils.PreferencesManager;
import src.main.utils.AudioManager;
import src.main.utils.SceneManager;
import src.main.auth.SessionManager;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class VTUGamifiedQuizApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Delay applying the stylesheet until the scene is set
        Scene scene = primaryStage.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
        }        
        // Initialize managers
        PreferencesManager.initialize();
        AudioManager.initialize();
        SceneManager.initialize(primaryStage);

        // Check session and set initial scene
        String token = SessionManager.getToken();
        if (token != null) {
            SceneManager.showMainMenu();
        } else {
            SceneManager.showLogin();
        }

        // Set stage to fullscreen
        primaryStage.setMaximized(true);

        // // Delay applying the stylesheet until the scene is set
        // Scene scene = primaryStage.getScene();
        // if (scene != null) {
        //     scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        // }
    }

    // public static void main(String[] args) {
    //     launch(args);
    // }
    public static void main(String[] args) {
        launch(args);
        System.out.println("It works!");
    }    
}




// To compile and run this application, use the following commands:
// 
// For standard mode (JavaFX + Gson):
// javac --enable-preview --release 22 --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -cp ".;C:\Program Files\Gson\gson-2.12.1.jar" *.java
// java --enable-preview --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -cp ".;C:\Program Files\Gson\gson-2.12.1.jar" VTUGamifiedQuizApp
//
// javac --enable-preview --release 22 --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -cp ".;C:\Users\akhil\Programming\Aleena\VTU_QUIZ_V2\JAR\json-20230227.jar;C:\Program Files\Gson\gson-2.12.1.jar" *.java
// java --enable-preview --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -cp ".;C:\Users\akhil\Programming\Aleena\VTU_QUIZ_V2\JAR\json-20230227.jar;C:\Program Files\Gson\gson-2.12.1.jar"  VTUGamifiedQuizApp
//
// For updated adventure mode with LWJGL, use:
// javac --enable-preview --release 22 --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -cp ".;C:\Program Files\Gson\gson-2.12.1.jar;C:\Program Files\Lwjgl\lwjgl.jar;C:\Program Files\Lwjgl\lwjgl-glfw.jar;C:\Program Files\Lwjgl\lwjgl-opengl.jar;C:\Program Files\Lwjgl\lwjgl-stb.jar" *.java
// java --enable-preview --module-path "C:\Program Files\JavaFX\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics -Djava.library.path="C:\Program Files\Lwjgl\natives" -cp ".;C:\Program Files\Gson\gson-2.12.1.jar;C:\Program Files\Lwjgl\lwjgl.jar;C:\Program Files\Lwjgl\lwjgl-glfw.jar;C:\Program Files\Lwjgl\lwjgl-opengl.jar;C:\Program Files\Lwjgl\lwjgl-stb.jar" VTUGamifiedQuizApp
