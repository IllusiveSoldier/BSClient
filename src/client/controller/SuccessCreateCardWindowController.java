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
import java.util.Date;

public class SuccessCreateCardWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/successCreateCardWindow.fxml";

    private static final Logger log = Logger
            .getLogger(SuccessCreateCardWindowController.class);

    private Scene userProfileScene;

    private BSUser bsUser;
    private BSCard bsCard;
    private Connection connection;

    @FXML
    private Label cardGuidLbl;

    @FXML
    private void goToBankAccountWindowHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(UserProfileWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            userProfileScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(userProfileScene);
            stage.show();

            setControllerForUserProfileWindowController(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForUserProfileWindowController(FXMLLoader fxmlLoader) {
        try {
            UserProfileWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(userProfileScene);

            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());

            controller.setSessionIdText();
            controller.setUserHprLnkText();

            controller.setConnection(connection);
            controller.initializeCloseWindowListener();
            controller.loadExchangeRate();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void setCardGuid() {
        initializeCloseWindowListener();
        final String cardGuid = bsCard.getGuid();
        if (cardGuid != null && !cardGuid.isEmpty())
            cardGuidLbl.setText(cardGuid);
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(),
                connection);
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }

    public void setBsCard (BSCard bsCard) {
        this.bsCard = bsCard;
    }
}
