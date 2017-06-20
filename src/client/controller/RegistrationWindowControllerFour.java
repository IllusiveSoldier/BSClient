package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationWindowControllerFour extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/registrationWindow_step_4.fxml";

    private static final Logger log = Logger
            .getLogger(RegistrationWindowControllerFour.class);

    /**
     * Экземпляр класса пользователь в системе. По мере прохождения пользователя по окнам
     * регистрации экземпляр наполняется данными, необходимыми для регистрации.
     */
    private BSUser bsUser;
    private Connection connection;

    /**
     * Ссылка на окно входа
     */
    @FXML
    private Hyperlink signInHprLnk;
    /**
     * Текстовое поле для отображение уникального идентификатора только что зарегистрировавшегося
     * в системе
     */
    @FXML
    private Label userGuidTF;

    /**
     * Обработчик нажатия на кнопку "Войти" для осуществления процесса входа пользователя в
     * систему
     */
    @FXML
    private void signInHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(MainWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            Scene scene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(scene);
            stage.show();

            setControllerForMainWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Метод, который передаёт в контроллер Application, Stage, Scene
     * @param loader
     */
    private void setControllerForMainWindow(FXMLLoader loader) {
        try {
            MainWindowController controller = loader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setUserLoginPassword(bsUser.getLogin(), bsUser.getPassword());
            controller.setConnection(connection);
            controller.setBsUser(bsUser);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    public void setUserGuid() {
        try {
            userGuidTF.setText(bsUser.getGuid());
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

    public Connection getConnection () {
        return connection;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }
}
