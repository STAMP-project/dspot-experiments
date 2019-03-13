package com.auth0.jwt.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BasicHeaderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ObjectReader objectReader = new ObjectMapper().reader();

    @SuppressWarnings("Convert2Diamond")
    @Test
    public void shouldHaveUnmodifiableTreeWhenInstantiatedWithNonNullTree() throws Exception {
        exception.expect(UnsupportedOperationException.class);
        BasicHeader header = new BasicHeader(null, null, null, null, new HashMap<String, JsonNode>(), objectReader);
        header.getTree().put("something", null);
    }

    @Test
    public void shouldHaveUnmodifiableTreeWhenInstantiatedWithNullTree() throws Exception {
        exception.expect(UnsupportedOperationException.class);
        BasicHeader header = new BasicHeader(null, null, null, null, null, objectReader);
        header.getTree().put("something", null);
    }

    @Test
    public void shouldHaveTree() throws Exception {
        HashMap<String, JsonNode> map = new HashMap<>();
        JsonNode node = NullNode.getInstance();
        map.put("key", node);
        BasicHeader header = new BasicHeader(null, null, null, null, map, objectReader);
        MatcherAssert.assertThat(header.getTree(), is(notNullValue()));
        MatcherAssert.assertThat(header.getTree(), is(IsMapContaining.hasEntry("key", node)));
    }

    @Test
    public void shouldGetAlgorithm() throws Exception {
        BasicHeader header = new BasicHeader("HS256", null, null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getAlgorithm(), is(notNullValue()));
        MatcherAssert.assertThat(header.getAlgorithm(), is("HS256"));
    }

    @Test
    public void shouldGetNullAlgorithmIfMissing() throws Exception {
        BasicHeader header = new BasicHeader(null, null, null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getAlgorithm(), is(nullValue()));
    }

    @Test
    public void shouldGetType() throws Exception {
        BasicHeader header = new BasicHeader(null, "jwt", null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getType(), is(notNullValue()));
        MatcherAssert.assertThat(header.getType(), is("jwt"));
    }

    @Test
    public void shouldGetNullTypeIfMissing() throws Exception {
        BasicHeader header = new BasicHeader(null, null, null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getType(), is(nullValue()));
    }

    @Test
    public void shouldGetContentType() throws Exception {
        BasicHeader header = new BasicHeader(null, null, "content", null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getContentType(), is(notNullValue()));
        MatcherAssert.assertThat(header.getContentType(), is("content"));
    }

    @Test
    public void shouldGetNullContentTypeIfMissing() throws Exception {
        BasicHeader header = new BasicHeader(null, null, null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getContentType(), is(nullValue()));
    }

    @Test
    public void shouldGetKeyId() throws Exception {
        BasicHeader header = new BasicHeader(null, null, null, "key", null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getKeyId(), is(notNullValue()));
        MatcherAssert.assertThat(header.getKeyId(), is("key"));
    }

    @Test
    public void shouldGetNullKeyIdIfMissing() throws Exception {
        BasicHeader header = new BasicHeader(null, null, null, null, null, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getKeyId(), is(nullValue()));
    }

    @Test
    public void shouldGetExtraClaim() throws Exception {
        Map<String, JsonNode> tree = new HashMap<>();
        tree.put("extraClaim", new TextNode("extraValue"));
        BasicHeader header = new BasicHeader(null, null, null, null, tree, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getHeaderClaim("extraClaim"), is(instanceOf(JsonNodeClaim.class)));
        MatcherAssert.assertThat(header.getHeaderClaim("extraClaim").asString(), is("extraValue"));
    }

    @Test
    public void shouldGetNotNullExtraClaimIfMissing() throws Exception {
        Map<String, JsonNode> tree = new HashMap<>();
        BasicHeader header = new BasicHeader(null, null, null, null, tree, objectReader);
        MatcherAssert.assertThat(header, is(notNullValue()));
        MatcherAssert.assertThat(header.getHeaderClaim("missing"), is(notNullValue()));
        MatcherAssert.assertThat(header.getHeaderClaim("missing"), is(instanceOf(NullClaim.class)));
    }
}

