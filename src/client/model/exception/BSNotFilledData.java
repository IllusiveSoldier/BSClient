package client.model.exception;

/**
 * Исключение, которое вызывается, когда не заполнена какая-то информация в полях ввода и т.д.
 */
public class BSNotFilledData extends Exception {
    public static final String DEFAULT_MESSAGE = "Не заполнены данные";

    public BSNotFilledData(Throwable throwable) {
        super(throwable);
    }

    public BSNotFilledData(String exceptionMessage) {
        super(exceptionMessage);
    }
}
