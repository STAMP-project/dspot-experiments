package org.apereo.cas.aws;


import com.amazonaws.client.builder.AwsClientBuilder;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.env.Environment;


/**
 * This is {@link AmazonEnvironmentAwareClientBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class AmazonEnvironmentAwareClientBuilderTests {
    static {
        System.setProperty("aws.accessKeyId", "AKIAIPPIGGUNIO74C63Z");
        System.setProperty("aws.secretKey", "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");
    }

    @Autowired
    private Environment environment;

    @Test
    public void verifyAction() {
        val builder = new AmazonEnvironmentAwareClientBuilder("aws", environment);
        val mock = Mockito.mock(AwsClientBuilder.class);
        Mockito.when(mock.build()).thenReturn(new Object());
        val client = builder.build(mock, Object.class);
        Assertions.assertNotNull(client);
        Assertions.assertNotNull(builder.getSetting("secretKey"));
        Assertions.assertNotNull(builder.getSetting("secretKey", String.class));
        Assertions.assertNotNull(builder.getSetting("something", "defaultValue"));
    }
}

