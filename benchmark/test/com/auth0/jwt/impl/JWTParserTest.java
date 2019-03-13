package com.auth0.jwt.impl;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Header;
import com.auth0.jwt.interfaces.Payload;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JWTParserTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private JWTParser parser;

    @Test
    public void shouldGetDefaultObjectMapper() throws Exception {
        ObjectMapper mapper = JWTParser.getDefaultObjectMapper();
        MatcherAssert.assertThat(mapper, is(notNullValue()));
        MatcherAssert.assertThat(mapper, is(instanceOf(ObjectMapper.class)));
        MatcherAssert.assertThat(mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS), is(false));
    }

    @Test
    public void shouldAddDeserializers() throws Exception {
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        new JWTParser(mapper);
        Mockito.verify(mapper).registerModule(ArgumentMatchers.any(Module.class));
    }

    @Test
    public void shouldParsePayload() throws Exception {
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(mapper.readerFor(Payload.class)).thenReturn(reader);
        JWTParser parser = new JWTParser(mapper);
        parser.parsePayload("{}");
        Mockito.verify(reader).readValue("{}");
    }

    @Test
    public void shouldThrowOnInvalidPayload() throws Exception {
        String jsonPayload = "{{";
        exception.expect(JWTDecodeException.class);
        exception.expectMessage(String.format("The string '%s' doesn't have a valid JSON format.", jsonPayload));
        Payload payload = parser.parsePayload(jsonPayload);
        MatcherAssert.assertThat(payload, is(nullValue()));
    }

    @Test
    public void shouldParseHeader() throws Exception {
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(mapper.readerFor(Header.class)).thenReturn(reader);
        JWTParser parser = new JWTParser(mapper);
        parser.parseHeader("{}");
        Mockito.verify(reader).readValue("{}");
    }

    @Test
    public void shouldThrowOnInvalidHeader() throws Exception {
        String jsonHeader = "}}";
        exception.expect(JWTDecodeException.class);
        exception.expectMessage(String.format("The string '%s' doesn't have a valid JSON format.", jsonHeader));
        Header header = parser.parseHeader(jsonHeader);
        MatcherAssert.assertThat(header, is(nullValue()));
    }

    @Test
    public void shouldThrowWhenConvertingHeaderIfNullJson() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The string 'null' doesn't have a valid JSON format.");
        parser.parseHeader(null);
    }

    @Test
    public void shouldThrowWhenConvertingHeaderFromInvalidJson() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The string '}{' doesn't have a valid JSON format.");
        parser.parseHeader("}{");
    }

    @Test
    public void shouldThrowWhenConvertingPayloadIfNullJson() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The string 'null' doesn't have a valid JSON format.");
        parser.parsePayload(null);
    }

    @Test
    public void shouldThrowWhenConvertingPayloadFromInvalidJson() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The string '}{' doesn't have a valid JSON format.");
        parser.parsePayload("}{");
    }
}

