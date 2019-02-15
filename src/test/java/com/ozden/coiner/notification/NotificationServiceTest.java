package com.ozden.coiner.notification;

import com.ozden.coiner.data.DataService;
import com.ozden.coiner.subscription.Subscription;
import com.ozden.coiner.subscription.SubscriptionService;
import com.ozden.coiner.xchange.XChangeService;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private DataService dataService;

    @Mock
    private XChangeService xChangeService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;
    
    @Test
    public void shouldSubscribeCurrencyPairs() {
        Map<String, List<Subscription>> groupedPairs = new HashMap<>();
        groupedPairs.put("BTC-USD", Arrays.asList(new Subscription("name", "BTC-USD", BigDecimal.TEN)));
        groupedPairs.put("BTC-PLN", Arrays.asList(new Subscription("name", "BTC-PLN", BigDecimal.TEN)));
        groupedPairs.put("BTC-TRY", Arrays.asList(new Subscription("name", "BTC-TRY", BigDecimal.TEN)));

        when(subscriptionService.getGroupedUsersByCurrencyPair()).thenReturn(groupedPairs);
        when(xChangeService.getTrades("BTC-USD")).thenReturn(Observable.create(subscriber -> {}));
        when(xChangeService.getTrades("BTC-PLN")).thenReturn(Observable.create(subscriber -> {}));
        when(xChangeService.getTrades("BTC-TRY")).thenReturn(Observable.create(subscriber -> {}));

        notificationService.subscribeCurrencyPairs();

        verify(xChangeService, times(3)).getTrades(anyString());
    }

    @Test
    public void shouldNotifyUser() {
        Notification notification = new Notification();
        notificationService.notify(notification, "userName");
        verify(messagingTemplate).convertAndSendToUser("userName", "/queue/notify", notification);
    }
}