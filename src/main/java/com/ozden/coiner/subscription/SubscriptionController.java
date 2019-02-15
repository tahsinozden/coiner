package com.ozden.coiner.subscription;

import com.ozden.coiner.xchange.XChangeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PutMapping("/alert")
    public void createSubscriptions(@QueryParam("pair") String pair, @QueryParam("limit") BigDecimal limit,
                                    HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String userName = principal.getName();
        Subscription subscription = subscriptionService.saveSubscription(userName, pair, limit);
        subscriptionService.subscribe(subscription);
    }

    @DeleteMapping("/alert")
    public void removeSubscriptions(@QueryParam("pair") String pair, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String userName = principal.getName();
        List<Subscription> subscriptions = subscriptionService.removeSubscription(userName, pair);
        subscriptionService.unsubscribe(subscriptions);
    }

    @GetMapping("/subscriptions")
    public List<Subscription> getUserUserSubscriptions(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String userName = principal.getName();
        return subscriptionService.getUserSubscriptionsByUserName(userName);
    }

    @GetMapping("/currency-pairs")
    public List<String> getAllCurrencyPairs() {
        return XChangeUtils.CURRENCY_PAIRS;
    }
}
