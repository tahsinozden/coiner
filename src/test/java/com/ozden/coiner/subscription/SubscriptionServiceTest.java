package com.ozden.coiner.subscription;

import com.ozden.coiner.data.DataService;
import com.ozden.coiner.xchange.XChangeService;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private XChangeService xChangeService;

    @Mock
    private DataService dataService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Captor
    private ArgumentCaptor<Subscription> subscriptionCaptor;

    @Test
    public void shouldSaveSubscriptionWhenNotExistInDatabase() {
        when(subscriptionRepository.findByUserNameAndCurrencyPair("userName", "BTC-USD"))
                .thenReturn(Collections.emptyList());
        subscriptionService.saveSubscription("userName", "BTC-USD", BigDecimal.ONE);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    public void shouldSaveSubscriptionWhenExistsInDatabase() {
        when(subscriptionRepository.findByUserNameAndCurrencyPair("userName", "BTC-USD"))
                .thenReturn(Arrays.asList(new Subscription("userName", "BTC-USD", BigDecimal.TEN)));
        subscriptionService.saveSubscription("userName", "BTC-USD", BigDecimal.ONE);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());
        assertThat(subscriptionCaptor.getValue().getThreshold()).isEqualTo(BigDecimal.ONE);
    }


    @Test
    public void shouldRemoveSubscription() {
        when(subscriptionRepository.findByUserNameAndCurrencyPair("userName", "BTC-USD"))
                .thenReturn(Arrays.asList(new Subscription("userName", "BTC-USD", BigDecimal.TEN)));
        subscriptionService.removeSubscription("userName", "BTC-USD");
        verify(subscriptionRepository).delete(any(Subscription.class));
    }

    @Test
    public void shouldSubscribeWhenNoSubscriptionsDoneBefore() {
        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription = new Subscription("user1", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user2", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user3", "BTC-PLN", BigDecimal.TEN);
        subscriptions.add(subscription);
        subscription = new Subscription("user4", "BTC-PLN", BigDecimal.ONE);
        subscriptions.add(subscription);

        when(subscriptionRepository.findAll()).thenReturn(subscriptions);
        when(xChangeService.getTrades("BTC-TRY")).thenReturn(Observable.create(subscriber -> {}));

        subscription = new Subscription("user1", "BTC-TRY", BigDecimal.ONE);
        subscriptionService.subscribe(subscription);

        verify(xChangeService).getTrades("BTC-TRY");
    }

    @Test
    public void shouldNotSubscribeWhenSubscriptionsExists() {
        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription = new Subscription("user1", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user2", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user3", "BTC-PLN", BigDecimal.TEN);
        subscriptions.add(subscription);

        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        subscription = new Subscription("user1", "BTC-PLN", BigDecimal.ONE);
        subscriptionService.subscribe(subscription);

        verify(xChangeService, never()).getTrades(anyString());
    }

    @Test
    public void shouldUnsubscribe() {
        Subscription subscription = new Subscription("user1", "BTC-PLN", BigDecimal.ONE);
        when(xChangeService.getTrades("BTC-PLN")).thenReturn(Observable.create(subscriber -> {}));

        subscriptionService.unsubscribe(Arrays.asList(subscription));

        verify(dataService).removeSubscription(subscription);
        verify(xChangeService).getTrades("BTC-PLN");
    }

    @Test
    public void shouldGetUserSubscriptionsByUserName() {
        subscriptionService.getUserSubscriptionsByUserName("userName");
        verify(subscriptionRepository).findByUserName("userName");
    }

    @Test
    public void shouldGetGroupedUsersByCurrencyPair() {
        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription = new Subscription("user1", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user2", "BTC-USD", BigDecimal.ONE);
        subscriptions.add(subscription);
        subscription = new Subscription("user3", "BTC-PLN", BigDecimal.TEN);
        subscriptions.add(subscription);
        subscription = new Subscription("user4", "BTC-PLN", BigDecimal.ONE);
        subscriptions.add(subscription);

        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        Map<String, List<Subscription>> actual = subscriptionService.getGroupedUsersByCurrencyPair();

        verify(subscriptionRepository).findAll();
        assertThat(actual).hasSize(2);
        assertThat(actual.get("BTC-USD")).hasSize(2);
        assertThat(actual.get("BTC-PLN")).hasSize(2);
    }
}