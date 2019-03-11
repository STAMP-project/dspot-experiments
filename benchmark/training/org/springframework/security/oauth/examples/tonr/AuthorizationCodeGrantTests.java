package org.springframework.security.oauth.examples.tonr;


import HttpStatus.OK;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.MultiValueMap;


/**
 *
 *
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class AuthorizationCodeGrantTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    private AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();

    {
        resource.setAccessTokenUri(serverRunning.getUrl("/sparklr2/oauth/token"));
        resource.setClientId("my-client-with-registered-redirect");
        resource.setId("sparklr");
        resource.setScope(Arrays.asList("trust"));
        resource.setUserAuthorizationUri(serverRunning.getUrl("/sparklr2/oauth/authorize"));
    }

    @Test
    public void testCannotConnectWithoutToken() throws Exception {
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource);
        resource.setPreEstablishedRedirectUri("http://anywhere.com");
        try {
            template.getForObject(serverRunning.getUrl("/tonr2/photos"), String.class);
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("A redirect is required to get the users approval"));
        }
    }

    @Test
    public void testAttemptedTokenAcquisitionWithNoRedirect() throws Exception {
        AuthorizationCodeAccessTokenProvider provider = new AuthorizationCodeAccessTokenProvider();
        try {
            OAuth2AccessToken token = provider.obtainAccessToken(resource, new DefaultAccessTokenRequest());
            Assert.fail("Expected UserRedirectRequiredException");
            Assert.assertNotNull(token);
        } catch (UserRedirectRequiredException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("A redirect is required"));
        }
    }

    @Test
    public void testTokenAcquisitionWithCorrectContext() throws Exception {
        ResponseEntity<String> page = serverRunning.getForString("/tonr2/login.jsp");
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(page.getBody());
        MultiValueMap<String, String> form;
        form = new org.springframework.util.LinkedMultiValueMap<String, String>();
        form.add("username", "marissa");
        form.add("password", "wombat");
        if (matcher.matches()) {
            form.add("_csrf", matcher.group(1));
        }
        ResponseEntity<Void> response = serverRunning.postForStatus("/tonr2/login", headers, form);
        cookie = response.getHeaders().getFirst("Set-Cookie");
        headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        // headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.setAccept(MediaType.parseMediaTypes("image/png,image/*;q=0.8,*/*;q=0.5"));
        String location = serverRunning.getForRedirect("/tonr2/sparklr/photos/1", headers);
        location = authenticateAndApprove(location);
        Assert.assertTrue(("Redirect location should be to the original photo URL: " + location), location.contains("photos/1"));
        HttpStatus status = serverRunning.getStatusCode(location, headers);
        Assert.assertEquals(OK, status);
    }

    @Test
    public void testTokenAcquisitionWithRegisteredRedirect() throws Exception {
        ResponseEntity<String> page = serverRunning.getForString("/tonr2/login.jsp");
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(page.getBody());
        MultiValueMap<String, String> form;
        form = new org.springframework.util.LinkedMultiValueMap<String, String>();
        form.add("username", "marissa");
        form.add("password", "wombat");
        if (matcher.matches()) {
            form.add("_csrf", matcher.group(1));
        }
        ResponseEntity<Void> response = serverRunning.postForStatus("/tonr2/login", headers, form);
        cookie = response.getHeaders().getFirst("Set-Cookie");
        headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        // The registered redirect is /redirect, but /trigger is used as a test
        // because it is different (i.e. not the current request URI).
        String location = serverRunning.getForRedirect("/tonr2/sparklr/trigger", headers);
        location = authenticateAndApprove(location);
        Assert.assertTrue(("Redirect location should be to the original photo URL: " + location), location.contains("sparklr/redirect"));
        HttpStatus status = serverRunning.getStatusCode(location, headers);
        Assert.assertEquals(OK, status);
    }
}

