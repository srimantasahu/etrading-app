package com.etrading.client_connector.subscriber;

import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.Message;
import com.crankuptheamps.client.exception.AMPSException;
import com.etrading.client_connector.parser.FixParser;
import com.etrading.messages.ClientFix.QuoteRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientMsgSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(ClientMsgSubscriber.class);

    @Value("${amps.client.url}")
    private String ampsUrl;

    @Value("${fix.subscribe.topic}")
    private String fixTopic;

    @Value("${oms.publish.topic}")
    private String omsTopic;

    @Value("${amps.client.name:ClientConnector}") // Optional override
    private String clientName;

    private Client client;

    @PostConstruct
    public void init() {
        try {
            client = new Client(clientName);
            client.connect(ampsUrl);
            client.logon();

            client.subscribe(this::handleMessage, fixTopic, 0);
            logger.info("Subscribed to {}", fixTopic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect or subscribe to AMPS", e);
        }
    }

    private void handleMessage(Message message) {
        try {
            String fix = message.getData();
            Map<String, String> parsed = FixParser.parse(fix);

            QuoteRequest proto = QuoteRequest.newBuilder()
                    .setRequestId(parsed.getOrDefault("131", ""))
                    .setSymbol(parsed.getOrDefault("55", ""))
                    .setSender(parsed.getOrDefault("49", ""))
                    .setTarget(parsed.getOrDefault("56", ""))
                    .setTimestamp(parsed.getOrDefault("52", ""))
                    .build();

            byte[] dataBytes = proto.toByteArray();
            byte[] topicBytes = omsTopic.getBytes();

            client.publish(topicBytes, 0, topicBytes.length, dataBytes, 0, dataBytes.length);

            logger.info("Published to {}: {}", omsTopic, proto);
        } catch (AMPSException e) {
            logger.error("Failed to publish message to AMPS", e);
        } catch (Exception e) {
            logger.error("Error in message handling", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            logger.info("Shutting down AMPS client.");
            client.close();
        } catch (Exception e) {
            logger.warn("Error closing AMPS client", e);
        }
    }
}