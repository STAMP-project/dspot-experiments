package com.auth0.jwt.impl;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Header;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HeaderDeserializerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HeaderDeserializer deserializer;

    private ObjectReader objectReader = new ObjectMapper().reader();

    @Test
    public void shouldThrowOnNullTree() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("Parsing the Header's JSON resulted on a Null map");
        JsonDeserializer deserializer = new HeaderDeserializer(objectReader);
        JsonParser parser = Mockito.mock(JsonParser.class);
        ObjectCodec codec = Mockito.mock(ObjectCodec.class);
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        Mockito.when(codec.readValue(ArgumentMatchers.eq(parser), ArgumentMatchers.any(TypeReference.class))).thenReturn(null);
        Mockito.when(parser.getCodec()).thenReturn(codec);
        deserializer.deserialize(parser, context);
    }

    @Test
    public void shouldNotRemoveKnownPublicClaimsFromTree() throws Exception {
        String headerJSON = "{\n" + ((((("  \"alg\": \"HS256\",\n" + "  \"typ\": \"jws\",\n") + "  \"cty\": \"content\",\n") + "  \"kid\": \"key\",\n") + "  \"roles\": \"admin\"\n") + "}");
        StringReader reader = new StringReader(headerJSON);
        JsonParser jsonParser = new JsonFactory().createParser(reader);
        ObjectMapper mapper = new ObjectMapper();
        jsonParser.setCodec(mapper);
        Header header = deserializer.deserialize(jsonParser, mapper.getDeserializationContext());
        Assert.assertThat(header, is(notNullValue()));
        Assert.assertThat(header.getAlgorithm(), is("HS256"));
        Assert.assertThat(header.getType(), is("jws"));
        Assert.assertThat(header.getContentType(), is("content"));
        Assert.assertThat(header.getKeyId(), is("key"));
        Assert.assertThat(header.getHeaderClaim("roles").asString(), is("admin"));
        Assert.assertThat(header.getHeaderClaim("alg").asString(), is("HS256"));
        Assert.assertThat(header.getHeaderClaim("typ").asString(), is("jws"));
        Assert.assertThat(header.getHeaderClaim("cty").asString(), is("content"));
        Assert.assertThat(header.getHeaderClaim("kid").asString(), is("key"));
    }

    @Test
    public void shouldGetNullStringWhenParsingNullNode() throws Exception {
        Map<String, JsonNode> tree = new HashMap<>();
        NullNode node = NullNode.getInstance();
        tree.put("key", node);
        String text = deserializer.getString(tree, "key");
        Assert.assertThat(text, is(nullValue()));
    }

    @Test
    public void shouldGetNullStringWhenParsingNull() throws Exception {
        Map<String, JsonNode> tree = new HashMap<>();
        tree.put("key", null);
        String text = deserializer.getString(tree, "key");
        Assert.assertThat(text, is(nullValue()));
    }

    @Test
    public void shouldGetStringWhenParsingTextNode() throws Exception {
        Map<String, JsonNode> tree = new HashMap<>();
        TextNode node = new TextNode("something here");
        tree.put("key", node);
        String text = deserializer.getString(tree, "key");
        Assert.assertThat(text, is(notNullValue()));
        Assert.assertThat(text, is("something here"));
    }
}

