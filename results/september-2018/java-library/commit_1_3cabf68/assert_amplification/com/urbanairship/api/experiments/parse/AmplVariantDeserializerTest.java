package com.urbanairship.api.experiments.parse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.urbanairship.api.experiments.model.Variant;
import com.urbanairship.api.experiments.model.VariantPushPayload;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AmplVariantDeserializerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final ObjectMapper MAPPER = ExperimentObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testVariant() throws Exception {
        String variantString = "{" + ((((((("\"schedule\": {" + "\"scheduled_time\": \"2017-07-27T18:27:25.000Z\"") + "},") + "\"push\":{\"notification\":{\"alert\":\"Hello there\"}},") + "\"name\":\"A name\",") + "\"description\":\"A description\",") + "\"weight\":\"4\"") + "}");
        Assert.assertEquals("{\"schedule\": {\"scheduled_time\": \"2017-07-27T18:27:25.000Z\"},\"push\":{\"notification\":{\"alert\":\"Hello there\"}},\"name\":\"A name\",\"description\":\"A description\",\"weight\":\"4\"}", variantString);
        Variant variant = AmplVariantDeserializerTest.MAPPER.readValue(variantString, Variant.class);
        Assert.assertTrue(((Optional) (((Variant) (variant)).getDescription())).isPresent());
        Assert.assertEquals("A description", ((Optional) (((Variant) (variant)).getDescription())).get());
        Assert.assertEquals("Optional.of(A description)", ((Optional) (((Variant) (variant)).getDescription())).toString());
        Assert.assertEquals(2017251257, ((int) (((Optional) (((Variant) (variant)).getDescription())).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getSchedule())).isPresent());
        Assert.assertEquals("Optional.of(Schedule{scheduledTimestamp=2017-07-27T18:27:25.000Z, localTimePresent=false})", ((Optional) (((Variant) (variant)).getSchedule())).toString());
        Assert.assertEquals(2140669506, ((int) (((Optional) (((Variant) (variant)).getSchedule())).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getWeight())).isPresent());
        Assert.assertEquals(4, ((int) (((Optional) (((Variant) (variant)).getWeight())).get())));
        Assert.assertEquals("Optional.of(4)", ((Optional) (((Variant) (variant)).getWeight())).toString());
        Assert.assertEquals(1502476576, ((int) (((Optional) (((Variant) (variant)).getWeight())).hashCode())));
        Assert.assertTrue(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).isPresent());
        Assert.assertEquals("Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()})", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).toString());
        Assert.assertEquals(-2066536885, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).hashCode())));
        Assert.assertFalse(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).hashCode())));
        Assert.assertFalse(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).hashCode())));
        Assert.assertEquals("VariantPushPayload{notification=Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()}), pushOptions=Optional.absent(), inApp=Optional.absent()}", ((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).toString());
        Assert.assertEquals(-788100758, ((int) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).hashCode())));
        Assert.assertEquals("Variant{name=Optional.of(A name), description=Optional.of(A description), schedule=Optional.of(Schedule{scheduledTimestamp=2017-07-27T18:27:25.000Z, localTimePresent=false}), variantPushPayload=VariantPushPayload{notification=Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()}), pushOptions=Optional.absent(), inApp=Optional.absent()}, weight=Optional.of(4)}", ((Variant) (variant)).toString());
        Assert.assertEquals(1336065572, ((int) (((Variant) (variant)).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getName())).isPresent());
        Assert.assertEquals("A name", ((Optional) (((Variant) (variant)).getName())).get());
        Assert.assertEquals("Optional.of(A name)", ((Optional) (((Variant) (variant)).getName())).toString());
        Assert.assertEquals(-898669530, ((int) (((Optional) (((Variant) (variant)).getName())).hashCode())));
        variant.getSchedule().isPresent();
        variant.getName().isPresent();
        variant.getDescription().isPresent();
        variant.getWeight().isPresent();
        variant.getVariantPushPayload().getNotification().isPresent();
        Assert.assertEquals("{\"schedule\": {\"scheduled_time\": \"2017-07-27T18:27:25.000Z\"},\"push\":{\"notification\":{\"alert\":\"Hello there\"}},\"name\":\"A name\",\"description\":\"A description\",\"weight\":\"4\"}", variantString);
        Assert.assertTrue(((Optional) (((Variant) (variant)).getDescription())).isPresent());
        Assert.assertEquals("A description", ((Optional) (((Variant) (variant)).getDescription())).get());
        Assert.assertEquals("Optional.of(A description)", ((Optional) (((Variant) (variant)).getDescription())).toString());
        Assert.assertEquals(2017251257, ((int) (((Optional) (((Variant) (variant)).getDescription())).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getSchedule())).isPresent());
        Assert.assertEquals("Optional.of(Schedule{scheduledTimestamp=2017-07-27T18:27:25.000Z, localTimePresent=false})", ((Optional) (((Variant) (variant)).getSchedule())).toString());
        Assert.assertEquals(2140669506, ((int) (((Optional) (((Variant) (variant)).getSchedule())).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getWeight())).isPresent());
        Assert.assertEquals(4, ((int) (((Optional) (((Variant) (variant)).getWeight())).get())));
        Assert.assertEquals("Optional.of(4)", ((Optional) (((Variant) (variant)).getWeight())).toString());
        Assert.assertEquals(1502476576, ((int) (((Optional) (((Variant) (variant)).getWeight())).hashCode())));
        Assert.assertTrue(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).isPresent());
        Assert.assertEquals("Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()})", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).toString());
        Assert.assertEquals(-2066536885, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getNotification())).hashCode())));
        Assert.assertFalse(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getPushOptions())).hashCode())));
        Assert.assertFalse(((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).getInApp())).hashCode())));
        Assert.assertEquals("VariantPushPayload{notification=Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()}), pushOptions=Optional.absent(), inApp=Optional.absent()}", ((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).toString());
        Assert.assertEquals(-788100758, ((int) (((VariantPushPayload) (((Variant) (variant)).getVariantPushPayload())).hashCode())));
        Assert.assertEquals("Variant{name=Optional.of(A name), description=Optional.of(A description), schedule=Optional.of(Schedule{scheduledTimestamp=2017-07-27T18:27:25.000Z, localTimePresent=false}), variantPushPayload=VariantPushPayload{notification=Optional.of(Notification{alert=Optional.of(Hello there), deviceTypePayloadOverrides={}, actions=Optional.absent(), interactive=Optional.absent()}), pushOptions=Optional.absent(), inApp=Optional.absent()}, weight=Optional.of(4)}", ((Variant) (variant)).toString());
        Assert.assertEquals(1336065572, ((int) (((Variant) (variant)).hashCode())));
        Assert.assertTrue(((Optional) (((Variant) (variant)).getName())).isPresent());
        Assert.assertEquals("A name", ((Optional) (((Variant) (variant)).getName())).get());
        Assert.assertEquals("Optional.of(A name)", ((Optional) (((Variant) (variant)).getName())).toString());
        Assert.assertEquals(-898669530, ((int) (((Optional) (((Variant) (variant)).getName())).hashCode())));
    }
}

