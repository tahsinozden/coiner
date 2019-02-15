# coiner
a simple Cryptocurrency notification web application based on https://github.com/bitrich-info/xchange-stream

Technology stack :  Java 8, Spring Boot 2, Spring Security, Websocket, H2, JQuery(since it is just a single page), Bootstrap (please see pom.xml for the rest)

I used for https://github.com/bitrich-info/xchange-stream (based on XChange) because it supports streaming which give better performance and the application doesn't have to send a request at all. It works based on publisher & subscriber pattern.

I created some users to be used for login. Those can be found under Spring Security config. (for example, user name: user1, password: user1)

In the application, as seen below there are three parts.

1- Subscribe for a notification : new subscriptions can be made with all supported currencies
2- Your Subscriptions : your current subscriptions can be seen and can be unsubscribed.
3- Subscription Alerts : alerts based on your subscriptions in real time using web sockets.
