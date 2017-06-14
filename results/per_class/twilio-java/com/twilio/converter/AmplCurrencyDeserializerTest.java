

package com.twilio.converter;


/**
 * Test class for {@link CurrencyDeserializer}.
 */
public class AmplCurrencyDeserializerTest {
    @org.junit.Test
    public void testDeserialize() throws java.io.IOException {
        java.lang.String json = "{ \"currency\": \"usd\" }";
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.twilio.converter.AmplCurrencyDeserializerTest.Container c = com.twilio.converter.AmplCurrencyDeserializerTest.Container.fromJson(json, mapper);
        org.junit.Assert.assertEquals("USD", c.currency.getCurrencyCode());
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testInvalidCurrency() throws java.io.IOException {
        java.lang.String json = "{ \"currency\": \"poo\" }";
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.twilio.converter.AmplCurrencyDeserializerTest.Container.fromJson(json, mapper);
    }

    private static class Container {
        private final java.util.Currency currency;

        public Container(@com.fasterxml.jackson.annotation.JsonProperty(value = "currency")
        @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.twilio.converter.CurrencyDeserializer.class)
        java.util.Currency currency) {
            this.currency = currency;
        }

        public static com.twilio.converter.AmplCurrencyDeserializerTest.Container fromJson(java.lang.String json, com.fasterxml.jackson.databind.ObjectMapper mapper) throws java.io.IOException {
            return mapper.readValue(json, com.twilio.converter.AmplCurrencyDeserializerTest.Container.class);
        }
    }
}

