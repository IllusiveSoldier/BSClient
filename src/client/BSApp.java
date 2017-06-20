package client;

import client.controller.MainWindowController;
import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;


public class BSApp extends Application {
    public static final String BS_SYSTEM_APPLICATION_NAME = "® BS Client";

    private static final Logger log = Logger
            .getLogger(BSApp.class);

    private Stage stage;
    private Scene scene;

    @Override
    public void start (Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.stage.setResizable(false);
        this.stage.sizeToScene();
        this.stage.setTitle(BS_SYSTEM_APPLICATION_NAME);

        initRootLayout();
    }

    /**
     * Метод, который загружает файл с разметкой и отображает главное окно приложения
     */
    private void initRootLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(MainWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            scene = new Scene(pane);
            stage.setScene(scene);
            stage.show();

            setControllerForMainWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который передаёт в контроллер Application, Stage, Scene
     * @param loader
     */
    private void setControllerForMainWindow(FXMLLoader loader) {
        try {
            MainWindowController controller = loader.getController();
            controller.initializeConnection();
            controller.setApplication(this);
            controller.setStage(stage);
            controller.setScene(scene);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }
}
