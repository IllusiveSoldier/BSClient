package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationWindowControllerTwo extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/registrationWindow_step_2.fxml";

    private static final Logger log = Logger
            .getLogger(RegistrationWindowControllerTwo.class);

    private Scene nextWindowScene;

    /**
     * Экземпляр класса пользователь в системе. По мере прохождения пользователя по окнам
     * регистрации экземпляр наполняется данными, необходимыми для регистрации.
     */
    private BSUser bsUser;
    private Connection connection;

    /**
     * Ссылка на предыдующее окно регистрации
     */
    @FXML
    private Hyperlink backHprLnk;
    /**
     * Ссылка на следующее окно регистрации пользователя
     */
    @FXML
    private Hyperlink nextHprLnk;
    /**
     * Текстовое поле для ввода логина пользователя
     */
    @FXML
    private TextField loginTF;
    /**
     * Текстовое поле для ввода пароля пользователя
     */
    @FXML
    private PasswordField passwordPF;
    /**
     * Текстовое поле для повторения пароля пользователя
     */
    @FXML
    private PasswordField passwordRepeatTF;
    /**
     * Текстовое поле, предназначенное для вывода сообщений при прохождении процесса регистрации
     */
    @FXML
    private Label messageTF;

    /**
     * Обработчик нажатия на кнопку перехода на предыдущее окно
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
     * Обработчик нажатия на кнопку перехода на следующее окно регистрации пользователя
     */
    @FXML
    private void nextHprLnkClickHandle() {
        try {
            setDefaultViewOfMessageLabel();
            if (nextWindowScene != null) {
                final Stage stage = getStage();
                stage.setScene(nextWindowScene);
                stage.show();
            } else {
                if (loginTF.getText().isEmpty() || !BSUser.isLatinCharactersInLogin(loginTF.getText())
                        || passwordPF.getText().isEmpty() || passwordPF.getText().length() < 10
                        || passwordPF.getText().length() > 255) {
                    throw new BSNotFilledData(
                            "Не заполнено(-ы) или некорректны обязательные поля "
                                    + "(Логин, Пароль(не менее 10 символов))"
                    );
                } else {
                    if (!passwordPF.getText().equals(passwordRepeatTF.getText())) {
                        throw new BSNotFilledData("Пароли в двух текстовых "
                                + "полях должны совпадать");
                    } else {
                        bsUser.setLogin(loginTF.getText());
                        bsUser.setPassword(passwordPF.getText());

                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getApplication().getClass()
                                .getResource(RegistrationWindowControllerThree.PATH_TO_FXML_FILE));
                        Pane pane = fxmlLoader.load();

                        nextWindowScene = new Scene(pane);
                        final Stage stage = getStage();
                        stage.setScene(nextWindowScene);
                        stage.show();

                        setControllerForRegistrationWindowThree(fxmlLoader, nextWindowScene, bsUser);
                    }
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

    /**
     * Метод, который передаёт контролеру, отвечающего за третье окно регистрации Application,
     * Stage, Scene
     * @param loader
     */
    private void setControllerForRegistrationWindowThree (FXMLLoader loader, Scene currentScene,
                                                          BSUser bsUser) {
        try {
            RegistrationWindowControllerThree controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(currentScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @Override
    public void closeSession () throws SQLException {

    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
