package com.urbanairship.api.experiments.parse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.common.parse.APIParsingException;
import com.urbanairship.api.experiments.model.Variant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AmplVariantDeserializerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final ObjectMapper MAPPER = ExperimentObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testVariant_literalMutationString10_failAssert7() throws Exception {
        try {
            String variantString = "{" + ((((((("\"schedule\": {" + "") + "},") + "\"push\":{\"notification\":{\"alert\":\"Hello there\"}},") + "\"name\":\"A name\",") + "\"description\":\"A description\",") + "\"weight\":\"4\"") + "}");
            Variant variant = AmplVariantDeserializerTest.MAPPER.readValue(variantString, Variant.class);
            variant.getSchedule().isPresent();
            variant.getName().isPresent();
            variant.getDescription().isPresent();
            variant.getWeight().isPresent();
            variant.getVariantPushPayload().getNotification().isPresent();
            org.junit.Assert.fail("testVariant_literalMutationString10 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testVariant_literalMutationString15_failAssert12() throws Exception {
        try {
            String variantString = "{" + ((((((("\"schedule\": {" + "\"schedulqed_time\": \"2017-07-27T18:27:25.000Z\"") + "},") + "\"push\":{\"notification\":{\"alert\":\"Hello there\"}},") + "\"name\":\"A name\",") + "\"description\":\"A description\",") + "\"weight\":\"4\"") + "}");
            Variant variant = AmplVariantDeserializerTest.MAPPER.readValue(variantString, Variant.class);
            variant.getSchedule().isPresent();
            variant.getName().isPresent();
            variant.getDescription().isPresent();
            variant.getWeight().isPresent();
            variant.getVariantPushPayload().getNotification().isPresent();
            org.junit.Assert.fail("testVariant_literalMutationString15 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

