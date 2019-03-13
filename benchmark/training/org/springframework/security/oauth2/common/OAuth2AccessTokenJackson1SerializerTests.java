package org.springframework.security.oauth2.common;


import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


/**
 * Tests serialization of an {@link OAuth2AccessToken} using jackson.
 *
 * @author Rob Winch
 */
@PrepareForTest(OAuth2AccessTokenJackson1Serializer.class)
public class OAuth2AccessTokenJackson1SerializerTests extends BaseOAuth2AccessTokenJacksonTest {
    protected ObjectMapper mapper;

    @Test
    public void writeValueAsStringNoRefresh() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.setRefreshToken(null);
        accessToken.setScope(null);
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_NOREFRESH, encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithRefresh() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.setScope(null);
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_NOSCOPE, encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithEmptyScope() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.getScope().clear();
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_NOSCOPE, encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithSingleScopes() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.getScope().remove(accessToken.getScope().iterator().next());
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_SINGLESCOPE, encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithNullScope() throws IOException, JsonGenerationException, JsonMappingException {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("Scopes cannot be null or empty. Got [null]");
        accessToken.getScope().clear();
        try {
            accessToken.getScope().add(null);
        } catch (NullPointerException e) {
            // short circuit NPE from Java 7 (which is correct but only relevant for this test)
            throw new JsonMappingException("Scopes cannot be null or empty. Got [null]");
        }
        mapper.writeValueAsString(accessToken);
    }

    @Test
    public void writeValueAsStringWithEmptyStringScope() throws IOException, JsonGenerationException, JsonMappingException {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("Scopes cannot be null or empty. Got []");
        accessToken.getScope().clear();
        accessToken.getScope().add("");
        mapper.writeValueAsString(accessToken);
    }

    @Test
    public void writeValueAsStringWithQuoteInScope() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.getScope().add("\"");
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals("{\"access_token\":\"token-value\",\"token_type\":\"bearer\",\"refresh_token\":\"refresh-value\",\"expires_in\":10,\"scope\":\"\\\" read write\"}", encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithMultiScopes() throws IOException, JsonGenerationException, JsonMappingException {
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_MULTISCOPE, encodedAccessToken);
    }

    @Test
    public void writeValueAsStringWithMac() throws Exception {
        accessToken.setTokenType("mac");
        String expectedEncodedAccessToken = BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_MULTISCOPE.replace("bearer", accessToken.getTokenType());
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(expectedEncodedAccessToken, encodedAccessToken);
    }

    @Test
    public void writeValueWithAdditionalInformation() throws IOException, JsonGenerationException, JsonMappingException {
        accessToken.setRefreshToken(null);
        accessToken.setScope(null);
        accessToken.setExpiration(null);
        accessToken.setAdditionalInformation(additionalInformation);
        String encodedAccessToken = mapper.writeValueAsString(accessToken);
        Assert.assertEquals(BaseOAuth2AccessTokenJacksonTest.ACCESS_TOKEN_ADDITIONAL_INFO, encodedAccessToken);
    }
}

