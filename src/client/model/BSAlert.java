package client.model;

import javafx.scene.control.Alert;

public class BSAlert {
    private static final String ALERT_TITLE = "BS Client alert";
    private static final String ERROR_CAUSE_MESSAGE = "Произошла ошибка :(";
    private static final String DEFAULT_ERROR_MESSAGE =
            "В системе произошла ошибка. Если ошибка осталась сообщите администраторам о ней";

    public static void showAlert(final String title, final String headerText,
                                 final String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void showAlert () {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ALERT_TITLE);
        alert.setHeaderText(ERROR_CAUSE_MESSAGE);
        alert.setContentText(DEFAULT_ERROR_MESSAGE);
        alert.showAndWait();
    }
}
