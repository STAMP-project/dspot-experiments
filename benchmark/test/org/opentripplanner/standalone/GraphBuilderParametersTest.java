package org.opentripplanner.standalone;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class GraphBuilderParametersTest {
    enum AnEnum {

        A,
        B;}

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String KEY = "key";

    private static final GraphBuilderParametersTest.AnEnum DEFAULT = GraphBuilderParametersTest.AnEnum.B;

    @Test
    public void testValueOf() throws Exception {
        // Given
        JsonNode config = GraphBuilderParametersTest.readConfig("{ 'key' : 'A' }");
        // Then
        Assert.assertEquals("Get existing property", GraphBuilderParametersTest.AnEnum.A, GraphBuilderParameters.enumValueOf(config, GraphBuilderParametersTest.KEY, GraphBuilderParametersTest.DEFAULT));
        Assert.assertEquals("Get default value", GraphBuilderParametersTest.DEFAULT, GraphBuilderParameters.enumValueOf(config, "missing-key", GraphBuilderParametersTest.DEFAULT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithIllegalPropertySet() throws Exception {
        // Given
        JsonNode config = GraphBuilderParametersTest.readConfig("{ 'key' : 'X' }");
        // Then expect an error when value 'X' is not in the set of legal values: ['A', 'B']
        GraphBuilderParameters.enumValueOf(config, "key", GraphBuilderParametersTest.DEFAULT);
    }
}

