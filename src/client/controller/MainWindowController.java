package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import system.core.BSSession;
import system.core.BSUser;
import system.core.database.BSDb;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class MainWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/mainWindow.fxml";

    private static final Logger log = Logger.getLogger(MainWindowController.class);

    private Scene signUpScene;
    private Scene userProfileWindowScene;
    private Connection connection;

    /**
     * Экземпляр класса "Пользователь". После процесса аутентификации экземпляр будет передаваться
     * в профиль.
     */
    private BSUser bsUser;

    /**
     * Ссылка на регистрацию
     */
    @FXML
    private Hyperlink signUpHprLnk;
    /**
     * Текстовое поля для ввода логина пользователя
     */
    @FXML
    private TextField loginTF;
    /**
     * Текстовое поле для ввода пароля пользователя
     */
    @FXML
    private PasswordField passwordPF;
    /**
     * Кнопка "Войти" для процесса вхождения пользователя в систему
     */
    @FXML
    private Button signInBtn;
    /**
     * Текстовое поле, предназначенное для вывода сообщений при прохождении процесса регистрации
     */
    @FXML
    private Label messageTF;

    /**
     * Обработчик нажатия на кнопку регистрации пользователя в системе
     */
    @FXML
    public void signUpHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(RegistrationWindowControllerOne.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            signUpScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(signUpScene);
            stage.show();

            setControllerForRegistrationWindowOne(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который передаёт контролеру, отвечающего за первое окно регистрации Application,
     * Stage, Scene
     * @param loader
     */
    private void setControllerForRegistrationWindowOne (FXMLLoader loader) {
        try {
            RegistrationWindowControllerOne controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(signUpScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который подставляет логин и пароль в соответствующие текстовые поля
     * @param userLogin
     * @param userPassword
     */
    public void setUserLoginPassword(String userLogin, String userPassword) {
        try {
            loginTF.setText(userLogin);
            passwordPF.setText(userPassword);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void initializeConnection() throws SQLException, ParserConfigurationException,
            SAXException, IOException {
        connection = connection != null
                ? connection
                : BSDb.getConnectionFromConfigurationFile(null);
    }

    /**
     * Обработчик нажатия на кнопку входа в систему
     */
    @FXML
    public void signInBtnClickHandle() {
        try {
            setDefaultViewOfMessageLabel();
            if (loginTF.getText().isEmpty() || passwordPF.getText().isEmpty()) {
                throw new BSNotFilledData(
                        "Для входа в систему заполните обязательные поля (Логин, Пароль)"
                );
            } else {
                String userGuid;
                initializeConnection();
                if (BSUser.isCorrectEmailAddress(loginTF.getText())) {
                    userGuid = BSUser.signIn(
                            null, loginTF.getText(), passwordPF.getText(), connection
                    );
                } else {
                    userGuid = BSUser.signIn(
                            loginTF.getText(), null, passwordPF.getText(), connection
                    );
                }
                if (userGuid != null) {
                    if (bsUser == null) {
                        bsUser = BSUser.getBSUser(userGuid, connection);
                    }
                    final BSSession bsSession = new BSSession(
                            BSSession.openSession(
                                    BSUser.ADMIN_OUID, bsUser.getGuid(),
                                    BSSession.getComputerIpAddress(), connection
                            ),
                            bsUser.getGuid(),
                            new Date().getTime()
                    );
                    setBsSession(bsSession);
                    initializeUserProfileWindowLayout(
                            bsUser,
                            bsSession
                    );
                } else {
                    messageTF.setTextFill(Paint.valueOf("RED"));
                    messageTF.setText("Неправильный логин и(или) пароль");
                }
            }
        } catch (BSNotFilledData notFilledData) {
            messageTF.setTextFill(Paint.valueOf("RED"));
            messageTF.setText(notFilledData.getMessage());
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void initializeUserProfileWindowLayout(BSUser bsUser, BSSession bsSession) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(UserProfileWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            userProfileWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(userProfileWindowScene);
            stage.show();

            setControllerForUserProfileWindowController(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForUserProfileWindowController(FXMLLoader loader) {
        try {
            UserProfileWindowController controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(userProfileWindowScene);
            controller.setPreviewScene(getPreviewScene());

            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());

            controller.setSessionIdText();
            controller.setUserHprLnkText();

            controller.setConnection(connection);
            controller.initializeCloseWindowListenerSub();
            controller.loadExchangeRate();
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

    @Override
    public void closeSession () throws SQLException {
    }

    public Connection getConnection () {
        return connection;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }

    public BSUser getBsUser () {
        return bsUser;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }
}
