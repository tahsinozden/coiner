package com.ozden.coiner.subscription;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "SUBSCRIPTIONS")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Subscription implements Serializable {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    private String userName;

    @Column
    private String currencyPair;

    @Column
    private BigDecimal threshold;

    public Subscription(String userName, String currencyPair, BigDecimal threshold) {
        this.userName = userName;
        this.currencyPair = currencyPair;
        this.threshold = threshold;
    }
}
