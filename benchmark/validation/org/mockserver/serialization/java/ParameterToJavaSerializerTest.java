package org.mockserver.serialization.java;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Parameter;


public class ParameterToJavaSerializerTest {
    @Test
    public void shouldSerializeParameter() throws IOException {
        Assert.assertEquals(((NEW_LINE) + "        new Parameter(\"requestParameterNameOne\", \"requestParameterValueOneOne\", \"requestParameterValueOneTwo\")"), new ParameterToJavaSerializer().serialize(1, new Parameter("requestParameterNameOne", "requestParameterValueOneOne", "requestParameterValueOneTwo")));
    }

    @Test
    public void shouldSerializeMultipleParameters() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Parameter(\"requestParameterNameOne\", \"requestParameterValueOneOne\", \"requestParameterValueOneTwo\"),") + (NEW_LINE)) + "        new Parameter(\"requestParameterNameTwo\", \"requestParameterValueTwo\")"), new ParameterToJavaSerializer().serializeAsJava(1, new Parameter("requestParameterNameOne", "requestParameterValueOneOne", "requestParameterValueOneTwo"), new Parameter("requestParameterNameTwo", "requestParameterValueTwo")));
    }

    @Test
    public void shouldSerializeListOfParameters() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Parameter(\"requestParameterNameOne\", \"requestParameterValueOneOne\", \"requestParameterValueOneTwo\"),") + (NEW_LINE)) + "        new Parameter(\"requestParameterNameTwo\", \"requestParameterValueTwo\")"), new ParameterToJavaSerializer().serializeAsJava(1, Arrays.asList(new Parameter("requestParameterNameOne", "requestParameterValueOneOne", "requestParameterValueOneTwo"), new Parameter("requestParameterNameTwo", "requestParameterValueTwo"))));
    }
}

