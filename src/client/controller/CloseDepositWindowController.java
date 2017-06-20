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
import system.core.BSBankAccount;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class CloseDepositWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/closeDepositWindow.fxml";

    private static final Logger log = Logger
            .getLogger(CloseDepositWindowController.class);

    private Scene successWindowScene;

    private BSUser bsUser;
    private BSBankAccount bsBankAccount;

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
                final String from = bsBankAccount.getGuid();
                final byte[] cash = bsBankAccount.getValue();
                if (BSCard.isExistCard(targetCardGuid, connection)) {
                    final boolean isSend = BSUser.sendMoney(from, BSBankAccount.TYPE, targetCardGuid,
                            BSCard.TYPE, cash, connection);
                    if (!isSend) {
                        final boolean isCloseDeposit = BSBankAccount.closeDeposit(bsBankAccount
                                .getGuid(), connection);
                        if (!isCloseDeposit)
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
                    .getResource(SuccessClosingDepositWindowController.PATH_TO_FXML_FILE));
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
        SuccessClosingDepositWindowController controller = fxmlLoader.getController();
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

    public void setBsBankAccount (BSBankAccount bsBankAccount) {
        this.bsBankAccount = bsBankAccount;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
