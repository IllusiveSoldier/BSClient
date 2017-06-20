package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSBankAccount;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;
import system.core.database.BSDb;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class SendMoneyFromAccountWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/sendMoneyFromAccountWindow.fxml";

    private static final Logger log = Logger
            .getLogger(SendMoneyFromAccountWindowController.class);

    private BSUser bsUser;
    private BSBankAccount bsBankAccount;

    private Connection connection;

    @FXML
    private Label valueLbl;
    @FXML
    private TextField sendToAccountGuidTF;
    @FXML
    private TextField sendToCardTF;
    @FXML
    private Label bankAccountGuid;
    @FXML
    private Label bufferInfoLbl;
    @FXML
    private Label accountTransactionInfoLbl;
    @FXML
    private TextField moneySendAccountTF;
    @FXML
    private Label cardTransactionInfoLbl;
    @FXML
    private TextField moneySendCardTF;

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
    private void sendToAccountHprLnkClickHandle() {
        try {
            accountTransactionInfoLbl.setTextFill(Paint.valueOf("BLACK"));
            accountTransactionInfoLbl.setText("");
            final String sendTo = sendToAccountGuidTF.getText();
            if (sendTo != null && !sendTo.isEmpty()) {
                final String sendFrom = bsBankAccount.getGuid();
                final String valueAsString = moneySendAccountTF.getText();
                final BigDecimal value = new BigDecimal(valueAsString).setScale(2,
                        BigDecimal.ROUND_DOWN);
                if (BSBankAccount.isExistAccount(sendTo, connection)) {
                    BSUser.sendMoney(sendFrom, BSBankAccount.TYPE, sendTo, BSBankAccount.TYPE, value,
                            connection);
                    accountTransactionInfoLbl.setTextFill(Paint.valueOf("GREEN"));
                    accountTransactionInfoLbl.setText("Отправлено");
                }
                else {
                    accountTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                    accountTransactionInfoLbl.setText("Депозит не существует");
                }
                final BSBankAccount bsBankAccount = BSBankAccount.getBSBankAccountObject(
                        this.bsBankAccount.getGuid(), connection);
                final byte[] encryptedValueAsByteArray = bsBankAccount.getValue();
                final String decryptedValueAsString = BSDb.decryptBytesBySK(
                        encryptedValueAsByteArray, connection);
                valueLbl.setText(decryptedValueAsString + " руб.");
            }
        } catch (NumberFormatException numberFormatEx) {
            accountTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
            accountTransactionInfoLbl.setText("Ошибка");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void sendToCardHprLnkClickHandle() {
        try {
            cardTransactionInfoLbl.setTextFill(Paint.valueOf("black"));
            cardTransactionInfoLbl.setText("");
            final String sendTo = sendToCardTF.getText();
            if (sendTo != null && !sendTo.isEmpty()) {
                final String sendFrom = bsBankAccount.getGuid();
                final String valueAsString = moneySendCardTF.getText();
                final BigDecimal value = new BigDecimal(valueAsString).setScale(2,
                        BigDecimal.ROUND_DOWN);
                if (BSCard.isExistCard(sendTo, connection)) {
                    BSUser.sendMoney(sendFrom, BSBankAccount.TYPE, sendTo, BSCard.TYPE, value,
                            connection);
                    cardTransactionInfoLbl.setTextFill(Paint.valueOf("GREEN"));
                    cardTransactionInfoLbl.setText("Отправлено");
                }
                else {
                    cardTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                    cardTransactionInfoLbl.setText("Карта не существует");
                }
                final BSBankAccount bsBankAccount = BSBankAccount.getBSBankAccountObject(
                        this.bsBankAccount.getGuid(), connection);
                final byte[] encryptedValueAsByteArray = bsBankAccount.getValue();
                final String decryptedValueAsString = BSDb.decryptBytesBySK(
                        encryptedValueAsByteArray, connection);
                valueLbl.setText(decryptedValueAsString + " руб.");
            }
        } catch (NumberFormatException numberFormatEx) {
            cardTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
            cardTransactionInfoLbl.setText("Ошибка");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
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
    private void updateAccountInfoHprLnkClickHandle() {
        try {
            if (bsBankAccount != null) {
                final BSBankAccount account = BSBankAccount.getBSBankAccountObject(
                        this.bsBankAccount.getGuid(), connection);
                final byte[] encryptedValueAsBytesArray = account.getValue();
                final String decryptedValueAsString = BSDb.decryptBytesBySK(
                        encryptedValueAsBytesArray, connection);
                valueLbl.setText(decryptedValueAsString + " руб.");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void loadAccountValue() throws SQLException {
        final byte[] accountMoneyEncrypt = bsBankAccount.getValue();
        final String decryptMoney = BSDb.decryptBytesBySK(accountMoneyEncrypt, connection);
        valueLbl.setText(
                decryptMoney + " руб."
        );
    }

    public void loadAccountGuid() {
        final String accountGuid = bsBankAccount.getGuid();
        bankAccountGuid.setText(accountGuid);
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
