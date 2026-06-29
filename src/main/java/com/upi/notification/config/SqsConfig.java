package com.upi.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import org.springframework.messaging.Message;
import java.util.Collection;

@Configuration
@Slf4j
public class SqsConfig {

    @Value("${spring.cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        if (accessKey == null || accessKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            log.info("AWS Credentials not fully configured. SqsAsyncClient will use the default credentials provider chain.");
            return SqsAsyncClient.builder()
                    .region(Region.of(region))
                    .build();
        }
        return SqsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .acknowledgementMode(AcknowledgementMode.ON_SUCCESS) // Deletes message from SQS ONLY on successful processing
                )
                .errorHandler(new io.awspring.cloud.sqs.listener.errorhandler.ErrorHandler<Object>() {
                    @Override
                    public void handle(Message<Object> message, Throwable t) {
                        log.error("❌ Error occurred while processing SQS message. Message ID: {}. Error: {}", 
                                message.getHeaders().getId(), 
                                t.getMessage());
                    }

                    @Override
                    public void handle(Collection<Message<Object>> messages, Throwable t) {
                        log.error("❌ Error occurred while processing batch SQS messages. Batch size: {}. Error: {}", 
                                messages.size(), 
                                t.getMessage());
                    }
                })
                .build();
    }
}
