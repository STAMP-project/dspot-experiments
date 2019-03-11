package com.baeldung.spring.cloud.aws.ec2;


import com.amazonaws.services.ec2.AmazonEC2;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class EC2MetadataIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(EC2MetadataIntegrationTest.class);

    private boolean serverEc2;

    @Autowired
    private EC2Metadata eC2Metadata;

    @Autowired
    private AmazonEC2 amazonEC2;

    @Test
    public void whenEC2ClinentNotNull_thenSuccess() {
        assertThat(amazonEC2).isNotNull();
    }

    @Test
    public void whenEC2MetadataNotNull_thenSuccess() {
        assertThat(eC2Metadata).isNotNull();
    }

    @Test
    public void whenMetdataValuesNotNull_thenSuccess() {
        Assume.assumeTrue(serverEc2);
        assertThat(eC2Metadata.getAmiId()).isNotEqualTo("N/A");
        assertThat(eC2Metadata.getInstanceType()).isNotEqualTo("N/A");
    }

    @Test
    public void whenMetadataLogged_thenSuccess() {
        EC2MetadataIntegrationTest.logger.info("Environment is EC2: {}", serverEc2);
        EC2MetadataIntegrationTest.logger.info(eC2Metadata.toString());
    }
}

