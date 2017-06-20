package client.controller;

import client.BSApp;
import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class UserInfoWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/userInfoWindow.fxml";

    private static final int MAX_FILE_SIZE = 1000000;

    private static final Logger log = Logger
            .getLogger(UserInfoWindowController.class);

    private Stage changeUserPasswordWindowStage;
    private Scene deleteUserWindowScene;

    private BSUser bsUser;
    private Connection connection;

    /**
     * Кнопка для перехода на предыдущее окно
     */
    @FXML
    private Hyperlink backHprLnk;
    /**
     * Пользовательский аватар
     */
    @FXML
    private ImageView avatarImgView;
    /**
     * Кнопка для изменения пользовательского аватара
     */
    @FXML
    private Hyperlink changeAvatarHprLnk;
    /**
     * Текстовое поле для фамилии пользователя
     */
    @FXML
    private Label lastNameLbl;
    /**
     * Текстовое поле для имени пользователя
     */
    @FXML
    private Label firstNameLbl;
    /**
     * Текстовое поля для отчества пользователя
     */
    @FXML
    private Label secondNameLbl;
    /**
     * Текстовое поле для логина пользователя
     */
    @FXML
    private Label loginLbl;
    /**
     * Кнопка для изменения пароля пользователя
     */
    @FXML
    private Hyperlink changePasswordHrpLnk;
    /**
     * Текстовое поле для глобального идентификатора
     */
    @FXML
    private Label userIdentifierLbl;
    /**
     * Текстовое поле для отображения адреса электронной почты
     */
    @FXML
    private Label emailLbl;
    /**
     * Текстовое поле для вывода сообщений
     */
    @FXML
    private Label messageLbl;

    /**
     * Обработчик нажатия на кнопку "Назад"
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

    @FXML
    private void deleteAccountHprLnkClickHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getApplication().getClass()
                    .getResource(DeleteUserWindowController.PATH_TO_FXML_FILE));
            Pane pane = fxmlLoader.load();

            deleteUserWindowScene = new Scene(pane);
            final Stage stage = getStage();
            stage.setScene(deleteUserWindowScene);
            stage.show();

            setControllerToDeleteUserWindow(fxmlLoader);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerToDeleteUserWindow(FXMLLoader fxmlLoader) {
        try {
            DeleteUserWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(getStage());
            controller.setScene(deleteUserWindowScene);
            controller.setPreviewScene(getScene());
            controller.setBsUser(bsUser);
            controller.setBsSession(getBsSession());
            controller.initializeCloseWindowListenerSub();
            controller.setConnection(connection);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Обработчик нажатия на кнопку "Изменить пароль"
     */
    @FXML
    private void changePasswordHrpLnkClickHandle() {
        try {
            if (changeUserPasswordWindowStage == null) {
                changeUserPasswordWindowStage = new Stage();
                changeUserPasswordWindowStage.setOnCloseRequest(event -> {
                    changeUserPasswordWindowStage = null;
                });
                changeUserPasswordWindowStage.setResizable(false);
                changeUserPasswordWindowStage.setTitle(BSApp.BS_SYSTEM_APPLICATION_NAME);

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getApplication().getClass()
                        .getResource(ChangeUserPasswordWindowController.PATH_TO_FXML_FILE));
                Pane pane = fxmlLoader.load();

                Scene scene = new Scene(pane);
                changeUserPasswordWindowStage.setScene(scene);
                changeUserPasswordWindowStage.sizeToScene();
                changeUserPasswordWindowStage.show();

                setControllerForChangeUserPasswordWindow(fxmlLoader, scene);
            }
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void setControllerForChangeUserPasswordWindow(FXMLLoader fxmlLoader, Scene scene) {
        try {
            ChangeUserPasswordWindowController controller = fxmlLoader.getController();
            controller.setApplication(getApplication());
            controller.setStage(changeUserPasswordWindowStage);
            controller.setScene(scene);
            controller.setUserGuid(bsUser.getGuid());
            controller.setConnection(connection);
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public void initializeCloseWindowListenerSub() {
        initializeCloseWindowListener();
    }

    /**
     * Обработчик нажатия на кнопку "Изменить аватар"
     */
    @FXML
    private void changeAvatarHprLnkClickHandle() {
        try {
            setDefaultViewOfMessageLabel();
            fileChoose();
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void fileChoose() throws IOException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аватар");
        fileChooserConfigure(fileChooser);
        File imageFile = fileChooser.showOpenDialog(getStage());
        imageFileProcessing(imageFile);
    }

    private void fileChooserConfigure(FileChooser fileChooser) {
        if (fileChooser != null)
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
    }

    private void imageFileProcessing(File file) throws IOException, SQLException {
        if (file != null) {
            if (file.exists()) {
                if (file.length() <= MAX_FILE_SIZE) {
                    final Path filePath = file.toPath();
                    byte[] fileBytesArray = Files.readAllBytes(filePath);
                    BSUser.changeAvatar(bsUser.getGuid(), fileBytesArray, connection);
                    avatarImgView.setImage(
                            SwingFXUtils.toFXImage(ImageIO.read(
                                    new ByteArrayInputStream(fileBytesArray)
                                    ),
                                    null
                            ));
                } else {
                    messageLbl.setTextFill(Paint.valueOf("RED"));
                    messageLbl.setText("Размер файла не должен превышать 1Мб");
                }
            }
        }
    }

    /**
     * Метод, который возвращает текстовое поле для вывода сообщений в состояние по умполчанию
     * (меняет цвет текста на чёрный и очищает поле от текста)
     */
    private void setDefaultViewOfMessageLabel() {
        try {
            messageLbl.setText("");
            messageLbl.setTextFill(Paint.valueOf("BLACK"));
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    /**
     * Определение данных
     */
    public void initializeUserInfo() {
        try {
            lastNameLbl.setText(bsUser.getLastName());
            firstNameLbl.setText(bsUser.getFirstName());
            secondNameLbl.setText(bsUser.getSecondName());
            loginLbl.setText(bsUser.getLogin());
            userIdentifierLbl.setText(bsUser.getGuid());
            emailLbl.setText(
                    bsUser.geteMail() == null
                    ? "При регистрации вы не указали E-Mail"
                    : bsUser.geteMail()
            );
            final byte[] userAvatar = bsUser.getAvatar();
            if (userAvatar != null)
                avatarImgView.setImage(
                        SwingFXUtils.toFXImage(ImageIO.read(
                                new ByteArrayInputStream(bsUser.getAvatar())
                                ),
                                null
                        ));
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
