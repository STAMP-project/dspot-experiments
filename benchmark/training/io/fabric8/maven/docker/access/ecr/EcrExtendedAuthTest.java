package io.fabric8.maven.docker.access.ecr;


import AwsSigner4Request.TIME_FORMAT;
import io.fabric8.maven.docker.access.AuthConfig;
import io.fabric8.maven.docker.util.Logger;
import java.text.ParseException;
import java.util.Date;
import mockit.Mocked;
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test exchange of local stored credentials for temporary ecr credentials
 *
 * @author chas
 * @since 2016-12-21
 */
public class EcrExtendedAuthTest {
    @Mocked
    private Logger logger;

    @Test
    public void testIsNotAws() {
        Assert.assertFalse(isAwsRegistry());
    }

    @Test
    public void testIsAws() {
        Assert.assertTrue(isAwsRegistry());
    }

    @Test
    public void testHeaders() throws ParseException {
        EcrExtendedAuth eea = new EcrExtendedAuth(logger, "123456789012.dkr.ecr.eu-west-1.amazonaws.com");
        AuthConfig localCredentials = new AuthConfig("username", "password", null, null);
        Date signingTime = TIME_FORMAT.parse("20161217T211058Z");
        HttpPost request = eea.createSignedRequest(localCredentials, signingTime);
        Assert.assertEquals("ecr.eu-west-1.amazonaws.com", request.getFirstHeader("host").getValue());
        Assert.assertEquals("20161217T211058Z", request.getFirstHeader("X-Amz-Date").getValue());
        Assert.assertEquals("AWS4-HMAC-SHA256 Credential=username/20161217/eu-west-1/ecr/aws4_request, SignedHeaders=content-type;host;x-amz-target, Signature=1bab0f5c269debe913e532011d5d192b190bb4c55d3de1bc1506eefb93e058e1", request.getFirstHeader("Authorization").getValue());
    }
}

