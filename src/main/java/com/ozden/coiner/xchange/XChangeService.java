package com.ozden.coiner.xchange;

import info.bitrich.xchangestream.bitstamp.BitstampStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
@Service
public class XChangeService {

    private StreamingExchange exchange;

    @PostConstruct
    public void init() {
        exchange = createStreamingExchangeConnection(BitstampStreamingExchange.class.getName());
    }

    @PreDestroy
    private void disconnect() {
        exchange.disconnect().subscribe(() -> log.info("Disconnected from the Exchange"));
    }

    private StreamingExchange createStreamingExchangeConnection(String serviceClassName) {
        StreamingExchange streamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(serviceClassName);
        streamingExchange.connect().blockingAwait();
        return streamingExchange;
    }

    public Observable<Trade> getTrades(String currencyPairText) {
        return exchange.getStreamingMarketDataService()
                .getTrades(XChangeUtils.getCurrencyPairFromText(currencyPairText));
    }
}
