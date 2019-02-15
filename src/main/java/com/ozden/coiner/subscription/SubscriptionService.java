package com.ozden.coiner.subscription;

import com.ozden.coiner.data.DataService;
import com.ozden.coiner.xchange.XChangeService;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private XChangeService xChangeService;

    @Autowired
    private DataService dataService;

    public Subscription saveSubscription(String userName, String currencyPair, BigDecimal threshold) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserNameAndCurrencyPair(userName, currencyPair);
        Subscription subscription;
        if (subscriptions.isEmpty()) {
            subscription = new Subscription(userName, currencyPair, threshold);
        } else {
            subscription = subscriptions.get(0);
            subscription.setThreshold(threshold);
        }
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> removeSubscription(String userName, String currencyPair) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserNameAndCurrencyPair(userName, currencyPair);
        subscriptions.forEach(s -> subscriptionRepository.delete(s));
        subscriptions.forEach(s -> dataService.removeSubscription(s));
        return subscriptions;
    }

    public void subscribe(Subscription subscription) {
        dataService.addSubscription(subscription);
        Map<String, List<Subscription>> groupedUsersByCurrencyPair = getGroupedUsersByCurrencyPair();
        if (groupedUsersByCurrencyPair.containsKey(subscription.getCurrencyPair())) {
            return;
        }

        xChangeService.getTrades(subscription.getCurrencyPair())
                .subscribe(trade -> {
                    List<Subscription> targetSubscriptions = groupedUsersByCurrencyPair.get(subscription.getCurrencyPair());
                    targetSubscriptions.stream()
                            .filter(s -> s.getThreshold().compareTo(trade.getPrice()) < 0)
                            .forEach(s -> {
                                log.info("limit reached : " + trade);
                            });
                });
    }

    public void unsubscribe(List<Subscription> subscriptions) {
        subscriptions.forEach(s -> dataService.removeSubscription(s));
        subscriptions.stream().forEach(subscription -> xChangeService.getTrades(subscription.getCurrencyPair())
                .unsubscribeOn(Schedulers.single()));

    }

    public List<Subscription> getUserSubscriptionsByUserName(String userName) {
        return subscriptionRepository.findByUserName(userName);
    }

    public Map<String, List<Subscription>> getGroupedUsersByCurrencyPair() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        Map<String, List<Subscription>> grouped = new HashMap<>();
        for (Subscription subscription : subscriptions) {
            String pair = subscription.getCurrencyPair();
            List<Subscription> subs = grouped.get(subscription.getCurrencyPair());
            if (subs == null) {
                subs = new ArrayList<>();
                grouped.put(pair, subs);
            }
            subs.add(subscription);
        }
        return grouped;
    }
}
