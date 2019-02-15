package com.ozden.coiner.notification;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {

    private String userName;
    private String currencyPair;
    private BigDecimal threshold;
    private BigDecimal currentValue;
    private Date timestamp;
}
