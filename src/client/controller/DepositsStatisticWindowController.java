package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.*;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DepositsStatisticWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/depositsStatisticWindow.fxml";

    private static final Logger log = Logger
            .getLogger(DepositsStatisticWindowController.class);

    private static final int MAX_PAGE_COUNT = 3;
    private static final int CURRENT_PAGE_ITEM = 1;
    private static final int PAGE_COUNT = 3;

    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private double screenWidth = screenSize.getWidth();
    private double screenHeight = screenSize.getHeight();

    private Stage firstPageStage;
    private Stage secondPageStage;
    private Stage thirdPageStage;
    private Stage fourthPageStage;

    private BSUser bsUser;
    private Connection connection;

    @FXML
    private Pagination depositsStatisticPagination;

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

    public void loadDepositsStatistic() throws SQLException {
        initializeCloseWindowListener();
        preparingPaginationControl();
    }

    private void preparingPaginationControl() throws SQLException {
        depositsStatisticPagination.setPageFactory(this::createPage);
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    private Pane createPage(int pageIndex) {
        Pane pane = null;
        try {
            if (pageIndex == 0) {
                pane = new Pane();
                BarChart<String, Number> chart = getGuidWithValueBarChart();
                chart.setLayoutX(14);
                chart.setMaxWidth(572);
                chart.setMaxHeight(250);
                pane.getChildren().add(chart);
                pane.setOnMouseClicked(click -> {
                    try {
                        if (click.getClickCount() == 2) {
                            if (firstPageStage == null) {
                                firstPageStage = new Stage();
                                firstPageStage.setOnCloseRequest(event -> {
                                        firstPageStage = null;
                                });
                                firstPageStage.setMaximized(true);
                                firstPageStage.setResizable(false);
                                Pane chartPane = new Pane();
                                chartPane.setMinHeight(screenHeight);
                                chartPane.setMinWidth(screenWidth);
                                BarChart<String, Number> chartPerBigSize =
                                        getGuidWithValueBarChart();
                                firstPageStage.setTitle(chartPerBigSize.getTitle());
                                chartPerBigSize.setLayoutX(14d);
                                chartPerBigSize.setMinHeight(
                                        screenHeight > 200
                                                ? screenHeight - 200
                                                : screenHeight
                                );
                                chartPerBigSize.setMinWidth(screenWidth - 28);
                                chartPane.getChildren().add(chartPerBigSize);
                                Scene scene = new Scene(chartPane);
                                firstPageStage.setScene(scene);
                                firstPageStage.show();
                            }
                        }
                    } catch (Exception e) {
                        log.error(BSException.getStackTraceAsString(e));
                        BSAlert.showAlert();
                    }
                });
            } else if (pageIndex == 1) {
                pane = new Pane();
                BarChart<String, Number> chart = getGuidWithTransactionCount();
                chart.setLayoutX(14);
                chart.setMaxWidth(572);
                chart.setMaxHeight(250);
                pane.getChildren().add(chart);
                pane.setOnMouseClicked(click -> {
                    try {
                        if (click.getClickCount() == 2) {
                            if (secondPageStage == null) {
                                secondPageStage = new Stage();
                                secondPageStage.setOnCloseRequest(event -> {
                                    secondPageStage = null;
                                });
                                secondPageStage.setMaximized(true);
                                secondPageStage.setResizable(false);
                                Pane chartPane = new Pane();
                                chartPane.setMinHeight(screenHeight);
                                chartPane.setMinWidth(screenWidth);
                                BarChart<String, Number> chartPerBigSize =
                                        getGuidWithTransactionCount();
                                secondPageStage.setTitle(chartPerBigSize.getTitle());
                                chartPerBigSize.setLayoutX(14d);
                                chartPerBigSize.setMinHeight(
                                        screenHeight > 200
                                                ? screenHeight - 200
                                                : screenHeight
                                );
                                chartPerBigSize.setMinWidth(screenWidth - 28);
                                chartPane.getChildren().add(chartPerBigSize);
                                Scene scene = new Scene(chartPane);
                                secondPageStage.setScene(scene);
                                secondPageStage.show();
                            }
                        }
                    } catch (Exception e) {
                        log.error(BSException.getStackTraceAsString(e));
                        BSAlert.showAlert();
                    }
                });
            } else if (pageIndex == 2) {
                pane = new Pane();
                LineChart<String, Number> chart = getGuidWithDateAndCash();
                chart.setLayoutX(14);
                chart.setMaxWidth(572);
                chart.setMaxHeight(250);
                pane.getChildren().add(chart);
                pane.setOnMouseClicked(click -> {
                    try {
                        if (click.getClickCount() == 2) {
                            if (thirdPageStage == null) {
                                thirdPageStage = new Stage();
                                thirdPageStage.setOnCloseRequest(event -> {
                                    thirdPageStage = null;
                                });
                                thirdPageStage.setMaximized(true);
                                thirdPageStage.setResizable(false);
                                Pane chartPane = new Pane();
                                chartPane.setMinHeight(screenHeight);
                                chartPane.setMinWidth(screenWidth);
                                LineChart<String,Number> chartPerBigSize =
                                        getGuidWithDateAndCash();
                                thirdPageStage.setTitle(chartPerBigSize.getTitle());
                                chartPerBigSize.setLayoutX(14d);
                                chartPerBigSize.setMinHeight(
                                        screenHeight > 200
                                                ? screenHeight - 200
                                                : screenHeight
                                );
                                chartPerBigSize.setMinWidth(screenWidth - 28);
                                chartPane.getChildren().add(chartPerBigSize);
                                Scene scene = new Scene(chartPane);
                                thirdPageStage.setScene(scene);
                                thirdPageStage.show();
                            }
                        }
                    } catch (Exception e) {
                        log.error(BSException.getStackTraceAsString(e));
                        BSAlert.showAlert();
                    }
                });
            } else if (pageIndex == 3) {
                pane = new Pane();
                BarChart<String, Number> chart = getGuidWithValueCard();
                chart.setLayoutX(14);
                chart.setMaxWidth(572);
                chart.setMaxHeight(250);
                pane.getChildren().add(chart);
                pane.setOnMouseClicked(click -> {
                    try {
                        if (click.getClickCount() == 2) {
                            if (fourthPageStage == null) {
                                fourthPageStage = new Stage();
                                fourthPageStage.setOnCloseRequest(event -> {
                                    fourthPageStage = null;
                                });
                                fourthPageStage.setMaximized(true);
                                fourthPageStage.setResizable(false);
                                Pane chartPane = new Pane();
                                chartPane.setMinHeight(screenHeight);
                                chartPane.setMinWidth(screenWidth);
                                BarChart<String, Number> chartPerBigSize =
                                        getGuidWithValueCard();
                                fourthPageStage.setTitle(chartPerBigSize.getTitle());
                                chartPerBigSize.setLayoutX(14d);
                                chartPerBigSize.setMinHeight(
                                        screenHeight > 200
                                                ? screenHeight - 200
                                                : screenHeight
                                );
                                chartPerBigSize.setMinWidth(screenWidth - 28);
                                chartPane.getChildren().add(chartPerBigSize);
                                Scene scene = new Scene(chartPane);
                                fourthPageStage.setScene(scene);
                                fourthPageStage.show();
                            }
                        }
                    } catch (Exception e) {
                        log.error(BSException.getStackTraceAsString(e));
                        BSAlert.showAlert();
                    }
                });
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }

        return pane;
    }

    private BarChart<String, Number> getGuidWithValueBarChart() throws SQLException {
        CategoryAxis categoryAxis = new CategoryAxis();
        NumberAxis numberAxis = new NumberAxis();
        categoryAxis.setLabel("Банковский дипозит");
        numberAxis.setLabel("Денеж. остаток (руб.)");
        BarChart<String, Number> chart = new BarChart<String, Number>(categoryAxis, numberAxis);
        chart.setTitle("Денежные остатки на ваших депозитах");
        HashMap<String, String> guidWithValue = BSBankAccount
                .getGuidWithValueBA(bsUser.getGuid(), connection);
        for (Map.Entry<String, String> entry : guidWithValue.entrySet()) {
            final String guid = entry.getKey();
            final String value = entry.getValue();
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(guid);
            series.getData().add(new XYChart.Data<String, Number>(guid, Float.valueOf(value != null
                    ? value
                    : "0.00")));
            chart.getData().add(series);
        }

        return chart;
    }

    private BarChart<String, Number> getGuidWithTransactionCount() throws SQLException {
        CategoryAxis categoryAxis = new CategoryAxis();
        NumberAxis numberAxis = new NumberAxis();
        categoryAxis.setLabel("Банковский дипозит");
        numberAxis.setLabel("Кол-во транзакций");
        BarChart<String, Number> chart = new BarChart<String, Number>(categoryAxis, numberAxis);
        chart.setTitle("Количество транзакций для ваших депозитах");
        HashMap<String, Integer> guidWithValue = BSTransactionInfo
                .getGuidWithTransactionCountBA(bsUser.getGuid(), connection);
        for (Map.Entry<String, Integer> entry : guidWithValue.entrySet()) {
            final String guid = entry.getKey();
            final Integer value = entry.getValue();
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(guid);
            series.getData().add(new XYChart.Data<String, Number>(guid, value));
            chart.getData().add(series);
        }

        return chart;
    }

    private LineChart<String, Number> getGuidWithDateAndCash() throws SQLException {
        CategoryAxis categoryAxis = new CategoryAxis();
        NumberAxis numberAxis = new NumberAxis();
        categoryAxis.setLabel("Дата и время транзакции");
        numberAxis.setLabel("Денеж. остаток (руб.)");
        LineChart<String,Number> chart = new LineChart<String,Number>(categoryAxis, numberAxis);
        chart.setTitle("Дата и время транзакций с информацией о денеж. остатке");
        HashMap<String, HashMap<String, String>> dateWithCash = BSTransactionInfo
                .getGuidWithDateAndValueBA(bsUser.getGuid(), connection);
        for (Map.Entry<String, HashMap<String, String>> entry : dateWithCash.entrySet()) {
            final String guid = entry.getKey();
            final HashMap<String, String> value = entry.getValue();
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(guid);
            for (Map.Entry<String, String> dateWithCashEntry : value.entrySet()) {
                final String date = dateWithCashEntry.getKey();
                final String cash = dateWithCashEntry.getValue();
                series.getData().add(new XYChart.Data<String, Number>(date, Float.valueOf(cash)));
            }
            chart.getData().add(series);
        }

        return chart;
    }

    private BarChart<String, Number> getGuidWithValueCard() throws SQLException {
        CategoryAxis categoryAxis = new CategoryAxis();
        NumberAxis numberAxis = new NumberAxis();
        categoryAxis.setLabel("Банковская карта");
        numberAxis.setLabel("Денеж. остаток");
        BarChart<String, Number> chart = new BarChart<String, Number>(categoryAxis, numberAxis);
        chart.setTitle("Денежный остаток на банковских картах");
        HashMap<String, String> guidWithValueCard = BSCard
                .getGuidWithValueCard(bsUser.getGuid(), connection);
        for (Map.Entry<String, String> entry : guidWithValueCard.entrySet()) {
            final String guid = entry.getKey();
            final String value = entry.getValue();
            final BigDecimal valueAsNumber = new BigDecimal(value).setScale(2,
                    BigDecimal.ROUND_DOWN);
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(guid);
            series.getData().add(new XYChart.Data<String, Number>(guid, valueAsNumber));
            chart.getData().add(series);
        }

        return chart;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
