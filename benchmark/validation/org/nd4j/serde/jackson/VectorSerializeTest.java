package org.nd4j.serde.jackson;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.jackson.databind.ObjectMapper;


/**
 * Created by agibsonccc on 6/23/16.
 */
public class VectorSerializeTest {
    private static ObjectMapper objectMapper;

    @Test
    public void testSerde() throws Exception {
        String json = VectorSerializeTest.objectMapper.writeValueAsString(Nd4j.create(2, 2));
        INDArray assertion = Nd4j.create(2, 2);
        INDArray test = VectorSerializeTest.objectMapper.readValue(json, INDArray.class);
        Assert.assertEquals(assertion, test);
    }
}

