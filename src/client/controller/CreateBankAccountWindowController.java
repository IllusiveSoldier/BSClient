package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import client.model.exchange_rate.ExchangeRate;
import client.model.exchange_rate.ExchangeRateSuper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import system.core.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateBankAccountWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/createBankAccountWindow.fxml";

    private static final Logger log = Logger
            .getLogger(CreateBankAccountWindowController.class);

    private Scene acceptBankAccountCreateWindowScene;

    private BSUser bsUser;

    private Connection connection;

    private ArrayList<BSBankAccountType> types = null;
    private ArrayList<BSCard> bsCards = null;

    private int currentAccountType = 0;
    private int currentCardId = 0;

    @FXML
    private ChoiceBox<String> accountTypeSelector;
    @FXML
    private DatePicker endDateDP;
    @FXML
    private Label docDateLbl;
    @FXML
    private Label docNumLbl;
    @FXML
    private Label userInfoLbl;
    @FXML
    private Label currencyTypeLbl;
    @FXML
    private Label percentLbl;
    @FXML
    private ChoiceBox<String> capitalizationSelector;
    @FXML
    private Label marketName;
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
    private void completeCreateDepositHprLnkClickHandle() {
        try {
            setDefaultViewOfMessageLabel();
            if (accountTypeSelector.getValue() == null || endDateDP.getValue() == null
                    || percentLbl.getText() == null || capitalizationSelector.getValue() == null)
            {
                throw new BSNotFilledData("Не заполнены обязательные поля!");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(AcceptCreateBankAccountWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                acceptBankAccountCreateWindowScene = new Scene(pane);
                final Stage stage = getStage();
                stage.setScene(acceptBankAccountCreateWindowScene);
                stage.show();

                setControllerToAcceptCreateBankAccountWindowController(fxmlLoader);
            }
        } catch (BSNotFilledData notFilledData) {
            messageTF.setTextFill(Paint.valueOf("RED"));
            messageTF.setText(notFilledData.getMessage());
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToAcceptCreateBankAccountWindowController(FXMLLoader fxmlLoader) {
        try {
            AcceptCreateBankAccountWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(acceptBankAccountCreateWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.setBsBankAccountType(types.get(currentAccountType));
            if (bsCards != null && bsCards.size() > 0) {
                if (currentCardId > 0)
                    controller.setBsCard(bsCards.get(currentCardId - 1));
                else controller.setBsCard(null);
            } else controller.setBsCard(null);
            controller.setEndDate(Date.from(Instant.from(endDateDP.getValue().atStartOfDay(ZoneId
                    .systemDefault()))));
            controller.setAcceptData(
                    accountTypeSelector.getValue(),
                    docDateLbl.getText(),
                    docNumLbl.getText(),
                    userInfoLbl.getText(),
                    currencyTypeLbl.getText(),
                    percentLbl.getText(),
                    capitalizationSelector.getValue()
            );
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

    public void loadUserInfo() {
        initializeCloseWindowListener();
        endDateDP.setDisable(true);
        endDateDP.setStyle("-fx-opacity: 1");
        endDateDP.getEditor().setStyle("-fx-opacity: 1");
        final String userInfo = bsUser.getLastName().toUpperCase() + " "
                + bsUser.getFirstName().toUpperCase().charAt(0) + ". "
                + bsUser.getSecondName().toUpperCase().charAt(0)
                + "., " + bsUser.getGuid() + " (Глоб. идентификатор)";
        userInfoLbl.setText(userInfo);
    }

    public void loadDocDateAndDocNum() throws SQLException {
        final int accountCount = BSBankAccount.getBankAccountCount(connection) + 1;
        docNumLbl.setText(String.valueOf(accountCount));
        final String docDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        docDateLbl.setText(docDate);
    }

    public void loadCurrencyAndMarketName() throws SAXException, ParserConfigurationException,
            IOException
    {
        ExchangeRateSuper exchangeRateSuper = new ExchangeRateSuper().getExchangeSuper();
        ExchangeRate rate = exchangeRateSuper.getExchangeRateById("R01235");
        final String currencyInfo = "Рубль - RUB(643) - Актуальный курс к доллару: "
                + rate.getValue() + " - " + rate.getCharCode() + "(" + rate.getNumCode() + ")";
        currencyTypeLbl.setText(currencyInfo);
        final String marketName = "Информация представлена " + exchangeRateSuper.getMarketName();
        this.marketName.setText(marketName);
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public void loadCapitalizationSelector() throws SQLException {
        bsCards = BSCard.getBsCardsPerUsser(bsUser.getGuid(), connection);
        ArrayList<String> viewInfo = null;
        if (bsCards != null && bsCards.size() > 0) {
            viewInfo = new ArrayList<String>(5);
            viewInfo.add("На текущий депозит");
            for (BSCard card : bsCards) {
                final String info = card.getGuid();
                viewInfo.add(info);
            }
            capitalizationSelector.setItems(FXCollections.observableArrayList(
                    viewInfo
            ));
            capitalizationSelector.getSelectionModel().selectedIndexProperty().addListener(
                    (observable, oldValue, newValue) -> {
                currentCardId = newValue.intValue();
            });
        } else {
            capitalizationSelector.setItems(FXCollections.observableArrayList(
                    "На текущий депозит"
            ));
        }
    }

    public void loadBankAccountTypesSelector() throws SQLException {
        types = BSBankAccountType.getBankAccountTypes(connection);
        ArrayList<String> viewInfo = null;
        if (types != null && types.size() > 0) {
            viewInfo = new ArrayList<String>(5);
            for (BSBankAccountType type : types) {
                final String info = "\"" + type.getName() + "\"" + "\t|\t"
                        + type.getPercent().setScale(2, BigDecimal.ROUND_DOWN) + "% (ежемес.)"
                        + "\t|\t" + type.getDuration() + "мес. (" + type.getDuration() / 12 + "лет)";
                viewInfo.add(info);
            }
            accountTypeSelector.setItems(FXCollections.observableArrayList(
                    viewInfo
            ));
            accountTypeSelector.getSelectionModel().selectedIndexProperty().addListener(
                    (observable, oldValue, newValue) -> {
                currentAccountType = newValue.intValue();
                final BigDecimal percent = types.get(newValue.intValue()).getPercent();
                percentLbl.setText(
                        String.valueOf(percent.setScale(2, BigDecimal.ROUND_DOWN)) + "%"
                );
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                final int duration = types.get(newValue.intValue()).getDuration();
                calendar.add(Calendar.MONTH, duration);
                endDateDP.setValue(
                        LocalDate.of(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        )
                );
            });
        }
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
