package com.ozden.coiner.xchange;

import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


public class XChangeUtilsTest {

    @Test
    public void shouldGetCurrencyPairFromText() {
        CurrencyPair actual = XChangeUtils.getCurrencyPairFromText("BTC-USD");
        assertEquals(CurrencyPair.BTC_USD, actual);
    }

    @Test
    public void shouldGetCurrencyPairText() {
        String actual = XChangeUtils.getCurrencyPairText(CurrencyPair.BTC_USD);
        assertEquals("BTC-USD", actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvalidFormatProvided() {
        CurrencyPair actual = XChangeUtils.getCurrencyPairFromText("BTC_USD");
    }

    @Test
    public void shouldGetAllCurrencyPairs() {
        List<String> actual = XChangeUtils.getCurrencyPairs();
        assertThat(actual).hasSize(238);

        actual = actual.stream()
                .filter(s -> s.length() < 6 || s.length() > 10)
                .filter(s -> !s.contains("-"))
                .collect(Collectors.toList());
        assertThat(actual).isEmpty();
    }
}