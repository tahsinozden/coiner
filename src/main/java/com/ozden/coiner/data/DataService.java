package com.ozden.coiner.data;

import com.ozden.coiner.subscription.Subscription;
import com.ozden.coiner.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// it is crucial to have an in memory database so that we don't have to query physical database each time,
// this will give more performance but it real world, real in memory database has to be used.
@Service
public class DataService {

    // in real world application Redis can be used instead.
    private Map<String, List<Subscription>> IN_MEMORY_SUBSCRIPTIONS;

    @Autowired
    private SubscriptionService subscriptionService;

    @PostConstruct
    public void loadInitialData() {
        IN_MEMORY_SUBSCRIPTIONS = new ConcurrentHashMap<>(subscriptionService.getGroupedUsersByCurrencyPair());
    }

    public void addSubscription(Subscription subscription) {
        List<Subscription> subscriptions = IN_MEMORY_SUBSCRIPTIONS.computeIfAbsent(subscription.getCurrencyPair(), k -> new ArrayList<>());
        subscriptions.add(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        List<Subscription> subscriptions = IN_MEMORY_SUBSCRIPTIONS.get(subscription.getCurrencyPair());
        if (subscriptions == null) {
            return;
        }
        // in case of the user is only subscriber
        if (subscriptions.size() == 1) {
            IN_MEMORY_SUBSCRIPTIONS.remove(subscription.getCurrencyPair());
        } else {
            subscriptions.remove(subscription);
        }
    }

    public Map<String, List<Subscription>> getInMemorySubscriptions() {
        return new ConcurrentHashMap<>(IN_MEMORY_SUBSCRIPTIONS);
    }
}
