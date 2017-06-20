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

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class SendMoneyFromCardWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/sendMoneyFromCardWindow.fxml";

    private static final Logger log = Logger
            .getLogger(SendMoneyFromCardWindowController.class);

    private BSUser bsUser;
    private BSCard bsCard;

    private Connection connection;

    @FXML
    private Label valueLbl;
    @FXML
    private TextField moneySendAccountTF;
    @FXML
    private TextField sendToAccountGuidTF;
    @FXML
    private Label accountTransactionInfoLbl;
    @FXML
    private TextField moneySendCardTF;
    @FXML
    private TextField sendToCardTF;
    @FXML
    private Label cardTransactionInfoLbl;
    @FXML
    private Label bufferInfoLbl;
    @FXML
    private Label cardGuidLbl;

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
    private void updateCardInfoHprLnkClickHandle() {
        try {
            if (bsCard != null) {
                final BSCard account = BSCard.getBSCardObject(
                        this.bsCard.getGuid(), connection);
                final BigDecimal value = account.getValue();
                valueLbl.setText(value + " руб.");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void sendToAccountHprLnkClickHandle() {
        try {
            try {
                accountTransactionInfoLbl.setTextFill(Paint.valueOf("BLACK"));
                accountTransactionInfoLbl.setText("");
                final String sendTo = sendToAccountGuidTF.getText();
                if (sendTo != null && !sendTo.isEmpty()) {
                    final String sendFrom = bsCard.getGuid();
                    final String valueAsString = moneySendAccountTF.getText();
                    final BigDecimal value = new BigDecimal(valueAsString).setScale(2,
                            BigDecimal.ROUND_DOWN);
                    if (BSBankAccount.isExistAccount(sendTo, connection)) {
                        BSUser.sendMoney(sendFrom, BSCard.TYPE, sendTo, BSBankAccount.TYPE, value,
                                connection);
                        accountTransactionInfoLbl.setTextFill(Paint.valueOf("GREEN"));
                        accountTransactionInfoLbl.setText("Отправлено");
                    }
                    else {
                        accountTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                        accountTransactionInfoLbl.setText("Депозит не существует");
                    }
                    final BSCard bsCard = BSCard.getBSCardObject(
                            this.bsCard.getGuid(), connection);
                    final BigDecimal valueUpdates = bsCard.getValue();
                    valueLbl.setText(String.valueOf(valueUpdates) + " руб.");
                }
            } catch (NumberFormatException numberFormatEx) {
                accountTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                accountTransactionInfoLbl.setText("Ошибка");
            } catch (Exception e) {
                log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void sendToCardHprLnkClickHandle() {
        try {
            try {
                cardTransactionInfoLbl.setTextFill(Paint.valueOf("BLACK"));
                cardTransactionInfoLbl.setText("");
                final String sendTo = sendToCardTF.getText();
                if (sendTo != null && !sendTo.isEmpty()) {
                    final String sendFrom = bsCard.getGuid();
                    final String valueAsString = moneySendCardTF.getText();
                    final BigDecimal value = new BigDecimal(valueAsString).setScale(2,
                            BigDecimal.ROUND_DOWN);
                    if (BSCard.isExistCard(sendTo, connection)) {
                        BSUser.sendMoney(sendFrom, BSCard.TYPE, sendTo, BSCard.TYPE, value,
                                connection);
                        cardTransactionInfoLbl.setTextFill(Paint.valueOf("GREEN"));
                        cardTransactionInfoLbl.setText("Отправлено");
                    } else {
                        cardTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                        cardTransactionInfoLbl.setText("Карта не существует");
                    }
                    final BSCard bsCard = BSCard.getBSCardObject(
                            this.bsCard.getGuid(), connection);
                    final BigDecimal valueUpdates = bsCard.getValue();
                    valueLbl.setText(String.valueOf(valueUpdates) + " руб.");
                }
            } catch (NumberFormatException numberFormatEx) {
                cardTransactionInfoLbl.setTextFill(Paint.valueOf("RED"));
                cardTransactionInfoLbl.setText("Ошибка");
            } catch (Exception e) {
                log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void bankAccountGuidClickHandle() {
        try {
            final String accountGuid = bsCard.getGuid();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(accountGuid), null
            );
            bufferInfoLbl.setText("GUID скопирован");
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void loadCardValue() throws SQLException {
        final BigDecimal cardmoney = bsCard.getValue();
        valueLbl.setText(
                cardmoney + " руб."
        );
    }

    public void loadCardGuid() {
        final String cardGuid = bsCard.getGuid();
        cardGuidLbl.setText(cardGuid);
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
