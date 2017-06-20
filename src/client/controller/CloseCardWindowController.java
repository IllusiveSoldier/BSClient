package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class CloseCardWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/closeCardWindow.fxml";

    private static final Logger log = Logger
            .getLogger(CloseCardWindowController.class);

    private Scene successWindowScene;

    private BSUser bsUser;
    private BSCard bsCard;

    private Connection connection;

    @FXML
    private TextField bsCardGuidTF;
    @FXML
    private Label isSendingLbl;

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
            isSendingLbl.setText("");
            isSendingLbl.setTextFill(Paint.valueOf("BLACK"));
            final String targetCardGuid = bsCardGuidTF.getText();
            if (targetCardGuid != null && !targetCardGuid.isEmpty()) {
                final String from = bsCard.getGuid();
                final BigDecimal cash = bsCard.getValue();
                if (BSCard.isExistCard(targetCardGuid, connection)) {
                    final boolean isSend = BSUser.sendMoney(from, BSCard.TYPE, targetCardGuid,
                            BSCard.TYPE, cash, connection);
                    if (!isSend) {
                        final boolean isCloseCard = BSCard.closeCard(bsCard.getGuid(), connection);
                        if (!isCloseCard)
                            loadSuccessClosingDepositWindow();
                    } else {
                        setStatusSendLbl(true);
                    }
                } else {
                    isSendingLbl.setTextFill(Paint.valueOf("RED"));
                    isSendingLbl.setText("Карта не существует");
                }
            } else {
                isSendingLbl.setTextFill(Paint.valueOf("RED"));
                isSendingLbl.setText("Заполните GUID!");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void loadSuccessClosingDepositWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(SuccessClosingCardWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            successWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(successWindowScene);
            stage.show();

            setControllerToSuccessClosingWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToSuccessClosingWindow(FXMLLoader fxmlLoader) {
        SuccessClosingCardWindowController controller = fxmlLoader.getController();
        controller.setApplication(getApplication());
        controller.setStage(getStage());
        controller.setScene(successWindowScene);
        controller.setPreviewScene(getScene());
        controller.setBsUser(bsUser);
        controller.setBsSession(getBsSession());
        controller.setConnection(connection);
        controller.loadDateAndStatusOfTransaction(new Date(), false);
    }

    private void setStatusSendLbl(boolean isSend) {
        if (isSend) {
            isSendingLbl.setTextFill(Paint.valueOf("GREEN"));
            isSendingLbl.setText("Отправлено");
        } else {
            isSendingLbl.setTextFill(Paint.valueOf("RED"));
            isSendingLbl.setText("Ошибка");
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

    public void setBsCard (BSCard bsCard) {
        this.bsCard = bsCard;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
