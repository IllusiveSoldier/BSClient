package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AcceptCreateBankAccountWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/acceptCreatebankAccountWindow.fxml";

    private static final Logger log = Logger
            .getLogger(AcceptCreateBankAccountWindowController.class);

    private Scene acceptCreateBankAccountWindowStepTwoScene;

    private BSUser bsUser;
    private BSBankAccountType bsBankAccountType;
    private BSCard bsCard;
    private Date endDate;
    private Connection connection;

    @FXML
    private Hyperlink acceptHprLnk;
    @FXML
    private Label accountNameLbl;
    @FXML
    private Label endDateLbl;
    @FXML
    private Label docDateLbl;
    @FXML
    private Label docNumLbl;
    @FXML
    private Label userInfoLbl;
    @FXML
    private Label currencyInfoLbl;
    @FXML
    private Label percentLbl;
    @FXML
    private Label capitalizationInfoLbl;
    @FXML
    private Label messageTF;

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
            setDefaultViewOfMessageLabel();
            final int accountType = BSBankAccountType
                    .getTypeAccountByGuid(bsBankAccountType.getGuid(), connection);
            final String accountGuid = UUID.randomUUID().toString().toUpperCase();
            if (bsCard != null) {
                BSBankAccount.createBankAccount(
                        accountGuid,
                        bsUser.getGuid(),
                        endDate.getTime(),
                        new BigDecimal(500.00d),
                        accountType,
                        bsCard.getGuid(),
                        BSCard.TYPE,
                        connection
                );
            } else {
                BSBankAccount.createBankAccount(
                        accountGuid,
                        bsUser.getGuid(),
                        endDate.getTime(),
                        new BigDecimal(500.00d),
                        accountType,
                        accountGuid,
                        BSBankAccount.TYPE,
                        connection
                );
            }
            final boolean isExistAccount = BSBankAccount.isExistAccount(accountGuid, connection);
            if (isExistAccount) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(AcceptCreateBankAccountWindowControllerStepTwo
                                .PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                acceptCreateBankAccountWindowStepTwoScene = new Scene(pane);
                final Stage stage = getStage();
                stage.setScene(acceptCreateBankAccountWindowStepTwoScene);
                stage.show();

                setControllerToAcceptCreateBankAccountWindowStepTwo(fxmlLoader, accountGuid);
            } else {
                messageTF.setTextFill(Paint.valueOf("RED"));
                messageTF.setText("В момент создания депозита произошла ошибка. Попробуйте позже.");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToAcceptCreateBankAccountWindowStepTwo(FXMLLoader fxmlLoader,
                                                                     final String accountGuid) {
        try {
            AcceptCreateBankAccountWindowControllerStepTwo controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(acceptCreateBankAccountWindowStepTwoScene);
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.showCreatedBankAccountGuid(accountGuid);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который возвращает текстовое поле для вывода сообщений в состояние по умполчанию
     * (меняет цвет текста на чёрный и очищает поле от текста)
     */
    private void setDefaultViewOfMessageLabel() {
        try {
            messageTF.setText("");
            messageTF.setTextFill(Paint.valueOf("BLACK"));
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void setAcceptData(final String accountName, final String docDate,
                              final String docNum, final String userInfo, final String currencyInfo,
                              final String percent, final String capitalizationInfo)
    {
        initializeCloseWindowListener();
        accountNameLbl.setText(accountName);
        endDateLbl.setText(new SimpleDateFormat("dd.MM.yyyy").format(endDate));
        docDateLbl.setText(docDate);
        docNumLbl.setText(docNum);
        userInfoLbl.setText(userInfo);
        currencyInfoLbl.setText(currencyInfo);
        percentLbl.setText(percent);
        capitalizationInfoLbl.setText(capitalizationInfo);
    }

    public void setBsBankAccountType (BSBankAccountType bsBankAccountType) {
        this.bsBankAccountType = bsBankAccountType;
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public void setBsCard (BSCard bsCard) {
        this.bsCard = bsCard;
    }

    public void setEndDate (Date endDate) {
        this.endDate = endDate;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
