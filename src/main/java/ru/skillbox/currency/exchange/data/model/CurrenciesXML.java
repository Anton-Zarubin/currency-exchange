package ru.skillbox.currency.exchange.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrenciesXML {
    @XmlElement(name = "Valute")
    private List<CurrencyXML> currencies;
}