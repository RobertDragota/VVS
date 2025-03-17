package tasks.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.controller.Controller;
import tasks.controller.Notificator;
import tasks.model.ArrayTaskList;
import tasks.services.TaskIO;
import tasks.services.TasksService;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;
    private static final int defaultWidth = 820;
    private static final int defaultHeight = 520;
    private static final Logger log = Logger.getLogger(Main.class.getName());

    private final ArrayTaskList savedTasksList = new ArrayTaskList();

    private static File savedTasksFile = new File("data/tasks.txt");

    private TasksService service = new TasksService(savedTasksList);//savedTasksList);

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Main.primaryStage = primaryStage;
    }

    public static File getSavedTasksFile() {
        return savedTasksFile;
    }

    public static void setSavedTasksFile(File savedTasksFile) {
        Main.savedTasksFile = savedTasksFile;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("saved data reading");
        if (savedTasksFile.length() != 0) {
            TaskIO.readBinary(savedTasksList, savedTasksFile);
        }
        try {
            log.info("application start");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Controller ctrl= loader.getController();
            service = new TasksService(savedTasksList);

            ctrl.setService(service);
            primaryStage.setTitle("Task Manager");
            primaryStage.setScene(new Scene(root, defaultWidth, defaultHeight));
            primaryStage.setMinWidth(defaultWidth);
            primaryStage.setMinHeight(defaultHeight);
            primaryStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            log.error("error reading main.fxml");
        }
        primaryStage.setOnCloseRequest(we -> {
                System.exit(0);
            });
        new Notificator(FXCollections.observableArrayList(service.getObservableList())).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
