package io.github.isharedoc.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isharedoc.api.config.DynamoDbConfig;
import io.github.isharedoc.api.config.SqsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@ExtendWith(MockitoExtension.class)
class PresignedUrlServiceTest {

    @Mock
    private S3Presigner s3Presigner;
    @Mock
    private DynamoDbAsyncClient dynamoClient;
    @Mock
    private SqsAsyncClient sqsClient;
    @Mock
    private DynamoDbConfig dynamoDbConfig;
    @Mock
    private SqsConfig sqsConfig;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private PresignedUrlService service;

    @Test
    void generateUploadUrl() {
    }

    @Test
    void generateDownloadUrl() {
    }

    @Test
    void getFileMetadata() {
    }
}