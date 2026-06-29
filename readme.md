# Notification Service

A Java & Spring Boot microservice simulating the asynchronous, event-driven notification dispatch layer of India's UPI payments infrastructure. It consumes payment completion events from **AWS SQS** queues, processes templates, and sends mock SMS/push notifications. It handles failed notifications gracefully using a **Dead Letter Queue (DLQ)**.

---

## 🛠️ Tech Stack & Ports
* **Language:** Java 21+
* **Framework:** Spring Boot 3.x (with Spring Cloud AWS SQS starter)
* **Message Broker:** AWS SQS (Simple Queue Service)
* **Default Port:** `3004`

---

## 📝 Detailed Step-by-Step Implementation Plan

### Phase 1: Project Setup & AWS SQS Integration Configuration
* Goal: Bootstrap the Spring Boot service and configure AWS SQS connections.
1. **Initialize Project Directory:** Create the standard Spring Boot directory structure inside `notification-service`.
2. **Configure Dependencies (`build.gradle.kts`):**
   - Spring Boot Web (basic health checks and endpoints)
   - Spring Cloud AWS SQS Starter (`io.awspring.cloud:spring-cloud-aws-starter-sqs:3.1.x` or compatible)
   - Lombok & Validation
3. **App Properties (`application.yaml`):** Configure service port `3004`. Set up SQS queue settings:
   - AWS region (e.g., `ap-south-1` or `us-east-1`).
   - SQS Queue names (`payment-notifications-queue` and `payment-notifications-dlq`).
   - Credentials (configured via environment variables: `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`).

---

### Phase 2: SQS Message Consumer (`@SqsListener`)
* Goal: Create the listener engine that automatically polls and processes incoming SQS events.
1. **Define Message Payload DTO:** Create `PaymentEvent.java` mapping incoming payment events:
   - `transactionId` (UUID)
   - `payerVpa` (String)
   - `payeeVpa` (String)
   - `amountPaise` (Long)
   - `status` (SUCCESS, FAILED, REVERSED)
   - `payerRrn` (String, optional)
   - `payeeRrn` (String, optional)
   - `failureReason` (String, optional)
2. **Implement `@SqsListener`:** Build `PaymentNotificationListener.java` to automatically receive messages from the SQS queue, deserialize JSON to `PaymentEvent`, and hand them to the dispatcher.

---

### Phase 3: Mock Notification Dispatcher
* Goal: Format and log notifications simulating SMS/Push alerts.
1. **Notification Templates:** Implement a formatter class to generate friendly messages:
   - **For Payer (Debit):** *"Dear Rahul, ₹1,000.00 has been debited from your account to Mina. RRN: RRN123..."*
   - **For Payee (Credit):** *"Dear Mina, ₹1,000.00 has been credited to your account from Rahul. RRN: RRN123..."*
   - **For Reversal (Refund):** *"Dear Rahul, ₹1,000.00 has been credited back to your account due to credit timeout. RRN: RRN123..."*
2. **Log Dispatchers:** Output simulated SMS notifications in the console logs.

---

### Phase 4: Retry Policies and DLQ (Dead Letter Queue) Handling
* Goal: Handle message delivery failures gracefully without losing data.
1. **Failure Simulation Mode:** Implement a mock failure selector to simulate message processing errors (e.g. throwing `RuntimeException` for specific VPAs).
2. **Spring SQS Retry Configuration:** Configure custom retry count (max 3 times) and backoff intervals.
3. **DLQ Routing:** Configure the dead letter queue destination (`payment-notifications-dlq`) in Spring Boot to store permanently failed notifications for manual audit.
