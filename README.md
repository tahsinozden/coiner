# coiner
a simple Cryptocurrency notification web application based on https://github.com/bitrich-info/xchange-stream

## how-to
- Build: `mvn install`
- Run: `mvn spring-boot:run`

## details
Technology stack :  Java 8, Spring Boot 2, Spring Security, Websocket, H2, JQuery, Bootstrap (please see pom.xml for the rest)

I used for https://github.com/bitrich-info/xchange-stream (based on XChange) because it supports streaming which give better performance and the application doesn't have to send a request at all. It works based on publisher & subscriber pattern.

I created some users to be used for login. Those can be found under Spring Security config. (for example, user name: user1, password: user1)

In the application, as seen below there are three parts.

- Subscribe for a notification : new subscriptions can be made with all supported currencies
- Your Subscriptions : your current subscriptions can be seen and can be unsubscribed.
- Subscription Alerts : alerts based on your subscriptions in real time using web sockets.

![img](https://github.com/tahsinozden/coiner/blob/master/img/main-page.png)
![img](https://github.com/tahsinozden/coiner/blob/master/img/login.png)
