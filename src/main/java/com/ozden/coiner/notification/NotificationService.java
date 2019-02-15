package com.ozden.coiner.notification;

import com.ozden.coiner.data.DataService;
import com.ozden.coiner.subscription.Subscription;
import com.ozden.coiner.subscription.SubscriptionService;
import com.ozden.coiner.xchange.XChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    private static final String WEBSOCKET_NOTIFICATION_URL = "/queue/notify";

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private DataService dataService;

    @Autowired
    private XChangeService xChangeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void subscribeCurrencyPairs() {
        // initially load all subscriptions from database
        Map<String, List<Subscription>> groupedUsersByCurrencyPair = subscriptionService.getGroupedUsersByCurrencyPair();
        // subscribe each currency pair
        groupedUsersByCurrencyPair.forEach((key, value) -> xChangeService.getTrades(key)
                .subscribe(trade -> {
                    // check user thresholds for each currency pair
                    // load it again in case of new subscriptions
                    Map<String, List<Subscription>> groupedSubscriptions = dataService.getInMemorySubscriptions();
                    groupedSubscriptions.values().stream()
                            .flatMap(Collection::stream)
                            .filter(s -> s.getThreshold().compareTo(trade.getPrice()) < 0)
                            .forEach(s -> {
                                Notification notification = new Notification(s.getUserName(), s.getCurrencyPair(),
                                        s.getThreshold(), trade.getPrice(), trade.getTimestamp());
                                log.info(notification.toString());
                                // send notification to user
                                notify(notification, s.getUserName());
                            });
                }));

    }

    public void notify(Notification notification, String username) {
        messagingTemplate.convertAndSendToUser(
                username,
                WEBSOCKET_NOTIFICATION_URL,
                notification
        );
    }
}
