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

public class DeleteUserWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/deleteUserWindow.fxml";

    private static final Logger log = Logger
            .getLogger(DeleteUserWindowController.class);

    private Scene successDeleteUserWindowScene;

    private BSUser bsUser;
    private Connection connection;

    @FXML
    private void backHprLnkClickHandle() {
        try {
            final Stage stage = getStage();
            final Scene previewScene = getPreviewScene();
            stage.setScene(previewScene);
            stage.show();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void acceptHprLnkClickHandle() {
        try {
            final String userGuid = bsUser.getGuid();
            if (userGuid != null && !userGuid.isEmpty()) {
                BSUser.deleteUser(userGuid, connection);
                loadSuccessDeleteUserWindow();
            } else throw new Exception("userGuid is null");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void loadSuccessDeleteUserWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(SuccessDeleteUserWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            successDeleteUserWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(successDeleteUserWindowScene);
            stage.show();

            setControllerToSuccessDeleteWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToSuccessDeleteWindow(FXMLLoader fxmlLoader) {
        try {
            SuccessDeleteUserWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(successDeleteUserWindowScene);
            controller.setPreviewScene(getScene());
            controller.initializeCloseWindowListenerSub();
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
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
