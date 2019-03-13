package org.mockserver.serialization.serializers.response;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.serialization.ObjectMapperFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimesSerializerTest {
    @Test
    public void shouldSerializeOnceTimes() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(once()), Is.is((((("{" + (NEW_LINE)) + "  \"remainingTimes\" : 1") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeExactlyTimes() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(exactly(1)), Is.is((((("{" + (NEW_LINE)) + "  \"remainingTimes\" : 1") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeUnlimitedTimes() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(unlimited()), Is.is((((("{" + (NEW_LINE)) + "  \"unlimited\" : true") + (NEW_LINE)) + "}")));
    }
}

