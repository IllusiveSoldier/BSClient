package client.controller;

import client.BSApp;
import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSUser;
import system.core.Generator;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class ChangeUserPasswordWindowController implements Initializable {
    public static final String PATH_TO_FXML_FILE = "view/changeUserPasswordWindow.fxml";

    private static final Logger log = Logger
            .getLogger(ChangeUserPasswordWindowController.class);

    private BSApp application;
    private Stage stage;
    private Scene scene;

    private String userGuid;
    private Connection connection;

    /**
     * Текстовое поле для вывода сообщений
     */
    @FXML
    private Label messageLbl;
    /**
     * Текстовое поле для ввода нового пароля пользователя
     */
    @FXML
    private PasswordField newPasswordPF;
    /**
     * Текстовое поля для повторения нового пароля пользователя
     */
    @FXML
    private PasswordField newPasswordRepeatPF;
    /**
     * Кнопка, которая позволяет генерировать псевдослучайный пароль
     */
    @FXML
    private Button generatePasswordBtn;
    /**
     * Индикатор, отражающий длину пароля
     */
    @FXML
    private ProgressIndicator passwordComplexityIndicator;
    /**
     * Текстовое поле, которре отражает "сложность" пароля по его длине
     */
    @FXML
    private Label passwordComplexityTF;
    /**
     * Кнопка для изменения пароля пользователя в системе
     */
    @FXML
    private Button changePasswordBtn;

    /**
     * Обработчик нажатия на кнопку изменения пароля
     */
    @FXML
    private void changePasswordBtnClickHandle() {
        setDefaultViewOfMessageLabel();
        try {
            final String newPass = newPasswordPF.getText();
            final String newPassRepeat = newPasswordRepeatPF.getText();
            if (newPass != null && !newPass.isEmpty() && newPassRepeat != null
                    && !newPassRepeat.isEmpty() && newPass.length() >= 10 && newPass.length() <= 255
                    && newPass.equals(newPassRepeat))
            {
                BSUser.changePassword(userGuid, newPass, connection);
                messageLbl.setTextFill(javafx.scene.paint.Paint.valueOf("GREEN"));
                messageLbl.setText("Пароль изменен");
            } else throw new BSNotFilledData("Пароли не совпадают или короче"
                    + " 10 символов");
        } catch (BSNotFilledData notFilledData) {
            messageLbl.setTextFill(javafx.scene.paint.Paint.valueOf("RED"));
            messageLbl.setText(notFilledData.getMessage());
        } catch (Exception e) {
            messageLbl.setTextFill(javafx.scene.paint.Paint.valueOf("RED"));
            messageLbl.setText("При изменении пароля произошли ошибка");
        }
    }

    /**
     * Метод, который возвращает текстовое поле для вывода сообщений в состояние по умполчанию
     * (меняет цвет текста на чёрный и очищает поле от текста)
     */
    private void setDefaultViewOfMessageLabel() {
        try {
            messageLbl.setText("");
            messageLbl.setTextFill(javafx.scene.paint.Paint.valueOf("BLACK"));
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        newPasswordPF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s,
                                String s2) {
                final int newPassLength = newPasswordPF.getText().length();
                if (newPassLength >= 10 && newPassLength <= 15) {
                    passwordComplexityTF.setText("Легкий");
                    passwordComplexityIndicator.setProgress(0.3f);
                }
                if (newPassLength > 15 && newPassLength <= 20) {
                    passwordComplexityTF.setText("Нормальный");
                    passwordComplexityIndicator.setProgress(0.7f);
                }
                if (newPassLength > 20 && newPassLength <= 25) {
                    passwordComplexityTF.setText("Сложный");
                    passwordComplexityIndicator.setProgress(1f);
                }
                if (newPassLength > 25) {
                    passwordComplexityTF.setText("Очень сложный");
                    passwordComplexityIndicator.setProgress(1f);
                }
            }
        });
    }

    /**
     * Обработчик нажатия на кнопку генерирования пароля
     */
    @FXML
    private void generatePasswordBtnClickHandle() {
        setDefaultViewOfMessageLabel();
        String generatingUserPassword = Generator.getPseudoGeneratedPassword(25);
        newPasswordPF.setText(generatingUserPassword);
        newPasswordRepeatPF.setText(generatingUserPassword);
        passwordComplexityIndicator.setProgress(1f);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(generatingUserPassword), null
        );
        messageLbl.setText("Пароль скопирован в буфер обмена");
        messageLbl.setTextFill(javafx.scene.paint.Paint.valueOf("GREEN"));
    }

    public void setApplication (BSApp application) {
        this.application = application;
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void setScene (Scene scene) {
        this.scene = scene;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
