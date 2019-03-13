package com.baeldung.jackson.node;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NodeOperationUnitTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenAnObject_whenConvertingIntoNode_thenCorrect() {
        final NodeBean fromValue = new NodeBean(2016, "baeldung.com");
        final JsonNode node = NodeOperationUnitTest.mapper.valueToTree(fromValue);
        Assert.assertEquals(2016, node.get("id").intValue());
        Assert.assertEquals("baeldung.com", node.get("name").textValue());
    }

    @Test
    public void givenANode_whenWritingOutAsAJsonString_thenCorrect() throws IOException {
        final String pathToTestFile = "node_to_json_test.json";
        final char[] characterBuffer = new char[50];
        final JsonNode node = NodeOperationUnitTest.mapper.createObjectNode();
        ((ObjectNode) (node)).put("id", 2016);
        ((ObjectNode) (node)).put("name", "baeldung.com");
        try (FileWriter outputStream = new FileWriter(pathToTestFile)) {
            NodeOperationUnitTest.mapper.writeValue(outputStream, node);
        }
        try (FileReader inputStreamForAssertion = new FileReader(pathToTestFile)) {
            inputStreamForAssertion.read(characterBuffer);
        }
        final String textContentOfTestFile = new String(characterBuffer);
        Assert.assertThat(textContentOfTestFile, CoreMatchers.containsString("2016"));
        Assert.assertThat(textContentOfTestFile, CoreMatchers.containsString("baeldung.com"));
        Files.delete(Paths.get(pathToTestFile));
    }

    @Test
    public void givenANode_whenConvertingIntoAnObject_thenCorrect() throws JsonProcessingException {
        final JsonNode node = NodeOperationUnitTest.mapper.createObjectNode();
        ((ObjectNode) (node)).put("id", 2016);
        ((ObjectNode) (node)).put("name", "baeldung.com");
        final NodeBean toValue = NodeOperationUnitTest.mapper.treeToValue(node, NodeBean.class);
        Assert.assertEquals(2016, toValue.getId());
        Assert.assertEquals("baeldung.com", toValue.getName());
    }

    @Test
    public void givenANode_whenAddingIntoATree_thenCorrect() throws IOException {
        final JsonNode rootNode = ExampleStructure.getExampleRoot();
        final ObjectNode addedNode = ((ObjectNode) (rootNode)).putObject("address");
        addedNode.put("city", "Seattle").put("state", "Washington").put("country", "United States");
        Assert.assertFalse(rootNode.path("address").isMissingNode());
        Assert.assertEquals("Seattle", rootNode.path("address").path("city").textValue());
        Assert.assertEquals("Washington", rootNode.path("address").path("state").textValue());
        Assert.assertEquals("United States", rootNode.path("address").path("country").textValue());
    }

    @Test
    public void givenANode_whenModifyingIt_thenCorrect() throws IOException {
        final String newString = "{\"nick\": \"cowtowncoder\"}";
        final JsonNode newNode = NodeOperationUnitTest.mapper.readTree(newString);
        final JsonNode rootNode = ExampleStructure.getExampleRoot();
        ((ObjectNode) (rootNode)).set("name", newNode);
        Assert.assertFalse(rootNode.path("name").path("nick").isMissingNode());
        Assert.assertEquals("cowtowncoder", rootNode.path("name").path("nick").textValue());
    }

    @Test
    public void givenANode_whenRemovingFromATree_thenCorrect() throws IOException {
        final JsonNode rootNode = ExampleStructure.getExampleRoot();
        ((ObjectNode) (rootNode)).remove("company");
        Assert.assertTrue(rootNode.path("company").isMissingNode());
    }
}

