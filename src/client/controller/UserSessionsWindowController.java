package client.controller;

import client.model.BSAlert;
import client.model.exception.BSException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import system.core.BSSession;
import system.core.BSUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserSessionsWindowController extends BSClientController {
    public static final String PATH_TO_FXML_FILE = "view/userSessionsWindow.fxml";

    private static final Logger log = Logger
            .getLogger(UserSessionsWindowController.class);

    private BSUser bsUser;

    /**
     * Глобальный идентификатор пользователя
     */
    private String userGuid;

    private ObservableList<Session> data;
    private ArrayList<BSSession> bsSessions;
    private ArrayList<Session> sessions;
    private Connection connection;

    /**
     * Ссылка на предыдущее окно
     */
    @FXML
    private Hyperlink backHrpLnk;
    /**
     * Таблица, в которую будут выводиться сессии пользователя
     */
    @FXML
    private TableView<Session> userSessionsTableView;
    /**
     * Столбец таблицы, содержащий глобальный идентификатор сессии
     */
    private TableColumn<Session, String> sessionIdColumn;
    /**
     * Столбец таблицы, содержащий дату создания сессии
     */
    private TableColumn<Session, String> sessionCreateDateColumn;
    /**
     * Столбец таблицы, содержащий дату закрытия сессии
     */
    private TableColumn<Session, String> sessionOutDateColumn;
    /**
     * Столбец таблицы, содержащий информацию о продолжительности сессии
     */
    private TableColumn<Session, String> sessionDurationColumn;
    /**
     * Столбец таблицы, содержащий IP-адрес клиента
     */
    private TableColumn<Session, String> sessionIpAddressColumn;

    /**
     * Обработчик нажатия на ссылку для перехода на предыдущее окно
     */
    @FXML
    private void backHrpLnkClickHandle() {
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
     * Метод, который наполняет данными таблицу
     */
    public void putDataToObservableList() {
        try {
            initializeCloseWindowListener();
            sessionIdColumn = new TableColumn<Session, String>("Идентификатор");
            sessionCreateDateColumn = new TableColumn<Session, String>("Открытие сесии");
            sessionOutDateColumn = new TableColumn<Session, String>("Закрытие сесии");
            sessionDurationColumn = new TableColumn<Session, String>("Продолжительность");
            sessionIpAddressColumn = new TableColumn<Session, String>("IP-адрес");

            sessionIdColumn.setCellValueFactory(new PropertyValueFactory<>("guid"));
            sessionCreateDateColumn.setCellValueFactory(new PropertyValueFactory<>("createDate"));
            sessionOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("outDate"));
            sessionDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            sessionIpAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));

            bsSessions = BSSession.getBSSessionList(userGuid, connection);
            sessionsPreprocessor();
            data = FXCollections.observableArrayList(
                sessions
            );
            userSessionsTableView.setItems(data);
            userSessionsTableView.getColumns().addAll(
                    sessionIdColumn,
                    sessionCreateDateColumn,
                    sessionOutDateColumn,
                    sessionDurationColumn,
                    sessionIpAddressColumn
            );
        } catch (Exception e) {
            log.error(BSException.getStackTraceAsString(e));
            BSAlert.showAlert();
        }
    }

    private void sessionsPreprocessor() {
        sessions = new ArrayList<Session>();
        long unixBeginDate = new Date(0).getTime();
        for (BSSession bsSession : bsSessions) {
            long duration = bsSession.getDuration();
            String outDate = null;
            String durationString = null;
            if (bsSession.getOutDate() != unixBeginDate) {
                outDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                        .format(bsSession.getOutDate());
                durationString = String.valueOf(duration) + "сек. (" +
                        String.valueOf(duration / 60L) + "мин.) ";
            } else {
                outDate = "-";
                durationString = "-";
            }
            sessions.add(
                    new Session(
                            bsSession.getGuid(),
                            new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                                    .format(bsSession.getCreateDate()),
                            outDate,
                            durationString,
                            bsSession.getIpAddress()
                    )
            );
        }
    }

    @Override
    public void closeSession () throws SQLException {
        final BSSession bsSession = getBsSession();
        BSSession.closeSession(
                bsUser.getGuid(), new Date().getTime(), bsSession.getGuid(), connection
        );
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public Connection getConnection () {
        return connection;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }

    public class Session {
        private String guid;
        private String createDate;
        private String outDate;
        private String duration;
        private String ipAddress;

        public Session(String guid, String createDate, String outDate, String duration,
                       String ipAddress) {
            this.guid = guid;
            this.createDate = createDate;
            this.outDate = outDate;
            this.duration = duration;
            this.ipAddress = ipAddress;
        }

        public String getGuid () {
            return guid;
        }

        public void setGuid (String guid) {
            this.guid = guid;
        }

        public String getCreateDate () {
            return createDate;
        }

        public void setCreateDate (String createDate) {
            this.createDate = createDate;
        }

        public String getOutDate () {
            return outDate;
        }

        public void setOutDate (String outDate) {
            this.outDate = outDate;
        }

        public String getDuration () {
            return duration;
        }

        public void setDuration (String duration) {
            this.duration = duration;
        }

        public String getIpAddress () {
            return ipAddress;
        }

        public void setIpAddress (String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }

    public void setBsUser (BSUser bsUser) {
        this.bsUser = bsUser;
    }
}
