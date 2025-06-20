package com.example.camelteste.camel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.google.pubsub.GooglePubsubConstants;
import org.apache.camel.component.google.pubsub.consumer.GooglePubsubAcknowledge;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@Component
public class CamelRouter extends RouteBuilder {

    @Value("${google.pubsub.subscription}")
    private String pubsubSubscription;

    @Value("${api.jogador.url}")
    private String jogadorApiUrl;

    @Override
    public void configure() throws Exception {

        from("google-pubsub://" + pubsubSubscription + "?ackMode=NONE")
                .threads(5)
                .process(exchange -> {
                    GooglePubsubAcknowledge acknowledge = exchange.getIn().getHeader(GooglePubsubConstants.GOOGLE_PUBSUB_ACKNOWLEDGE, GooglePubsubAcknowledge.class);
                    acknowledge.ack(exchange);
                })
                .routeId("mensagem")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
                .toD(jogadorApiUrl)
                .log("<<< Mensagem recebida ${body} ${headers}")
                .end();
    }
}