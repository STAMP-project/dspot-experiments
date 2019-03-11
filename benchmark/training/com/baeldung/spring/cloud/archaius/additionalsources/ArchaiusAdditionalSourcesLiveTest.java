package com.baeldung.spring.cloud.archaius.additionalsources;


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
public class ArchaiusAdditionalSourcesLiveTest {
    private static final String BASE_URL = "http://localhost:8082";

    private static final String DYNAMIC_PROPERTIES_URL = "/properties-from-dynamic";

    private static final Map<String, String> EXPECTED_ARCHAIUS_PROPERTIES = ArchaiusAdditionalSourcesLiveTest.createExpectedArchaiusProperties();

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    private TestRestTemplate template;

    @Test
    public void givenNonDefaultConfigurationFilesSetup_whenRequestProperties_thenEndpointRetrievesValuesInFiles() {
        Map<String, String> initialResponse = this.exchangeAsMap(((ArchaiusAdditionalSourcesLiveTest.BASE_URL) + (ArchaiusAdditionalSourcesLiveTest.DYNAMIC_PROPERTIES_URL)), new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {});
        assertThat(initialResponse).containsAllEntriesOf(ArchaiusAdditionalSourcesLiveTest.EXPECTED_ARCHAIUS_PROPERTIES);
    }
}

