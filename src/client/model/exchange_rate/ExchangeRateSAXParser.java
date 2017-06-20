package client.model.exchange_rate;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExchangeRateSAXParser extends DefaultHandler {
    private static final String VAL_CURS_BLOCK_NAME = "ValCurs";
    private static final String DATE_ATTRIBUTE_NAME = "Date";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUTE_BLOCK_NAME = "Valute";
    private static final String ID_ATTRIBUTE_NAME = "ID";
    private static final String NUM_CODE_BLOCK_NAME = "NumCode";
    private static final String CHAR_CODE_BLOCK_NAME = "CharCode";
    private static final String NOMINAL_BLOCK_NAME = "Nominal";
    private static final String NAME_BLOCK = "Name";
    private static final String VALUE_BLOCK_NAME = "Value";

    private String currentElement = "";
    private String currentValuteId = "";

    private ExchangeRateSuper exchangeRateSuper = new ExchangeRateSuper();

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes)
            throws SAXException
    {
        currentElement = qName;
        if (currentElement.equals(VAL_CURS_BLOCK_NAME)) {
            exchangeRateSuper.setDate(attributes.getValue(DATE_ATTRIBUTE_NAME));
            exchangeRateSuper.setMarketName(attributes.getValue(NAME_ATTRIBUTE));
        }
        if (currentElement.equals(VALUTE_BLOCK_NAME)) {
            ExchangeRate rate = new ExchangeRate();
            currentValuteId = attributes.getValue(ID_ATTRIBUTE_NAME);
            rate.setId(currentValuteId);

            exchangeRateSuper.putExchangeRate(currentValuteId, rate);
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        currentElement = "";
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {
        if (!currentValuteId.isEmpty()) {
                ExchangeRate rate = exchangeRateSuper.getExchangeRateById(currentValuteId);
        if (NUM_CODE_BLOCK_NAME.equals(currentElement))
            rate.setNumCode(String.valueOf(ch, start, length));
        if (CHAR_CODE_BLOCK_NAME.equals(currentElement))
            rate.setCharCode(String.valueOf(ch, start, length));
        if (NOMINAL_BLOCK_NAME.equals(currentElement))
            rate.setNominal(String.valueOf(ch, start, length));
        if (NAME_BLOCK.equals(currentElement))
            rate.setName(String.valueOf(ch, start, length));
        if (VALUE_BLOCK_NAME.equals(currentElement))
            rate.setValue(String.valueOf(ch, start, length));
        }
    }

    public ExchangeRateSuper getExchangeRateSuper () {
        return exchangeRateSuper;
    }
}
