package com.ozden.coiner.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private JacksonTester<List<Subscription>> jsonSubscriptions;

    @Before
    public void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    public void shouldCreateSubscriptionsWhenValidInputsProvided() throws Exception {
        // given
        Subscription subscription = new Subscription("user", "BTC-USD", BigDecimal.valueOf(3000));
        given(subscriptionService.saveSubscription("user", "BTC-USD", BigDecimal.valueOf(3000)))
                .willReturn(subscription);

        // when
        MockHttpServletResponse response = mockMvc.perform(
                put("/alert?pair=BTC-USD&limit=3000").with(request -> {
                    request.setRemoteUser("user");
                    request.setUserPrincipal((UserPrincipal) () -> "user");
                    return request;
                }).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void shouldRemoveSubscriptionsWhenValidInputsProvided() throws Exception {
        // given
        Subscription subscription = new Subscription("user", "BTC-USD", BigDecimal.valueOf(3000));
        given(subscriptionService.removeSubscription("user", "BTC-USD"))
                .willReturn(Arrays.asList(subscription));

        // when
        MockHttpServletResponse response = mockMvc.perform(
                delete("/alert?pair=BTC-USD").with(request -> {
                    request.setRemoteUser("user");
                    request.setUserPrincipal((UserPrincipal) () -> "user");
                    return request;
                }).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void shouldNotThrowExceptionWhenInvalidInputsProvided() throws Exception {
        // given nothing

        // when
        MockHttpServletResponse response = mockMvc.perform(
                delete("/alert?pair=BTCUSD").with(request -> {
                    request.setRemoteUser("user");
                    request.setUserPrincipal((UserPrincipal) () -> "user");
                    return request;
                }).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void shouldGetUserUserSubscriptions() throws Exception {
        // given
        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription = new Subscription("user", "BTC-USD", BigDecimal.valueOf(3000));
        subscriptions.add(subscription);
        subscription = new Subscription("user", "BTC-PLN", BigDecimal.valueOf(3000));
        subscriptions.add(subscription);

        given(subscriptionService.getUserSubscriptionsByUserName("user")).willReturn(subscriptions);

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/subscriptions").with(request -> {
                    request.setRemoteUser("user");
                    request.setUserPrincipal((UserPrincipal) () -> "user");
                    return request;
                }).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonSubscriptions.write(subscriptions).getJson());
    }

    @Test
    public void shouldGetEmptyListWhenUserHasNoSubscriptions() throws Exception {
        // given
        given(subscriptionService.getUserSubscriptionsByUserName("user")).willReturn(Collections.emptyList());

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/subscriptions").with(request -> {
                    request.setRemoteUser("user");
                    request.setUserPrincipal((UserPrincipal) () -> "user");
                    return request;
                }).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonSubscriptions.write(Collections.emptyList()).getJson());
    }

    @Test
    public void getAllCurrencyPairs() {
    }
}