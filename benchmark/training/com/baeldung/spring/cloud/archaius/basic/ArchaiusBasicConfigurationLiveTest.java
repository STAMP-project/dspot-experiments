package com.baeldung.spring.cloud.archaius.basic;


import SpringBootTest.WebEnvironment;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ArchaiusBasicConfigurationLiveTest {
    private static final String BASE_URL = "http://localhost:8080";

    private static final String DYNAMIC_PROPERTIES_URL = "/properties-from-dynamic";

    private static final Map<String, String> EXPECTED_ARCHAIUS_PROPERTIES = ArchaiusBasicConfigurationLiveTest.createExpectedArchaiusProperties();

    private static final String VALUE_PROPERTIES_URL = "/properties-from-value";

    private static final Map<String, String> EXPECTED_VALUE_PROPERTIES = ArchaiusBasicConfigurationLiveTest.createExpectedValueProperties();

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    private TestRestTemplate template;

    @Test
    public void givenDefaultConfigurationSetup_whenRequestProperties_thenEndpointRetrievesValuesInFiles() {
        Map<String, String> initialResponse = this.exchangeAsMap(((ArchaiusBasicConfigurationLiveTest.BASE_URL) + (ArchaiusBasicConfigurationLiveTest.DYNAMIC_PROPERTIES_URL)), new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {});
        assertThat(initialResponse).containsAllEntriesOf(ArchaiusBasicConfigurationLiveTest.EXPECTED_ARCHAIUS_PROPERTIES);
    }

    @Test
    public void givenNonDefaultConfigurationFilesSetup_whenRequestSpringVisibleProperties_thenEndpointDoesntRetrieveArchaiusProperties() {
        Map<String, String> initialResponse = this.exchangeAsMap(((ArchaiusBasicConfigurationLiveTest.BASE_URL) + (ArchaiusBasicConfigurationLiveTest.VALUE_PROPERTIES_URL)), new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {});
        assertThat(initialResponse).containsAllEntriesOf(ArchaiusBasicConfigurationLiveTest.EXPECTED_VALUE_PROPERTIES);
    }
}

