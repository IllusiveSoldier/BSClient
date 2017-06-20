package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class SuccessDeleteUserWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/successDeleteUserWindow.fxml";

    private static final Logger log = Logger
            .getLogger(SuccessDeleteUserWindowController.class);

    private Scene mainWindowScene;

    private BSUser bsUser;
    private Connection connection;

    @FXML
    private void goToSignInHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(MainWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            mainWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(mainWindowScene);
            stage.show();

            setControllerForMainWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForMainWindow(FXMLLoader loader) {
        try {
            MainWindowController controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(mainWindowScene);
            controller.setConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeCloseWindowListenerSub () {
        initializeCloseWindowListener();
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
