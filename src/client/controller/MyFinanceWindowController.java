package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;
import system.core.FinanceInfo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class MyFinanceWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/myFinanceWindow.fxml";

    private static final Logger log = Logger
            .getLogger(MyFinanceWindowController.class);

    private BSUser bsUser;
    private Connection connection;

    @FXML
    private PieChart infoAboutFinancePieChart;
    @FXML
    private Label depositCashLbl;
    @FXML
    private Label cardCashLbl;

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

    public void loadPieChart() throws SQLException {
        final BigDecimal allCashInDeposits = FinanceInfo.getAllMoneyInDeposits(bsUser.getGuid(),
                connection);
        final PieChart.Data depositsData = new PieChart.Data("Депозиты",
                allCashInDeposits.doubleValue());
        final BigDecimal allCashInCards = FinanceInfo.getAllMoneyInCards(bsUser.getGuid(),
                connection);
        final PieChart.Data cardsData = new PieChart.Data("Карты",
                allCashInCards.doubleValue());
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                depositsData,
                cardsData
        );
        infoAboutFinancePieChart.setData(data);
        loadCashInDepositsAndCards(allCashInDeposits, allCashInCards);
    }

    private void loadCashInDepositsAndCards(final BigDecimal inDeposits, final BigDecimal inCards) {
        depositCashLbl.setText(String.valueOf(inDeposits) + " руб.");
        cardCashLbl.setText(String.valueOf(inCards) + " руб.");
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
