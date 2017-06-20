package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSUser;
import system.core.EmailSender;
import system.core.database.BSDb;

import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationWindowControllerThree extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/registrationWindow_step_3.fxml";

    private static final Logger log = Logger
            .getLogger(RegistrationWindowControllerThree.class);

    private Scene nextWindowScene;
    private Connection connection;

    /**
     * Экземпляр класса пользователь в системе. По мере прохождения пользователя по окнам
     * регистрации экземпляр наполняется данными, необходимыми для регистрации.
     */
    private BSUser bsUser;

    /**
     * Ссылка на предыдующее окно регистрации
     */
    @FXML
    private Hyperlink backHprLnk;
    /**
     * Ссылка при нажатии на которой производится процесс регистрации пользователя в системе
     */
    @FXML
    private Hyperlink completeHprLnk;
    /**
     * Текстовое поле для ввода адреса электронной почты пользователя
     */
    @FXML
    private TextField emailTF;
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
     * Обработчик нажатия на кнопку завершения регистрации пользователя в системе
     */
    @FXML
    private void completeHprLnkClickHandle() {
        try {
            setDefaultViewOfMessageLabel();
            if (nextWindowScene != null) {
                final Stage stage = getStage();
                stage.setScene(nextWindowScene);
                stage.show();
            } else {
                if (!emailTF.getText().isEmpty() && !BSUser.isCorrectEmailAddress(emailTF.getText())) {
                    throw new BSNotFilledData(
                            "Некорректно заполнен адрес электронной почты"
                    );
                } else {
                    completeRegister();
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

    private void completeRegister() {
        try {
            bsUser.seteMail(emailTF.getText().isEmpty() ? null : emailTF.getText());

            connection = BSDb.getConnectionFromConfigurationFile(null);
            if (!BSUser.isExistUser(bsUser.getLogin(), bsUser.geteMail(), connection)) {
                BSUser.signUp(BSUser.ADMIN_OUID,
                        bsUser.getLogin(),
                        bsUser.getPassword(),
                        bsUser.getFirstName(),
                        bsUser.getLastName(),
                        bsUser.getSecondName(),
                        bsUser.getBirthDate(),
                        bsUser.geteMail(),
                        connection
                );
                if (BSUser.isExistUser(bsUser.getLogin(), bsUser.geteMail(), connection)) {
                    bsUser.setGuid(
                            BSUser.signIn(
                                    bsUser.getLogin(),
                                    bsUser.geteMail(),
                                    bsUser.getPassword(),
                                    connection
                            )
                    );
                    if (!emailTF.getText().isEmpty())
                        emailSend();
                    initRootLayout();
                } else {
                    messageTF.setTextFill(Paint.valueOf("RED"));
                    messageTF.setText("При регистрации произошла ошибка. Попробуйте чуть"
                            + " позже, либо обратитесь к администратору");
                }
            } else {
                messageTF.setTextFill(Paint.valueOf("RED"));
                messageTF.setText("Пользователь с таким логином/адресом электронной "
                        + "почты уже существует");
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void initRootLayout () {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(RegistrationWindowControllerFour.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            nextWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(nextWindowScene);
            stage.show();

            setControllerForRegistrationWindowFour(fxmlLoader, nextWindowScene, bsUser);
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
     * Меотд, который отправляет письмо на электронную почту
     */
    private void emailSend() {
        try {
            Thread thread = new Thread(() -> {
                EmailSender sender = new EmailSender(
                    "bankingsystembot@gmail.com",
                    "hbyLvMcGp927360ab927360ab"
                );
                sender.send(
                    "Регистрация в системе Banking System ©",
                    "Здравствуйте, " + bsUser.getFirstName() + " "
                            + bsUser.getSecondName() + ".\n"
                            + "Благодарим за регистрацию в системе Banking System ©.\n"
                            + "Ваш логин для входа: " + bsUser.getLogin(),
                    "BSBot",
                    bsUser.geteMail()
                );
            });
            thread.start();
        } catch (Exception e) {
            messageTF.setTextFill(Paint.valueOf("RED"));
            messageTF.setText("При отправке сообщения на электронную почту возникла ошибка");
        }
    }

    /**
     * Метод, который передаёт контролеру, отвечающего за четвёртое окно регистрации Application,
     * Stage, Scene
     * @param loader
     */
    private void setControllerForRegistrationWindowFour (FXMLLoader loader, Scene currentScene,
                                                         BSUser bsUser) {
        try {
            RegistrationWindowControllerFour controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(currentScene);
            controller.setPreviewScene(getPreviewScene());
            controller.setBsUser(bsUser);
            controller.setUserGuid();
            controller.setConnection(connection);
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
}
