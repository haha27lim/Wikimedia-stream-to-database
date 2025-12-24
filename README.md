# Kafka - Wikimedia stream to database

# Create Multi Module project in Spring Boot
1) Add '<packaging>pom</packaging>' on the parent pom.xml
2) Create new module in parent folder
3) Delete Main.java in new module
4) Review the new module reflect on the parent pom.xml
5) Create SpringBootApplication in new module
6) Test to run 'mvn clean install' whether successfully build
7) Add '<packaging>jar</packaging>' on new module

# Project Overview
- Stream live event data from Wikimedia (Server-Sent Events (SSE) endpoint)
- Publishes those events to Kafka (Producer)
- Consumes the events from Kafka (Consumer)
- Stores parsed data into MySQL

# Project Setup
Create 2 microservices
- Kafka-producer-wikimedia
- Kafka-consumer-database

# Wikimedia SSE Stream
URL = https://stream.wikimedia.org/v2/stream/recentchange
This is a Server-Sent Events (SSE) endpoint provided by Wikimedia.
It continuously streams JSON events whenever a change happens on Wikipedia or related projects — e.g., page edits, new pages, user changes.

# Kafka Producer - Read Wikimedia and Produce to Topic
The producer connects to the Wikimedia SSE stream, parses each event, and writes it into a Kafka topic.

Steps:
1. Connect to SSE endpoint
2. Parse JSON from the stream
3. Send each event as a message to Kafka

# Kafka Topic
Define a Kafka topic "wikimedia_recentchange" to store the events.
This topic acts as a buffer and durable store for downstream consumption.

# Kafka consumer - Read from Topic
The consumer reads messages from the Kafka topic and processes them — e.g., transforming them before saving to a database.

# MySQL - Persist Events
At the end of the pipeline, consumer inserts records into MySQL.
