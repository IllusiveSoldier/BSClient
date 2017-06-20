package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exchange_rate.ExchangeRate;
import client.model.exchange_rate.ExchangeRateSuper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRatesWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/exchangeRatesWindow.fxml";

    private static final Logger log = Logger
            .getLogger(ExchangeRatesWindowController.class);

    private BSUser bsUser;
    private Connection connection;

    private ExchangeRateSuper exchangeRateSuper;
    private ObservableList<ExchangeRate> data;

    @FXML
    private TableView tableView;
    /**
     * Цифровой код валюты
     */
    private TableColumn<String, ExchangeRate> numCodeColumn;
    /**
     * Символьный код валюты
     */
    private TableColumn<String, ExchangeRate> charCodeColumn;
    /**
     * Номинал валюты
     */
    private TableColumn<String, ExchangeRate> nominalColumn;
    /**
     * Наименование валюты
     */
    private TableColumn<String, ExchangeRate> nameColumn;
    /**
     * Стоимость в рублях в соответствии с номиналом
     */
    private TableColumn<String, ExchangeRate> valueColumn;
    /**
     * Дата курса валют и фондовый рынок
     */
    @FXML
    private Label dateAndMarketNameLbl;

    /**
     * Обработчик нажатия на кнопку "Назад"
     */
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

    /**
     * Метод, который заполняет поле для даты и фондового рынка
     */
    public void loadDateAndMarketName() {
        try {
            initializeCloseWindowListener();
            if (exchangeRateSuper != null) {
                String date = exchangeRateSuper.getDate();
                String marketName = exchangeRateSuper.getMarketName();
                dateAndMarketNameLbl.setText(marketName + ", " + date);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который наполняет данными таблицу
     */
    public void putDataToObservableList() {
        try {
            numCodeColumn = new TableColumn<String, ExchangeRate>("Цифр. код");
            charCodeColumn = new TableColumn<String, ExchangeRate>("Симв. код");
            nominalColumn = new TableColumn<String, ExchangeRate>("Номинал");
            nameColumn = new TableColumn<String, ExchangeRate>("Наим.");
            valueColumn = new TableColumn<String, ExchangeRate>("Стоим. в руб.");

            numCodeColumn.setCellValueFactory(new PropertyValueFactory<>("numCode"));
            charCodeColumn.setCellValueFactory(new PropertyValueFactory<>("charCode"));
            nominalColumn.setCellValueFactory(new PropertyValueFactory<>("nominal"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

            HashMap<String, ExchangeRate> rates = exchangeRateSuper.getExchangeRates();

            data = FXCollections.observableArrayList(
                    getRatesAsList(rates)
            );
            tableView.setItems(data);
            tableView.getColumns().addAll(
                    numCodeColumn,
                    charCodeColumn,
                    nominalColumn,
                    nameColumn,
                    valueColumn
            );
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private ArrayList<ExchangeRate> getRatesAsList(HashMap<String, ExchangeRate> rates) {
        ArrayList<ExchangeRate> ratesAsList = new ArrayList<ExchangeRate>();
        if (rates != null && rates.size() > 0) {
            for (Map.Entry<String, ExchangeRate> entry : rates.entrySet()) {
                ratesAsList.add(entry.getValue());
            }
        }

        return ratesAsList;
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public void setExchangeRateSuper (ExchangeRateSuper exchangeRateSuper) {
        this.exchangeRateSuper = exchangeRateSuper;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
