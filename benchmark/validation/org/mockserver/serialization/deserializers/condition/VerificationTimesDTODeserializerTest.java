package org.mockserver.serialization.deserializers.condition;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.VerificationTimesDTO;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationTimesDTODeserializerTest {
    @Test
    public void shouldDeserializeAtLeastVerificationFormat() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"exact\":false,\"count\":3}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(3, (-1)))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"count\":3,\"exact\":false}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(3, (-1)))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"count\":3,\"exact\":true}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(3, 3))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"count\":3}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(3, (-1)))));
    }

    @Test
    public void shouldDeserializeBetweenVerificationFormat() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"atLeast\":1,\"atMost\":2}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(1, 2))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"atLeast\":1}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between(1, (-1)))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"atMost\":2}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between((-1), 2))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"atMost\":2,\"exact\":true}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between((-1), 2))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"atMost\":2,\"count\":3}", VerificationTimesDTO.class), Is.is(new VerificationTimesDTO(VerificationTimes.between((-1), 2))));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{}", VerificationTimesDTO.class), Is.is(CoreMatchers.nullValue()));
    }
}

