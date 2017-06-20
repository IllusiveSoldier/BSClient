package client.model.exchange_rate;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Класс, который позволяет манипулировать информацией о валютных курсах
 */
public class ExchangeRateSuper {
    /**
     * Дата для курсов
     */
    private String date;
    /**
     * Валютный рынок
     */
    private String marketName;
    /**
     * Доступные валюты
     */
    private HashMap<String, ExchangeRate> exchangeRates = new HashMap<String, ExchangeRate>();

    /**
     * Метод, который добавляет в список доступных валют объект
     * @param rate - объект с описание валюты
     */
    public void putExchangeRate(String id, ExchangeRate rate) {
        exchangeRates.put(id, rate);
    }

    public ExchangeRate getExchangeRateById(String id) {
        return exchangeRates.get(id);
    }

    public ExchangeRateSuper getExchangeSuper()
            throws SAXException, ParserConfigurationException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        ExchangeRateSAXParser exchangeRateSAXParser =
                new ExchangeRateSAXParser();

        parser.parse(
                getExchangeRateXml(),
                exchangeRateSAXParser
        );

        return exchangeRateSAXParser.getExchangeRateSuper();
    }

    private InputStream getExchangeRateXml() throws MalformedURLException, IOException {
        return new URL("http://www.cbr.ru/scripts/XML_daily.asp?").openStream();
    }

    public String getDate () {
        return date;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public String getMarketName () {
        return marketName;
    }

    public void setMarketName (String marketName) {
        this.marketName = marketName;
    }

    public HashMap<String, ExchangeRate> getExchangeRates () {
        return exchangeRates;
    }

    public void setExchangeRates (HashMap<String, ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}
