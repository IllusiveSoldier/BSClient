package client.model.exchange_rate;

/**
 * Класс, содержащий информацию о конкретной валюте
 */
public class ExchangeRate {
    /**
     * Идентификатор валюты
     */
    private String id;
    /**
     * Числовой код валюты
     */
    private String numCode;
    /**
     * Символьный код валюты
     */
    private String charCode;
    /**
     * Номинал валюты
     */
    private String nominal;
    /**
     * Наименование валюты
     */
    private String name;
    /**
     * Стоимость в рублях относительно номинала
     */
    private String value;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getNumCode () {
        return numCode;
    }

    public void setNumCode (String numCode) {
        this.numCode = numCode;
    }

    public String getCharCode () {
        return charCode;
    }

    public void setCharCode (String charCode) {
        this.charCode = charCode;
    }

    public String getNominal () {
        return nominal;
    }

    public void setNominal (String nominal) {
        this.nominal = nominal;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
