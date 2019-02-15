package com.ozden.coiner.xchange;

import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class XChangeUtils {

    private static final String DELIMITER = "-";

    public static final List<String> CURRENCY_PAIRS = getCurrencyPairs();

    private XChangeUtils() {
        throw new RuntimeException("cannot be initialized!");
    }

    public static CurrencyPair getCurrencyPairFromText(String text) {
        String[] pairs = StringUtils.split(text, DELIMITER);
        if (pairs.length < 2) {
            throw new IllegalArgumentException(text + " must be delimited by " + DELIMITER);
        }
        return new CurrencyPair(pairs[0], pairs[1]);
    }

    public static String getCurrencyPairText(CurrencyPair currencyPair) {
        return new StringJoiner(DELIMITER)
                .add(currencyPair.base.toString())
                .add(currencyPair.counter.toString())
                .toString();
    }

    // XChange service doesn't provide any API to get supported currencies,
    // Instead of manually inserting them to db, I used reflection to get pair names
    public static List<String> getCurrencyPairs() {
        Field[] fields = CurrencyPair.class.getFields();
        List<String> pairs = Arrays.stream(fields)
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .map(Field::toString)
                .map(s -> s.substring(s.lastIndexOf(".") + 1))
                .filter(s -> !"base".equals(s) && !"counter".equals(s))
                .map(s -> s.replace("_", "-"))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(pairs);
    }
}
