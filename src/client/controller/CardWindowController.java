package client.controller;

import client.BSApp;
import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class CardWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/cardWindow.fxml";

    private static final Logger log = Logger
            .getLogger(CardWindowController.class);

    private Scene userInfoScene;
    private Scene sendMoneyScene;
    private Scene closeCardScene;

    private BSUser bsUser;
    private BSCard bsCard;

    private Connection connection;

    @FXML
    private Hyperlink cardHolderHprLnk;
    @FXML
    private Label cardGuidLbl;
    @FXML
    private Label valueLbl;
    @FXML
    private Label bufferInfoLbl;

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
    private void updateCardInfoHprLnkInfoHprLnkClickHandle() {
        try {
            if (bsCard != null) {
                final BSCard card = BSCard.getBSCardObject(
                        this.bsCard.getGuid(), connection);
                final BigDecimal value = card.getValue();
                valueLbl.setText(value + " руб.");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void beginTransactionHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(SendMoneyFromCardWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            sendMoneyScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(sendMoneyScene);
            stage.show();

            setControllerToSendMoneyFromCardWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToSendMoneyFromCardWindow(FXMLLoader fxmlLoader) {
        try {
            SendMoneyFromCardWindowController controller = fxmlLoader.getController();
            final BSApp application = getApplication();
            controller.setApplication(application);
            final Stage stage = getStage();
            controller.setStage(stage);
            controller.setScene(sendMoneyScene);
            final Scene scene = getScene();
            controller.setPreviewScene(scene);

            controller.setBsUser(bsUser);
            controller.initializeCloseWindowListenerSub();
            final BSSession bsSession = getBsSession();
            controller.setBsSession(bsSession);
            controller.setBsCard(bsCard);
            controller.setConnection(connection);

            controller.loadCardGuid();
            controller.loadCardValue();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void closeCardHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass().getResource(CloseCardWindowController
                    .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            closeCardScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(closeCardScene);
            stage.show();

            setControllerToCloseCardWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToCloseCardWindow (FXMLLoader fxmlLoader) {
        CloseCardWindowController controller = fxmlLoader.getController();
        controller.setApplication(getApplication());
        controller.setStage(getStage());
        controller.setScene(closeCardScene);
        controller.setPreviewScene(getScene());
        controller.setBsSession(getBsSession());
        controller.setBsUser(bsUser);
        controller.setBsCard(bsCard);
        controller.setConnection(connection);
        controller.initializeCloseWindowListenerSub();
    }

    @FXML
    private void cardHolderHprLnkClickHandle() {
        try {
            final Stage stage = getStage();
            if (userInfoScene != null) {
                stage.setScene(userInfoScene);
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(UserInfoWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                userInfoScene = new Scene(pane);
                stage.setScene(userInfoScene);
                stage.show();

                setControllerToUserInfoWindow(fxmlLoader);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToUserInfoWindow (FXMLLoader fxmlLoader) {
        try {
            UserInfoWindowController controller = fxmlLoader.getController();
            final BSApp application = getApplication();
            controller.setApplication(application);
            final Stage stage = getStage();
            controller.setStage(stage);
            controller.setScene(userInfoScene);
            final Scene scene = getScene();
            controller.setPreviewScene(scene);
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.initializeUserInfo();
            controller.initializeCloseWindowListener();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void cardGuidClickHandle() {
        try {
            final String cardGuid = bsCard.getGuid();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(cardGuid), null
            );
            bufferInfoLbl.setText("GUID скопирован");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(),
                connection);
    }

    public void loadCardHolder() {
        initializeCloseWindowListener();
        final String depositHolder = bsUser.getLastName() + " " + bsUser.getFirstName() + " "
                + bsUser.getSecondName();
        cardHolderHprLnk.setText(depositHolder);
    }

    public void loadCardGuid() {
        final String depositGuid = bsCard.getGuid();
        cardGuidLbl.setText(depositGuid);
    }

    public void loadCardValue() throws SQLException {
        final BigDecimal accountMoneyEncrypt = bsCard.getValue();
        valueLbl.setText(
                accountMoneyEncrypt + " руб."
        );
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setBsCard (BSCard bsCard) {
        this.bsCard = bsCard;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
