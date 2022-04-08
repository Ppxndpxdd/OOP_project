import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TextEditor extends Application {
    //Main pane
    private BorderPane borderPane = new BorderPane();

    //Top of pane
    private MenuBar menuBar = new MenuBar();
    private Menu text = new Menu("File");
    private MenuItem openMn = new MenuItem("open");
    private MenuItem saveMn = new MenuItem("save");
    private MenuItem closeMn = new MenuItem("close");

    //Center of pane
    private AnchorPane anchorPane = new AnchorPane();
    private TextArea textArea = new TextArea("Load a text file using the menu");
    
    //Bottom of pane
    private HBox hBox = new HBox();
    private Label statusMessage = new Label();
    private ProgressBar progressBar = new ProgressBar();
    
    public TextEditor(){
        this.setupUI();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(borderPane, 1080, 720);
        primaryStage.setTitle("OOP : textEditor"); // Set title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }

    private void setupUI() {
        //Top
        //borderPane Setup
        borderPane.setTop(menuBar);
        openMn.setGraphic(new ImageView(".png"));
        saveMn.setGraphic(new ImageView(".png"));
        closeMn.setGraphic(new ImageView(".png"));

        text.getItems().addAll(openMn,saveMn,closeMn);
        menuBar.getMenus().add(text);

        //Center
        anchorPane.getChildren().addAll(textArea);
        textArea.setEditable(false);
        textArea.setPrefHeight(400.00);
        textArea.prefWidth(600.00);
        

        
    }

    private void chooseFileToLoad() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File fileToLoad = fileChooser.showOpenDialog(null);
        if (fileToLoad != null) {
            loadFileToTextArea(fileToLoad);
        }
    }

    private Task<String> fileLoaderTask(File fileToLoad){
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }
                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }
                return totalFile.toString();
            }
        };
        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
                statusMessage.setText("File loaded: " + fileToLoad.getName());
                loadedFileReference = fileToLoad;
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
                textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });
        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            statusMessage.setText("Failed to load file");
        });
        return loadFileTask;
    }

    private void loadFileToTextArea(File fileToLoad) {

    }

    private void saveMethod() {
    }

    private void newMethod() {
    }

}
