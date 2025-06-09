package com.etrading.oms.subscriber;

import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.Message;
import com.crankuptheamps.client.exception.AMPSException;
import com.etrading.messages.Fix.QuoteRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderRouter {
    private static final Logger logger = LoggerFactory.getLogger(OrderRouter.class);

    @Value("${amps.client.url}")
    private String ampsUrl;

    @Value("${oms.subscribe.topic}")
    private String omsInTopic;

    @Value("${oms.publish.us.topic}")
    private String omsOutUsTopic;

    @Value("${oms.publish.emea.topic}")
    private String omsOutEmeaTopic;

    @Value("${amps.client.name:OMS}") // Optional override
    private String clientName;

    private Client client;

    @PostConstruct
    public void init() {
        try {
            client = new Client(clientName);
            client.connect(ampsUrl);
            client.logon();

            client.subscribe(this::handleMessage, omsInTopic, 0);
            logger.info("Subscribed to {}", omsInTopic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect or subscribe to AMPS", e);
        }
    }

    private void handleMessage(Message message) {
        try {
            QuoteRequest qr = null;
            try {
                qr = QuoteRequest.parseFrom(message.getData().getBytes());
            } catch (InvalidProtocolBufferException e) {
                logger.error("Failed to parse message: {}", message.getData(), e);
                return;
            }

            byte[] dataBytes = qr.toByteArray();

            String topic = qr.getSymbol().startsWith("US") ? omsOutUsTopic : omsOutEmeaTopic;
            byte[] topicBytes = topic.getBytes();  // default charset UTF-8

            client.publish(topicBytes, 0, topicBytes.length, dataBytes, 0, dataBytes.length);

            logger.info("Published to {}: {}", topic, qr);
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