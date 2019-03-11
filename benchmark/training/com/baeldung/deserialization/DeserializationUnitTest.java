package com.baeldung.deserialization;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class DeserializationUnitTest {
    private static final String serializedObj = "rO0ABXNyACljb20uYmFlbGR1bmcuZGVzZXJpYWxpemF0aW9uLkFwcGxlUHJvZHVjdAAAAAAAdMuxAgADTAANaGVhZHBob25lUG9ydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADWxpZ2h0bmluZ1BvcnRxAH4AAUwAD3RodW5kZXJib2x0UG9ydHEAfgABeHB0ABFoZWFkcGhvbmVQb3J0MjAyMHQAEWxpZ2h0bmluZ1BvcnQyMDIwdAATdGh1bmRlcmJvbHRQb3J0MjAyMA";

    private static long userDefinedSerialVersionUID = 1234567L;

    /**
     * Tests the deserialization of the original "AppleProduct" (no exceptions are thrown)
     *
     * @throws ClassNotFoundException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testDeserializeObj_compatible() throws IOException, ClassNotFoundException {
        Assert.assertEquals(DeserializationUnitTest.userDefinedSerialVersionUID, AppleProduct.getSerialVersionUID());
        AppleProduct macBook = new AppleProduct();
        macBook.headphonePort = "headphonePort2020";
        macBook.thunderboltPort = "thunderboltPort2020";
        macBook.lightningPort = "lightningPort2020";
        // serializes the "AppleProduct" object
        String serializedProduct = SerializationUtility.serializeObjectToString(macBook);
        // deserializes the "AppleProduct" object
        AppleProduct deserializedProduct = ((AppleProduct) (DeserializationUtility.deSerializeObjectFromString(serializedProduct)));
        Assert.assertTrue(deserializedProduct.headphonePort.equalsIgnoreCase(macBook.headphonePort));
        Assert.assertTrue(deserializedProduct.thunderboltPort.equalsIgnoreCase(macBook.thunderboltPort));
        Assert.assertTrue(deserializedProduct.lightningPort.equalsIgnoreCase(macBook.lightningPort));
    }
}

