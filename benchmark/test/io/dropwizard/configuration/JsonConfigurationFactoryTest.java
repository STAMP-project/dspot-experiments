package io.dropwizard.configuration;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;


public class JsonConfigurationFactoryTest extends BaseConfigurationFactoryTest {
    private File commentFile;

    @Test
    public void defaultJsonFactoryFailsOnComment() {
        assertThatThrownBy(() -> factory.build(commentFile)).hasMessageContaining(String.format(("%s has an error:%n" + "  * Malformed JSON at line: 4, column: 4; Unexpected character ('/' (code 47)): maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)"), commentFile.getName()));
    }

    @Test
    public void configuredMapperAllowsComment() throws ConfigurationException, IOException {
        ObjectMapper mapper = Jackson.newObjectMapper().configure(ALLOW_COMMENTS, true);
        JsonConfigurationFactory<BaseConfigurationFactoryTest.Example> factory = new JsonConfigurationFactory(BaseConfigurationFactoryTest.Example.class, validator, mapper, "dw");
        factory.build(commentFile);
    }
}

