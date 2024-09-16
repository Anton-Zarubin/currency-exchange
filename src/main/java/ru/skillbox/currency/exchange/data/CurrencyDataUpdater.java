package ru.skillbox.currency.exchange.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.currency.exchange.data.model.CurrenciesXML;
import ru.skillbox.currency.exchange.data.model.CurrencyXML;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyDataUpdater {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper mapper;
    @Value("${cbr_currency_market_url}")
    String cbrURL;

    @Scheduled(fixedDelayString = "${get_cbr_info_delay_millis}", initialDelay = 5000)
    private void updateData() {
        log.info("Extracting data from The Bank of Russia webpage!");

        List<CurrencyXML> currenciesList = getCbrCurrencyMarketData(getURL(cbrURL));
        if (currenciesList.isEmpty()) return;

        log.info("Data received.");

        Map<String,CurrencyXML> currenciesMap = currenciesList.stream()
                .collect(Collectors.toMap(CurrencyXML :: getCharCode, currencyXML -> currencyXML));

        for (String key : currenciesMap.keySet()) {
            Currency existingCurrency = currencyRepository.findByIsoCharCodeOrNumCode(key,currenciesMap.get(key).getNumCode());
            Currency currency = mapper.convertXMLToEntity(currenciesMap.get(key));
            if(existingCurrency != null) currency.setId(existingCurrency.getId());
            currencyRepository.save(currency);
        }

        log.info("Database updated.");
    }

    private List<CurrencyXML> getCbrCurrencyMarketData(URL url) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CurrenciesXML.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            CurrenciesXML currenciesXML = (CurrenciesXML) jaxbUnmarshaller.unmarshal(url);
            return currenciesXML.getCurrencies();
        } catch (Exception e) {
            log.error("Unable to retrieve data! Check URL and structure of XML document", e);
            return Collections.emptyList();
        }
    }

    private URL getURL(String url) {
        try {
            return new URI(url).toURL();
        } catch (Exception e) {
            log.error("Invalid URI. Check configuration properties.", e);
            return null;
        }
    }
}