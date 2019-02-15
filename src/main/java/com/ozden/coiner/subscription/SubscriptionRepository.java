package com.ozden.coiner.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserName(String userName);

    List<Subscription> findByUserNameAndCurrencyPair(String userName, String currencyPair);
}
