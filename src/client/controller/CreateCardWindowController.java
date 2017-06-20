package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CreateCardWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/createCardWindow.fxml";

    private static final Logger log = Logger
            .getLogger(CreateCardWindowController.class);

    private Scene successCreateCardScene;

    private BSUser bsUser;
    private BSCard bsCard;
    private Connection connection;

    @FXML
    private Label cardGuidLbl;
    @FXML
    private Label cardDateLbl;

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
    private void createCardHprLnkClickHandle() {
        try {
            final String cardGuid = bsCard.getGuid();
            final String userGuid = bsUser.getGuid();
            if (cardGuid != null && !cardGuid.isEmpty() && userGuid != null && !userGuid
                    .isEmpty()) {
                BSCard.createCard(cardGuid, userGuid, connection);
                loadSuccessCreateCardWindow();
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void loadSuccessCreateCardWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(SuccessCreateCardWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            successCreateCardScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(successCreateCardScene);
            stage.show();

            setControllerToSuccessCreateCardWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToSuccessCreateCardWindow(FXMLLoader fxmlLoader) {
        try {
            SuccessCreateCardWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(successCreateCardScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsCard(bsCard);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.setCardGuid();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void initializeBSCard() {
        initializeCloseWindowListener();
        bsCard = new BSCard();
        final String cardGuid = UUID.randomUUID().toString();
        bsCard.setGuid(cardGuid);
        final String userGuid = bsUser.getGuid();
        bsCard.setUserGuid(userGuid);
        cardGuidLbl.setText(cardGuid);
        final String cardDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
                .format(new Date());
        cardDateLbl.setText(cardDate);
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
