package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSBankAccount;
import system.core.BSCard;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BankAccountsWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/bankAccountsWindow.fxml";

    private static final Logger log = Logger
            .getLogger(BankAccountsWindowController.class);

    private Scene depositsStatisticWindowScene;
    private Scene createBankAccountWindowScene;
    private Scene accountWindowScene;
    private Scene createCardScene;
    private Scene cardWindowScene;

    private BSUser bsUser;

    private ObservableList<String> bankAccountObservableList;
    private ArrayList<BSBankAccount> bsBankAccounts;
    private HashMap<String, BSBankAccount> bsBankAccountsAsMap;

    private ObservableList<String> cardsObservableList;
    private ArrayList<BSCard> bsCards;
    private HashMap<String, BSCard> bsCardsAsMap;

    private Connection connection;

    @FXML
    private ListView<String> bankAccountsListView;
    @FXML
    private Label userInfoLbl;
    @FXML
    private ListView<String> cardsListView;

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
    private void createCardHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(CreateCardWindowController
                            .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            createCardScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(createCardScene);
            stage.show();

            setControllerToCreateCardWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToCreateCardWindow(FXMLLoader fxmlLoader) {
        try {
            CreateCardWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(createCardScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);

            controller.initializeBSCard();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void createBankAccountHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(CreateBankAccountWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            createBankAccountWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(createBankAccountWindowScene);
            stage.show();

            setControllerToCreateBankAccountWindowController(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToCreateBankAccountWindowController (FXMLLoader fxmlLoader) {
        try {
            CreateBankAccountWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(createBankAccountWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.loadUserInfo();
            controller.loadDocDateAndDocNum();
            controller.loadCurrencyAndMarketName();
            controller.loadCapitalizationSelector();
            controller.loadBankAccountTypesSelector();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @FXML
    private void showStatisticAboutDepositsClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(DepositsStatisticWindowController
                            .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            depositsStatisticWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(depositsStatisticWindowScene);
            stage.show();

            setControllerForDepositsStatisticWindowController(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForDepositsStatisticWindowController(FXMLLoader fxmlLoader) {
        try {
            DepositsStatisticWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(depositsStatisticWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.setConnection(connection);
            controller.loadDepositsStatistic();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void loadUserInfo() {
        try {
            initializeCloseWindowListener();
            if (bsUser != null) {
                userInfoLbl.setText(
                        bsUser.getLastName() + " "
                        + bsUser.getFirstName().toUpperCase().charAt(0) + ". "
                        + bsUser.getSecondName().toUpperCase().charAt(0) + "., "
                        + bsUser.getGuid() + " (Глоб. идентификатор)"
                );
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void loadBankAccounts() {
        try {
            preparingAccountForView();
            bankAccountsListView.setItems(bankAccountObservableList);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void loadCards() {
        try {
            preparingCardsForView();
            cardsListView.setItems(cardsObservableList);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void preparingAccountForView() throws SQLException {
        bsBankAccounts = BSBankAccount.getBSBankAccountsAsList(bsUser.getGuid(), connection);
        bankAccountObservableList = FXCollections.observableList(
                getAccountsViewList()
        );
    }

    private ArrayList<String> getAccountsViewList() throws SQLException {
        ArrayList<String> accounts = new ArrayList<String>();
        bsBankAccountsAsMap = new HashMap<String, BSBankAccount>();
        for (BSBankAccount account : bsBankAccounts) {
            final String accountGuid = account.getGuid();
            accounts.add(accountGuid);
            bsBankAccountsAsMap.put(accountGuid, account);
        }

        return accounts;
    }

    private void preparingCardsForView() throws SQLException {
        bsCards = BSCard.getBsCardsPerUsser(bsUser.getGuid(), connection);
        cardsObservableList = FXCollections.observableList(
                getCardsViewList()
        );
    }

    private ArrayList<String> getCardsViewList() throws SQLException {
        ArrayList<String> cards = new ArrayList<String>();
        bsCardsAsMap = new HashMap<String, BSCard>();
        for (BSCard card : bsCards) {
            final String cardGuid = card.getGuid();
            cards.add(cardGuid);
            bsCardsAsMap.put(cardGuid, card);
        }

        return cards;
    }

    public void initializeListViewChangeListener() {
        bankAccountsListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                final String currentItemSelected = bankAccountsListView.getSelectionModel()
                        .getSelectedItem();
                if (bsBankAccountsAsMap != null && bsBankAccountsAsMap.size() > 0) {
                    if (bsBankAccountsAsMap.get(currentItemSelected) != null) {
                        BSBankAccount bsBankAccount = bsBankAccountsAsMap.get(currentItemSelected);
                        showAccountWindow(bsBankAccount);
                    }
                }
            }
        });
    }

    public void initializeCardsListViewChangeListener () {
        cardsListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                final String currentItemSelected = cardsListView.getSelectionModel()
                        .getSelectedItem();
                if (bsCardsAsMap != null && bsCardsAsMap.size() > 0) {
                    if (bsCardsAsMap.get(currentItemSelected) != null) {
                        BSCard bsCard = bsCardsAsMap.get(currentItemSelected);
                        showCardWindow(bsCard);
                    }
                }
            }
        });
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    private void showAccountWindow(BSBankAccount account)  {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass().getResource(AccountWindowController
                    .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            accountWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(accountWindowScene);
            stage.show();

            setControllerToAccountWindow(fxmlLoader, account);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToAccountWindow(FXMLLoader fxmlLoader, BSBankAccount bsBankAccount) {
        try {
            AccountWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(accountWindowScene);
            controller.setPreviewScene(getScene());

            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            final String accountGuid = bsBankAccount.getGuid();
            final BSBankAccount account = BSBankAccount.getBSBankAccountObject(accountGuid,
                    connection);
            controller.setBsBankAccount(account);
            controller.setConnection(connection);

            controller.loadDepositHolder();
            controller.loadDepositBeginAndEndDates();
            controller.loadDepositGuid();
            controller.loadAccountValue();

        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void showCardWindow(BSCard card) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass().getResource(CardWindowController
                    .PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            cardWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(cardWindowScene);
            stage.show();

            setControllerToCardWindow(fxmlLoader, card);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToCardWindow(FXMLLoader fxmlLoader, BSCard bsCard) {
        try {
            CardWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(cardWindowScene);
            controller.setPreviewScene(getScene());

            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            final String cardGuid = bsCard.getGuid();
            final BSCard card = BSCard.getBSCardObject(cardGuid, connection);
            controller.setBsCard(card);
            controller.setConnection(connection);

            controller.loadCardHolder();
            controller.loadCardGuid();
            controller.loadCardValue();

        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
