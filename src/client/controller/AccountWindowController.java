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
import system.core.BSBankAccount;
import system.core.BSSession;
import system.core.BSUser;
import system.core.database.BSDb;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/accountWindow.fxml";

    private static final Logger log = Logger
            .getLogger(AccountWindowController.class);

    private Scene userInfoScene;
    private Scene sendMoneyScene;
    private Scene closeDepositScene;

    private BSUser bsUser;
    private BSBankAccount bsBankAccount;

    private Connection connection;

    @FXML
    private Hyperlink depositHolderHprLnk;
    @FXML
    private Label depositBeginDateLbl;
    @FXML
    private Label depositEndDateLbl;
    @FXML
    private Label bankAccountGuid;
    @FXML
    private Label bufferInfoLbl;
    @FXML
    private Label valueLbl;

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
    private void beginTransactionHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(SendMoneyFromAccountWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            sendMoneyScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(sendMoneyScene);
            stage.show();

            setControllerToSendMoneyFromAccountWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToSendMoneyFromAccountWindow(FXMLLoader fxmlLoader) {
        try {
            SendMoneyFromAccountWindowController controller = fxmlLoader.getController();
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
            controller.setBsBankAccount(bsBankAccount);
            controller.setConnection(connection);

            controller.loadAccountGuid();
            controller.loadAccountValue();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void closeDepositHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(CloseDepositWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            closeDepositScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(closeDepositScene);
            stage.show();

            setControllerToCloseDepositWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToCloseDepositWindow(FXMLLoader fxmlLoader) {
        CloseDepositWindowController controller = fxmlLoader.getController();
        controller.setApplication(getApplication());
        controller.setStage(getStage());
        controller.setScene(closeDepositScene);
        controller.setPreviewScene(getScene());
        controller.setBsSession(getBsSession());
        controller.setBsUser(bsUser);
        controller.setBsBankAccount(bsBankAccount);
        controller.setConnection(connection);
        controller.initializeCloseWindowListenerSub();
    }

    @FXML
    private void bankAccountGuidClickHandle() {
        try {
            final String accountGuid = bsBankAccount.getGuid();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(accountGuid), null
            );
            bufferInfoLbl.setText("GUID скопирован");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void depositHolderHprLnkClickHandle() {
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

    @FXML
    private void updateAccountInfoHprLnkClickHandle() {
        try {
            if (bsBankAccount != null) {
                final BSBankAccount account = BSBankAccount.getBSBankAccountObject(
                        this.bsBankAccount.getGuid(), connection);
                final byte[] encryptedValueAsBytesArray = account.getValue();
                final String decryptedValueAsString = BSDb.decryptBytesBySK(
                        encryptedValueAsBytesArray, connection);
                final BigDecimal toOutputValue = new BigDecimal(decryptedValueAsString)
                        .setScale(2, BigDecimal.ROUND_DOWN);
                valueLbl.setText(toOutputValue + " руб.");
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

    public void loadDepositHolder() {
        initializeCloseWindowListener();
        final String depositHolder = bsUser.getLastName() + " " + bsUser.getFirstName() + " "
                + bsUser.getSecondName();
        depositHolderHprLnk.setText(depositHolder);
    }

    public void loadDepositBeginAndEndDates() {
        final Date depositBeginDate = new Date(bsBankAccount.getBeginDate());
        final Date depositEndDate = new Date(bsBankAccount.getEndDate());

        final SimpleDateFormat ddMMyyyyhhmmDateFormat =
                new SimpleDateFormat("dd.MM.yyyy hh:mm");
        final String depositBeginDateAsString = ddMMyyyyhhmmDateFormat.format(depositBeginDate);
        final String depositEndDateAsString = ddMMyyyyhhmmDateFormat.format(depositEndDate);

        depositBeginDateLbl.setText(depositBeginDateAsString);
        depositEndDateLbl.setText(depositEndDateAsString);
    }

    public void loadDepositGuid() {
        final String depositGuid = bsBankAccount.getGuid();
        bankAccountGuid.setText(depositGuid);
    }

    public void loadAccountValue() throws SQLException {
        final byte[] accountMoneyEncrypt = bsBankAccount.getValue();
        final String decryptMoney = BSDb.decryptBytesBySK(accountMoneyEncrypt, connection);
        final BigDecimal toOutputValue = new BigDecimal(decryptMoney)
                .setScale(2, BigDecimal.ROUND_DOWN);
        valueLbl.setText(
                toOutputValue + " руб."
        );
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

    public void setBsBankAccount (BSBankAccount bsBankAccount) throws SQLException {
        this.bsBankAccount = bsBankAccount;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
