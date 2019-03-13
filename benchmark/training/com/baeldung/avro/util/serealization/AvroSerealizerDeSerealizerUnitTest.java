package com.baeldung.avro.util.serealization;


import com.baeldung.avro.util.model.AvroHttpRequest;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public class AvroSerealizerDeSerealizerUnitTest {
    AvroSerealizer serealizer;

    AvroDeSerealizer deSerealizer;

    AvroHttpRequest request;

    @Test
    public void WhenSerializedUsingJSONEncoder_thenObjectGetsSerialized() {
        byte[] data = serealizer.serealizeAvroHttpRequestJSON(request);
        Assert.assertTrue(Objects.nonNull(data));
        Assert.assertTrue(((data.length) > 0));
    }

    @Test
    public void WhenSerializedUsingBinaryEncoder_thenObjectGetsSerialized() {
        byte[] data = serealizer.serealizeAvroHttpRequestBinary(request);
        Assert.assertTrue(Objects.nonNull(data));
        Assert.assertTrue(((data.length) > 0));
    }

    @Test
    public void WhenDeserializeUsingJSONDecoder_thenActualAndExpectedObjectsAreEqual() {
        byte[] data = serealizer.serealizeAvroHttpRequestJSON(request);
        AvroHttpRequest actualRequest = deSerealizer.deSerealizeAvroHttpRequestJSON(data);
        Assert.assertEquals(actualRequest, request);
        Assert.assertTrue(actualRequest.getRequestTime().equals(request.getRequestTime()));
    }

    @Test
    public void WhenDeserializeUsingBinaryecoder_thenActualAndExpectedObjectsAreEqual() {
        byte[] data = serealizer.serealizeAvroHttpRequestBinary(request);
        AvroHttpRequest actualRequest = deSerealizer.deSerealizeAvroHttpRequestBinary(data);
        Assert.assertEquals(actualRequest, request);
        Assert.assertTrue(actualRequest.getRequestTime().equals(request.getRequestTime()));
    }
}

