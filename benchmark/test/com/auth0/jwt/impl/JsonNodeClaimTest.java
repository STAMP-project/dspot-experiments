package com.auth0.jwt.impl;


import com.auth0.jwt.UserPojo;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JsonNodeClaimTest {
    private ObjectMapper mapper;

    private ObjectReader objectReader;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldGetBooleanValue() throws Exception {
        JsonNode value = mapper.valueToTree(true);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asBoolean(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asBoolean(), is(true));
    }

    @Test
    public void shouldGetNullBooleanIfNotBooleanValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asBoolean(), is(IsNull.nullValue()));
        JsonNode stringValue = mapper.valueToTree("boolean");
        Assert.assertThat(claimFromNode(stringValue).asBoolean(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetIntValue() throws Exception {
        JsonNode value = mapper.valueToTree(123);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asInt(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asInt(), is(123));
    }

    @Test
    public void shouldGetNullIntIfNotIntValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asInt(), is(IsNull.nullValue()));
        JsonNode stringValue = mapper.valueToTree("123");
        Assert.assertThat(claimFromNode(stringValue).asInt(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetLongValue() throws Exception {
        JsonNode value = mapper.valueToTree(Long.MAX_VALUE);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asLong(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asLong(), is(Long.MAX_VALUE));
    }

    @Test
    public void shouldGetNullLongIfNotIntValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asLong(), is(IsNull.nullValue()));
        JsonNode stringValue = mapper.valueToTree(("" + (Long.MAX_VALUE)));
        Assert.assertThat(claimFromNode(stringValue).asLong(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetDoubleValue() throws Exception {
        JsonNode value = mapper.valueToTree(1.5);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asDouble(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asDouble(), is(1.5));
    }

    @Test
    public void shouldGetNullDoubleIfNotDoubleValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asDouble(), is(IsNull.nullValue()));
        JsonNode stringValue = mapper.valueToTree("123.23");
        Assert.assertThat(claimFromNode(stringValue).asDouble(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetDateValue() throws Exception {
        JsonNode value = mapper.valueToTree(1476824844L);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asDate(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asDate(), is(new Date((1476824844L * 1000))));
    }

    @Test
    public void shouldGetNullDateIfNotDateValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asDate(), is(IsNull.nullValue()));
        JsonNode stringValue = mapper.valueToTree("1476824844");
        Assert.assertThat(claimFromNode(stringValue).asDate(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetStringValue() throws Exception {
        JsonNode value = mapper.valueToTree("string");
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asString(), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asString(), is("string"));
    }

    @Test
    public void shouldGetNullStringIfNotStringValue() throws Exception {
        JsonNode objectValue = mapper.valueToTree(new Object());
        Assert.assertThat(claimFromNode(objectValue).asString(), is(IsNull.nullValue()));
        JsonNode intValue = mapper.valueToTree(12345);
        Assert.assertThat(claimFromNode(intValue).asString(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetArrayValueOfCustomClass() throws Exception {
        JsonNode value = mapper.valueToTree(new UserPojo[]{ new UserPojo("George", 1), new UserPojo("Mark", 2) });
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asArray(UserPojo.class), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asArray(UserPojo.class), is(arrayContaining(new UserPojo("George", 1), new UserPojo("Mark", 2))));
    }

    @Test
    public void shouldGetArrayValue() throws Exception {
        JsonNode value = mapper.valueToTree(new String[]{ "string1", "string2" });
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asArray(String.class), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asArray(String.class), is(arrayContaining("string1", "string2")));
    }

    @Test
    public void shouldGetNullArrayIfNullValue() throws Exception {
        JsonNode value = mapper.valueToTree(null);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asArray(String.class), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetNullArrayIfNonArrayValue() throws Exception {
        JsonNode value = mapper.valueToTree(1);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asArray(String.class), is(IsNull.nullValue()));
    }

    @Test
    public void shouldThrowIfArrayClassMismatch() throws Exception {
        JsonNode value = mapper.valueToTree(new String[]{ "keys", "values" });
        Claim claim = claimFromNode(value);
        exception.expect(JWTDecodeException.class);
        claim.asArray(UserPojo.class);
    }

    @Test
    public void shouldGetListValueOfCustomClass() throws Exception {
        JsonNode value = mapper.valueToTree(Arrays.asList(new UserPojo("George", 1), new UserPojo("Mark", 2)));
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asList(UserPojo.class), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asList(UserPojo.class), is(hasItems(new UserPojo("George", 1), new UserPojo("Mark", 2))));
    }

    @Test
    public void shouldGetListValue() throws Exception {
        JsonNode value = mapper.valueToTree(Arrays.asList("string1", "string2"));
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asList(String.class), is(IsNull.notNullValue()));
        Assert.assertThat(claim.asList(String.class), is(hasItems("string1", "string2")));
    }

    @Test
    public void shouldGetNullListIfNullValue() throws Exception {
        JsonNode value = mapper.valueToTree(null);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asList(String.class), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetNullListIfNonArrayValue() throws Exception {
        JsonNode value = mapper.valueToTree(1);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asList(String.class), is(IsNull.nullValue()));
    }

    @Test
    public void shouldThrowIfListClassMismatch() throws Exception {
        JsonNode value = mapper.valueToTree(new String[]{ "keys", "values" });
        Claim claim = claimFromNode(value);
        exception.expect(JWTDecodeException.class);
        claim.asList(UserPojo.class);
    }

    @Test
    public void shouldGetNullMapIfNullValue() throws Exception {
        JsonNode value = mapper.valueToTree(null);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asMap(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetNullMapIfNonArrayValue() throws Exception {
        JsonNode value = mapper.valueToTree(1);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim.asMap(), is(IsNull.nullValue()));
    }

    @Test
    public void shouldGetMapValue() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("text", "extraValue");
        map.put("number", 12);
        map.put("boolean", true);
        map.put("object", Collections.singletonMap("something", "else"));
        JsonNode value = mapper.valueToTree(map);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Map<String, Object> backMap = claim.asMap();
        Assert.assertThat(backMap, is(IsNull.notNullValue()));
        Assert.assertThat(backMap, hasEntry("text", ((Object) ("extraValue"))));
        Assert.assertThat(backMap, hasEntry("number", ((Object) (12))));
        Assert.assertThat(backMap, hasEntry("boolean", ((Object) (true))));
        Assert.assertThat(backMap, hasKey("object"));
        Assert.assertThat(((Map<String, Object>) (backMap.get("object"))), IsMapContaining.hasEntry("something", ((Object) ("else"))));
    }

    @Test
    public void shouldThrowIfAnExtraordinaryExceptionHappensWhenParsingAsGenericMap() throws Exception {
        JsonNode value = Mockito.mock(ObjectNode.class);
        Mockito.when(value.getNodeType()).thenReturn(JsonNodeType.OBJECT);
        ObjectReader mockedMapper = Mockito.mock(ObjectReader.class);
        JsonNodeClaim claim = ((JsonNodeClaim) (JsonNodeClaim.claimFromNode(value, mockedMapper)));
        JsonNodeClaim spiedClaim = Mockito.spy(claim);
        JsonParser mockedParser = Mockito.mock(JsonParser.class);
        Mockito.when(mockedMapper.treeAsTokens(value)).thenReturn(mockedParser);
        Mockito.when(mockedParser.readValueAs(ArgumentMatchers.any(TypeReference.class))).thenThrow(IOException.class);
        exception.expect(JWTDecodeException.class);
        spiedClaim.asMap();
    }

    @Test
    public void shouldGetCustomClassValue() throws Exception {
        JsonNode value = mapper.valueToTree(new UserPojo("john", 123));
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim.as(UserPojo.class).getName(), is("john"));
        Assert.assertThat(claim.as(UserPojo.class).getId(), is(123));
    }

    @Test
    public void shouldThrowIfCustomClassMismatch() throws Exception {
        JsonNode value = mapper.valueToTree(new UserPojo("john", 123));
        Claim claim = claimFromNode(value);
        exception.expect(JWTDecodeException.class);
        claim.as(String.class);
    }

    @SuppressWarnings({ "unchecked", "RedundantCast" })
    @Test
    public void shouldGetAsMapValue() throws Exception {
        JsonNode value = mapper.valueToTree(Collections.singletonMap("key", new UserPojo("john", 123)));
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Map map = claim.as(Map.class);
        Assert.assertThat(((Map<String, Object>) (map.get("key"))), hasEntry("name", ((Object) ("john"))));
        Assert.assertThat(((Map<String, Object>) (map.get("key"))), hasEntry("id", ((Object) (123))));
    }

    @Test
    public void shouldReturnBaseClaimWhenParsingMissingNode() throws Exception {
        JsonNode value = MissingNode.getInstance();
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(NullClaim.class)));
        Assert.assertThat(claim.isNull(), is(true));
    }

    @Test
    public void shouldReturnBaseClaimWhenParsingNullNode() throws Exception {
        JsonNode value = NullNode.getInstance();
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(NullClaim.class)));
        Assert.assertThat(claim.isNull(), is(true));
    }

    @Test
    public void shouldReturnBaseClaimWhenParsingNullValue() throws Exception {
        JsonNode value = mapper.valueToTree(null);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(NullClaim.class)));
        Assert.assertThat(claim.isNull(), is(true));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingObject() throws Exception {
        JsonNode value = mapper.valueToTree(new Object());
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingArray() throws Exception {
        JsonNode value = mapper.valueToTree(new String[]{  });
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingList() throws Exception {
        JsonNode value = mapper.valueToTree(new ArrayList<String>());
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingStringValue() throws Exception {
        JsonNode value = mapper.valueToTree("");
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingIntValue() throws Exception {
        JsonNode value = mapper.valueToTree(Integer.MAX_VALUE);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingDoubleValue() throws Exception {
        JsonNode value = mapper.valueToTree(Double.MAX_VALUE);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingDateValue() throws Exception {
        JsonNode value = mapper.valueToTree(new Date());
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }

    @Test
    public void shouldReturnNonNullClaimWhenParsingBooleanValue() throws Exception {
        JsonNode value = mapper.valueToTree(Boolean.TRUE);
        Claim claim = claimFromNode(value);
        Assert.assertThat(claim, is(IsNull.notNullValue()));
        Assert.assertThat(claim, is(instanceOf(JsonNodeClaim.class)));
        Assert.assertThat(claim.isNull(), is(false));
    }
}

