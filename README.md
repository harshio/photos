
# photos
The final project is in the main branch; Chloe's initial commit is in the second branch. Also note that, on MacOS, after closing the AlertDialog window, the main window of the application is not automatically refocused on. You'll probably notice that the code's still running; The application is hidden, still running, in some java folder/icon located at the taskbar of your laptop. I don't know if this disclaimer is necessary, but I wanted to be cautious. Anyway, here's what to write in the terminal (after navigating to the root of this project in the terminal of course) to compile the code in case you don't have VSCode.

(Make sure to replace /ABSOLUTE/PATH/TO/javafx-sdk-21.0.6/lib with the actual absolute path to your JavaFX SDK in your computer)

First paste in:

```bash

javac \
--module-path /ABSOLUTE/PATH/TO/javafx-sdk-21.0.6/lib \
--add-modules javafx.controls,javafx.fxml \
-d out \
$(find src -name "*.java")
```

to create a directory called out where the compiled .class files are stored. Then paste in:

cp -r src/photos/view out/photos/

so that /photos/view/Login.fxml is available in the runtime classpath. Then finally run the code with:

```bash
java \
--module-path /ABSOLUTE/PATH/TO/javafx-sdk-21.0.6/lib \
--add-modules javafx.controls,javafx.fxml \
-cp out \
photos.app.Photos
```

and the application should run without issue. You'll probably just need to compile one time and then you can just execute multiple times with the same .class files by repeatedly typing the third command into the terminal.

