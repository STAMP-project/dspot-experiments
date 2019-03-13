package org.mockserver.serialization;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;

import static org.mockserver.model.PortBinding.portBinding;


/**
 *
 *
 * @author jamesdbloom
 */
public class PortBindingSerializerIntegrationTest {
    @Test
    public void shouldIgnoreExtraFields() throws IOException {
        // given
        String requestBytes = ((((((((((((("{" + (NEW_LINE)) + "    \"ports\": [") + (NEW_LINE)) + "        0,") + (NEW_LINE)) + "        1080,") + (NEW_LINE)) + "        0") + (NEW_LINE)) + "    ],") + (NEW_LINE)) + "    \"extra_field\": \"extra_value\"") + (NEW_LINE)) + "}";
        // when
        PortBinding portBinding = deserialize(requestBytes);
        // then
        Assert.assertEquals(org.mockserver.model.PortBinding.portBinding(0, 1080, 0), portBinding);
    }

    @Test
    public void shouldDeserializeCompleteObject() throws IOException {
        // given
        String requestBytes = ((((((((((("{" + (NEW_LINE)) + "    \"ports\": [") + (NEW_LINE)) + "        0,") + (NEW_LINE)) + "        1080,") + (NEW_LINE)) + "        0") + (NEW_LINE)) + "    ]") + (NEW_LINE)) + "}";
        // when
        PortBinding portBinding = deserialize(requestBytes);
        // then
        Assert.assertEquals(org.mockserver.model.PortBinding.portBinding(0, 1080, 0), portBinding);
    }

    @Test
    public void shouldDeserializePartialObject() throws IOException {
        // given
        String requestBytes = "{ }";
        // when
        PortBinding portBinding = deserialize(requestBytes);
        // then
        Assert.assertEquals(portBinding(), portBinding);
    }

    @Test
    public void shouldSerializeCompleteObject() throws IOException {
        // when
        String jsonPortBinding = new PortBindingSerializer(new MockServerLogger()).serialize(new PortBinding().setPorts(Arrays.asList(0, 1080, 0)));
        // then
        Assert.assertEquals((((("{" + (NEW_LINE)) + "  \"ports\" : [ 0, 1080, 0 ]") + (NEW_LINE)) + "}"), jsonPortBinding);
    }
}

