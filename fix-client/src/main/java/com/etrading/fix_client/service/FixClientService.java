package com.etrading.fix_client.service;

import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.exception.AMPSException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FixClientService {
    private static final Logger logger = LoggerFactory.getLogger(FixClientService.class);
    private static final String SOH = "\u0001";

    @Value("${amps.client.url}")
    private String ampsUrl;

    @Value("${fix.publish.topic}")
    private String fixTopic;

    @Value("${amps.client.name:FixClient}") // Optional override
    private String clientName;

    private Client client;

    @PostConstruct
    public void init() {
        try {
            client = new Client(clientName);
            client.connect(ampsUrl);
            client.logon();
            logger.info("Successfully connected to AMPS client.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to AMPS", e);
        }
    }

    public String sendMessage(String fixMessage) {
        if (fixMessage == null || fixMessage.trim().isEmpty()) {
            logger.warn("Empty FIX message provided. Aborting publish.");
            return "ERROR: Empty FIX message";
        }

        logger.info("Preparing to send FIX message to topic [{}]", fixTopic);

        try {
            String formattedFixMessage = fixMessage.replace("|", SOH);
            client.publish(fixTopic, formattedFixMessage);

            logger.info("Successfully published FIX message to [{}]", fixTopic);
            return "SUCCESS";
        } catch (AMPSException e) {
            logger.error("AMPS publish failed", e);
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error while sending FIX message", e);
            return "ERROR: " + e.getMessage();
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
