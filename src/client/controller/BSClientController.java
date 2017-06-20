package client.controller;

import client.BSApp;
import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;

import java.sql.SQLException;

/**
 * Базовый класс для контроллеров.
 */
public abstract class BSClientController {
    private static final Logger log = Logger
            .getLogger(BSClientController.class);

    private BSApp application;
    private Stage stage;
    /**
     * Текущая сцена для отображения.
     */
    private Scene scene;
    /**
     * Сцена, которая отображалась в предыдущем окне.
     */
    private Scene previewScene;

    /**
     * Экземпляр класса "Пользовательская сессия". Т.к. в каждом окне пользователь может выйти из
     * системы, необходимо передавать ссылки на экземпляры сессии, чтобы иметь закрыть её.
     */
    private BSSession bsSession;

    /**
     * Метод закрытия пользовательской сессии. Вызывается, когда пользователь выходит из проложения.
     */
    public abstract void closeSession() throws SQLException;

    /**
     * Метод, который иницилизирует listener для закрытия окна. Вызывает метод закрытия
     * пользовательской сессии.
     */
    public void initializeCloseWindowListener() {
        stage.setOnCloseRequest(event -> {
            try {
                closeSession();
            } catch (SQLException e) {
                log.error(BSException.getStackTraceAsString(e));
                BSAlert.showAlert();
            }
        });
    }

    /**************************** Getter and Setter ****************************/

    public BSApp getApplication () {
        return application;
    }

    public void setApplication (BSApp application) {
        this.application = application;
    }

    public Stage getStage () {
        return stage;
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public Scene getScene () {
        return scene;
    }

    public void setScene (Scene scene) {
        this.scene = scene;
    }

    public Scene getPreviewScene () {
        return previewScene;
    }

    public void setPreviewScene (Scene previewScene) {
        this.previewScene = previewScene;
    }

    public BSSession getBsSession () {
        return bsSession;
    }

    public void setBsSession (BSSession bsSession) {
        this.bsSession = bsSession;
    }
}
