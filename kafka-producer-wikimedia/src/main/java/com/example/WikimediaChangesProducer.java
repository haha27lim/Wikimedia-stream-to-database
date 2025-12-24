package com.example;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.HttpConnectStrategy;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaChangesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public WikimediaChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage() throws InterruptedException {

        String topic = "wikimedia_recentchange";
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        LOGGER.info("Connecting to {}", url);

        // to read real time stream data from wikimedia, we use event source
        // 1) Create SSE event handler
        BackgroundEventHandler eventHandler = new WikimediaChangesHandler(kafkaTemplate, topic);

        HttpConnectStrategy connectStrategy = HttpConnectStrategy.http(URI.create(url))
                .header("Accept", "text/event-stream")
                .header("User-Agent", "springboot-kafka-wikimedia/1.0");

        // 2) Build EventSource with connect strategy
        EventSource.Builder esBuilder = new EventSource.Builder(connectStrategy);

        // 3) Build BackgroundEventSource
        BackgroundEventSource eventSource = new BackgroundEventSource.Builder(eventHandler, esBuilder).build();

        // 4) Start and keep app alive
        eventSource.start();
        LOGGER.info("SSE started; waiting for events...");

        // Keep the app alive for 10min
        TimeUnit.MINUTES.sleep(10);
        LOGGER.info("Closing SSE...");
        eventSource.close();
    }
}
