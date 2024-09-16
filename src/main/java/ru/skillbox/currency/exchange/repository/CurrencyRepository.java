package ru.skillbox.currency.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.currency.exchange.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Currency findByIsoNumCode(Long isoNumCode);

    @Query("SELECT c FROM Currency c WHERE c.isoCharCode = :charCode OR c.isoNumCode = :numCode")
    Currency findByIsoCharCodeOrNumCode(@Param("charCode") String charCode, @Param("numCode") Long numCode);
}
