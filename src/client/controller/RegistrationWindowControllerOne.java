package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import client.model.exception.BSNotFilledData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class RegistrationWindowControllerOne extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/registrationWindow_step_1.fxml";

    private static final Logger log = Logger
            .getLogger(RegistrationWindowControllerOne.class);

    private Scene nextWindowScene;

    /**
     * Экземпляр класса пользователь в системе. По мере прохождения пользователя по окнам
     * регистрации экземпляр наполняется данными, необходимыми для регистрации.
     */
    private BSUser bsUser;
    private Connection connection;

    /**
     * Ссылка на предыдущее окно
     */
    @FXML
    private Hyperlink backHprLnk;
    /**
     * Ссылка на следующее окно регистрации
     */
    @FXML
    private Hyperlink nextHprLnk;
    /**
     * Текстовое поле для фамилии пользователя
     */
    @FXML
    private TextField lastNameTF;
    /**
     * Текстовое поле для имени пользователя
     */
    @FXML
    private TextField firstNameTF;
    /**
     * Текстовое поле для отчества пользователя
     */
    @FXML
    private TextField secondNameTF;
    /**
     * Поле для ввода даты рождения пользователя
     */
    @FXML
    private DatePicker birthDateDP;
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
    private void nextHprLnkClickHandle() throws BSNotFilledData {
        try {
            setDefaultViewOfMessageLabel();
            if (nextWindowScene != null) {
                final Stage stage = getStage();
                stage.setScene(nextWindowScene);
                stage.show();
            } else {
                if (lastNameTF.getText().isEmpty() || firstNameTF.getText().isEmpty()
                        || birthDateDP.getValue() == null) {
                    throw new BSNotFilledData(
                            "Не заполнено(-ы) или некорректны обязательные поля "
                                    + "(Фамилия, Имя, Дата рождения)"
                    );
                } else {
                    bsUser = new BSUser();
                    bsUser.setLastName(lastNameTF.getText());
                    bsUser.setFirstName(firstNameTF.getText());
                    bsUser.setSecondName(secondNameTF.getText());
                    bsUser.setBirthDate(getUserBirthDateFromDatePicker(birthDateDP.getValue()).getTime());

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getApplication().getClass()
                            .getResource(RegistrationWindowControllerTwo.PATH_TO_FXML_FILE));
                    Pane pane = fxmlLoader.load();

                    nextWindowScene = new Scene(pane);
                    final Stage stage = getStage();
                    stage.setScene(nextWindowScene);
                    stage.show();

                    setControllerForRegistrationWindowTwo(fxmlLoader);
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
     * Метод, который передаёт контролеру, отвечающего за второе окно регистрации Application,
     * Stage, Scene
     * @param loader
     */
    private void setControllerForRegistrationWindowTwo (FXMLLoader loader) {
        try {
            RegistrationWindowControllerTwo controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setPreviewScene(getScene());
            controller.setScene(nextWindowScene);
            controller.setBsUser(bsUser);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private Date getUserBirthDateFromDatePicker (LocalDate date) {
        Instant instant = Instant.from(date.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
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

    public void setConnection (Connection connection) {
        this.connection = connection;
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }
}
