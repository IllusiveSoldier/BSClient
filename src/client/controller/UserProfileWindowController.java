package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exchange_rate.ExchangeRate;
import client.model.exchange_rate.ExchangeRateSuper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class UserProfileWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/userProfileWindow.fxml";

    private static final Logger log = Logger
            .getLogger(UserProfileWindowController.class);

    private Scene mainWindowScene;
    private Scene userSessionsWindowScene;
    private Scene userInfoWindowScene;
    private Scene exchangeRateScene;
    private Scene bankAccountsWindowScene;
    private Scene myFinanceWindowScene;

    private Connection connection;

    /**
     * Экземпляр класса "Пользователь".
     */
    private BSUser bsUser;

    /**
     * Кнопка выйти. Происходит процесс выхода их аккаунта. Происходят закрытие сесии.
     */
    @FXML
    private Hyperlink signOutHprLnk;
    /**
     * Ссылка отображает идентификатор сессии.
     */
    @FXML
    private Hyperlink sessionIdHprLnk;
    /**
     * Ссылка отображает текущего пользователя.
     */
    @FXML
    private Hyperlink userHprLnk;
    /**
     * Ссылка на курсы валют
     */
    @FXML
    private Hyperlink exchangeRateHrpLnk;
    /**
     * Текстовое поле для вывода сообщений об ошибке
     */
    @FXML
    private Label messageTF;

    private ExchangeRateSuper exchangeRateSuper;

    /**
     * Обработчик нажатия на кнопку выхода из аккаунта и закрытия сессии.
     */
    @FXML
    private void signOutHprLnkClickHandle() throws SQLException {
        try {
            final BSSession bsSession = getBsSession();
            BSSession.closeSession(bsSession.getUserGuid(), new Date().getTime(), bsSession
                    .getGuid(), connection);
            loadMainWindow();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void loadMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(MainWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            mainWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(mainWindowScene);
            stage.show();

            setControllerForMainWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForMainWindow(FXMLLoader loader) {
        try {
            MainWindowController controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(mainWindowScene);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Обработчик для ссылки на сессиию. При нажатии на ссылку пользователь переходит в расширенное
     * представление о всех его сессиях.
     */
    @FXML
    private void sessionIdHprLnkClickHandle() throws IOException {
        try {
            if (userSessionsWindowScene != null) {
                final Stage stage = getStage();
                stage.setScene(userSessionsWindowScene);
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(UserSessionsWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                userSessionsWindowScene = new Scene(pane);
                final Stage stage = getStage();
                stage.setScene(userSessionsWindowScene);
                stage.show();

                setControllerForUserSessionsWindow(fxmlLoader);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForUserSessionsWindow(FXMLLoader fxmlLoader) {
        try {
            UserSessionsWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(userSessionsWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setUserGuid(bsUser.getGuid());
            controller.setConnection(connection);
            controller.putDataToObservableList();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Обработчик для ссылки на пользователя. При нажатии на ссылку пользователь переходит в
     * свой профиль.
     */
    @FXML
    private void userHprLnkClickHandle() {
        try {
            if (userInfoWindowScene != null) {
                final Stage stage = getStage();
                stage.setScene(userInfoWindowScene);
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(UserInfoWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                userInfoWindowScene = new Scene(pane);
                final Stage stage = getStage();
                stage.setScene(userInfoWindowScene);
                stage.show();

                setControllerToUserInfoWindow(fxmlLoader);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToUserInfoWindow (FXMLLoader fxmlLoader) {
        try {
            UserInfoWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(userInfoWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.initializeUserInfo();
            controller.initializeCloseWindowListenerSub();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void setSessionIdText() {
        final BSSession bsSession = getBsSession();
        final String sessionGuid = bsSession.getGuid();
        sessionIdHprLnk.setText(sessionGuid);
    }

    public void setUserHprLnkText() {
        userHprLnk.setText(
                bsUser.getLastName() + " " + bsUser.getFirstName() + " " + bsUser.getSecondName()
        );
    }

    public void initializeCloseWindowListenerSub ()  {
        initializeCloseWindowListener();
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    /**
     * Данный метод загружает инфомрацию о текущем курсе валют
     */
    public void loadExchangeRate() {
        try {
            exchangeRateSuper = new ExchangeRateSuper().getExchangeSuper();
            ExchangeRate rate = exchangeRateSuper.getExchangeRateById("R01235");
            String rateInfo = rate.getName() + ": " + rate.getValue();
            exchangeRateHrpLnk.setText(rateInfo);
        } catch (Exception e) {
            exchangeRateHrpLnk.setText("Не удалось получить информацию");
        }
    }

    /**
     * Обработчик нажатия на кнопку перехода к курсам валют
     */
    @FXML
    private void exchangeRateHrpLnkClickHandle() {
        try {
            if (exchangeRateScene != null) {
                final Stage stage = getStage();
                stage.setScene(exchangeRateScene);
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(ExchangeRatesWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                exchangeRateScene = new Scene(pane);
                final Stage stage = getStage();
                stage.setScene(exchangeRateScene);
                stage.show();

                setControllerToExchangeRateWindow(fxmlLoader);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToExchangeRateWindow(FXMLLoader fxmlLoader) {
        try {
            ExchangeRatesWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(exchangeRateScene);
            controller.setPreviewScene(getScene());
            controller.setExchangeRateSuper(exchangeRateSuper);
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.loadDateAndMarketName();
            controller.putDataToObservableList();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void bankAccountsHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(BankAccountsWindowController
                            .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            bankAccountsWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(bankAccountsWindowScene);
            stage.show();

            setControllerToBankAccountsWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToBankAccountsWindow(FXMLLoader fxmlLoader) {
        BankAccountsWindowController controller = fxmlLoader.getController();
        controller.setApplication(getApplication());
        controller.setStage(getStage());
        controller.setScene(bankAccountsWindowScene);
        controller.setPreviewScene(getScene());
        controller.setBsUser(bsUser);
        controller.setBsSession(getBsSession());
        controller.setConnection(connection);
        controller.loadUserInfo();
        controller.loadBankAccounts();
        controller.loadCards();
        controller.initializeListViewChangeListener();
        controller.initializeCardsListViewChangeListener();
    }

    @FXML
    private void myFinancesHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(MyFinanceWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            myFinanceWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(myFinanceWindowScene);
            stage.show();

            setControllerToMyFinanceWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToMyFinanceWindow(FXMLLoader fxmlLoader) {
        try {
            MyFinanceWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(myFinanceWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.initializeCloseWindowListenerSub();
            controller.loadPieChart();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public BSUser getBsUser () {
        return bsUser;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public Connection getConnection () {
        return connection;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}

