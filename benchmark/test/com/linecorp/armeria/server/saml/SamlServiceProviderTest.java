/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.saml;


import ClientOptions.DEFAULT;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.LOCATION;
import HttpHeaderNames.SET_COOKIE;
import HttpMethod.GET;
import HttpStatus.BAD_REQUEST;
import HttpStatus.FOUND;
import HttpStatus.OK;
import MediaType.HTML_UTF_8;
import SAMLConstants.SAML20P_NS;
import SAMLConstants.SAML2_POST_BINDING_URI;
import SAMLConstants.SAML2_REDIRECT_BINDING_URI;
import ServerCookieDecoder.STRICT;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.auth.Authorizer;
import com.linecorp.armeria.testing.server.ServerRule;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.ClassRule;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;


public class SamlServiceProviderTest {
    private static final String signatureAlgorithm = SignatureConstants.ALGO_ID_SIGNATURE_RSA;

    private static final String spHostname = "localhost";

    // Entity ID can be any form of a string. An URI string is one of the general forms of that.
    private static final String spEntityId = "http://127.0.0.1";

    private static final CredentialResolver spCredentialResolver;

    private static final Credential idpCredential;

    private static final SamlRequestIdManager requestIdManager = new SamlServiceProviderTest.SequentialRequestIdManager();

    static {
        try {
            // Create IdP's key store for testing.
            final KeyStore idpKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            idpKeyStore.load(null, null);
            final SelfSignedCertificate idp = new SelfSignedCertificate();
            idpKeyStore.setKeyEntry("signing", idp.key(), "".toCharArray(), new Certificate[]{ idp.cert() });
            final CredentialResolver idpCredentialResolver = new KeyStoreCredentialResolver(idpKeyStore, ImmutableMap.of("signing", ""));
            final CriteriaSet cs = new CriteriaSet();
            cs.add(new EntityIdCriterion("signing"));
            idpCredential = idpCredentialResolver.resolveSingle(cs);
            // Create my key store for testing.
            final KeyStore myKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            myKeyStore.load(null, null);
            final SelfSignedCertificate mine = new SelfSignedCertificate();
            // Add my keys for signing and encryption.
            myKeyStore.setKeyEntry("signing", mine.key(), "".toCharArray(), new Certificate[]{ mine.cert() });
            myKeyStore.setKeyEntry("encryption", mine.key(), "".toCharArray(), new Certificate[]{ mine.cert() });
            // Add IdPs' certificates for validating a SAML message from the IdP.
            // By default, Armeria finds the certificate whose name equals to the entity ID of an IdP,
            // so we are adding the certificate with IdP's entity ID.
            myKeyStore.setCertificateEntry("http://idp.example.com/post", idp.cert());
            myKeyStore.setCertificateEntry("http://idp.example.com/redirect", idp.cert());
            // Create a password map for my keys.
            final Map<String, String> myKeyPasswords = ImmutableMap.of("signing", "", "encryption", "");
            spCredentialResolver = new KeyStoreCredentialResolver(myKeyStore, myKeyPasswords);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @ClassRule
    public static ServerRule rule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            final SamlServiceProvider sp = // We have two IdP config so one of them will be selected by the path variable.
            // Add one more dummy IdP which supports HTTP-Redirect binding protocol for SSO.
            // Add a dummy IdP which supports HTTP-Post binding protocol for SSO.
            // .scheme(SessionProtocol.HTTP)
            // My entity ID
            // A request will be authenticated if it contains 'test=test' cookie in Cookie header.
            new SamlServiceProviderBuilder().authorizer(new SamlServiceProviderTest.CookieBasedAuthorizer("test", "test")).ssoHandler(new SamlServiceProviderTest.CookieBasedSsoHandler("test", "test")).entityId(SamlServiceProviderTest.spEntityId).hostname(SamlServiceProviderTest.spHostname).credentialResolver(SamlServiceProviderTest.spCredentialResolver).signatureAlgorithm(SamlServiceProviderTest.signatureAlgorithm).idp().entityId("http://idp.example.com/post").ssoEndpoint(SamlEndpoint.ofHttpPost("http://idp.example.com/saml/sso/post")).sloResEndpoint(SamlEndpoint.ofHttpPost("http://idp.example.com/saml/slo/post")).and().idp().entityId("http://idp.example.com/redirect").ssoEndpoint(SamlEndpoint.ofHttpRedirect("http://idp.example.com/saml/sso/redirect")).sloResEndpoint(SamlEndpoint.ofHttpRedirect("http://idp.example.com/saml/slo/redirect")).and().idpConfigSelector(( configurator, ctx, req) -> {
                final String idpEntityId = "http://idp.example.com/" + (ctx.pathParam("bindingProtocol"));
                return CompletableFuture.completedFuture(configurator.idpConfigs().get(idpEntityId));
            }).requestIdManager(SamlServiceProviderTest.requestIdManager).build();
            sb.service(sp.newSamlService()).annotatedService("/", new Object() {
                @Get("/{bindingProtocol}")
                public String root() {
                    return "authenticated";
                }
            }, sp.newSamlDecorator());
        }
    };

    static class CookieBasedAuthorizer implements Authorizer<HttpRequest> {
        private final String cookieName;

        private final String cookieValue;

        CookieBasedAuthorizer(String cookieName, String cookieValue) {
            this.cookieName = Objects.requireNonNull(cookieName, "cookieName");
            this.cookieValue = Objects.requireNonNull(cookieValue, "cookieValue");
        }

        @Override
        public CompletionStage<Boolean> authorize(ServiceRequestContext ctx, HttpRequest req) {
            final String value = req.headers().get(COOKIE);
            if (value == null) {
                return CompletableFuture.completedFuture(false);
            }
            // Authentication will be succeeded only if both the specified cookie name and value are matched.
            final Set<Cookie> cookies = STRICT.decode(value);
            final boolean result = cookies.stream().anyMatch(( cookie) -> (cookieName.equals(cookie.name())) && (cookieValue.equals(cookie.value())));
            return CompletableFuture.completedFuture(result);
        }
    }

    static class CookieBasedSsoHandler implements SamlSingleSignOnHandler {
        private final String setCookie;

        CookieBasedSsoHandler(String cookieName, String cookieValue) {
            Objects.requireNonNull(cookieName, "cookieName");
            Objects.requireNonNull(cookieValue, "cookieValue");
            final Cookie cookie = new DefaultCookie(cookieName, cookieValue);
            cookie.setDomain(SamlServiceProviderTest.spHostname);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            setCookie = ServerCookieEncoder.STRICT.encode(cookie);
        }

        @Override
        public CompletionStage<Void> beforeInitiatingSso(ServiceRequestContext ctx, HttpRequest req, MessageContext<AuthnRequest> message, SamlIdentityProviderConfig idpConfig) {
            message.getSubcontext(SAMLBindingContext.class, true).setRelayState(req.path());
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public HttpResponse loginSucceeded(ServiceRequestContext ctx, AggregatedHttpMessage req, MessageContext<Response> message, @Nullable
        String sessionIndex, @Nullable
        String relayState) {
            return HttpResponse.of(HttpRedirectBindingUtil.headersWithLocation(MoreObjects.firstNonNull(relayState, "/")).add(SET_COOKIE, setCookie));
        }

        @Override
        public HttpResponse loginFailed(ServiceRequestContext ctx, AggregatedHttpMessage req, @Nullable
        MessageContext<Response> message, Throwable cause) {
            // Handle as an error so that a test client can detect the failure.
            return HttpResponse.of(BAD_REQUEST);
        }
    }

    static class SequentialRequestIdManager implements SamlRequestIdManager {
        private final AtomicInteger id = new AtomicInteger();

        @Override
        public String newId() {
            return String.valueOf(id.getAndIncrement());
        }

        @Override
        public boolean validateId(String id) {
            try {
                Integer.parseInt(id);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    final HttpClient client = HttpClient.of(SamlServiceProviderTest.rule.uri("/"), DEFAULT);

    @Test
    public void shouldRespondAuthnRequest_HttpRedirect() throws Exception {
        final AggregatedHttpMessage resp = client.get("/redirect").aggregate().join();
        assertThat(resp.status()).isEqualTo(FOUND);
        // Check the order of the parameters in the quest string.
        final String location = resp.headers().get(LOCATION);
        final Pattern p = Pattern.compile(("http://idp\\.example\\.com/saml/sso/redirect\\?" + "SAMLRequest=([^&]+)&RelayState=([^&]+)&SigAlg=([^&]+)&Signature=(.+)$"));
        assertThat(p.matcher(location).matches()).isTrue();
        final QueryStringDecoder decoder = new QueryStringDecoder(location, true);
        assertThat(decoder.parameters().get(SamlHttpParameterNames.SIGNATURE_ALGORITHM).get(0)).isEqualTo(SamlServiceProviderTest.signatureAlgorithm);
    }

    @Test
    public void shouldRespondAuthnRequest_HttpPost() throws Exception {
        final AggregatedHttpMessage resp = client.get("/post").aggregate().join();
        assertThat(resp.status()).isEqualTo(OK);
        assertThat(resp.contentType()).isEqualTo(HTML_UTF_8);
        final Document doc = Jsoup.parse(resp.contentUtf8());
        assertThat(doc.body().attr("onLoad")).isEqualTo("document.forms[0].submit()");
        // SAMLRequest will be posted to the IdP's SSO URL.
        final Element form = doc.body().child(0);
        assertThat(form.attr("method")).isEqualTo("post");
        assertThat(form.attr("action")).isEqualTo("http://idp.example.com/saml/sso/post");
        assertThat(form.child(0).attr("name")).isEqualTo(SamlHttpParameterNames.SAML_REQUEST);
        assertThat(form.child(1).attr("name")).isEqualTo(SamlHttpParameterNames.RELAY_STATE);
    }

    @Test
    public void shouldBeAlreadyAuthenticated() throws Exception {
        final HttpHeaders req = HttpHeaders.of(GET, "/redirect").add(COOKIE, "test=test");
        final AggregatedHttpMessage resp = client.execute(req).aggregate().join();
        assertThat(resp.status()).isEqualTo(OK);
        assertThat(resp.contentUtf8()).isEqualTo("authenticated");
    }

    @Test
    public void shouldRespondMetadataWithoutAuthentication() throws Exception {
        final AggregatedHttpMessage resp = client.get("/saml/metadata").aggregate().join();
        assertThat(resp.status()).isEqualTo(OK);
        assertThat(resp.contentType()).isEqualTo(SamlMetadataServiceFunction.CONTENT_TYPE_SAML_METADATA);
        final EntityDescriptor metadata = ((EntityDescriptor) (SamlMessageUtil.deserialize(resp.contentUtf8().getBytes())));
        assertThat(metadata).isNotNull();
        final SPSSODescriptor sp = metadata.getSPSSODescriptor(SAML20P_NS);
        assertThat(sp.isAuthnRequestsSigned()).isTrue();
        assertThat(sp.getWantAssertionsSigned()).isTrue();
        final List<KeyDescriptor> kd = sp.getKeyDescriptors();
        assertThat(kd.get(0).getUse().name()).isEqualToIgnoringCase("signing");
        assertThat(kd.get(1).getUse().name()).isEqualToIgnoringCase("encryption");
        final List<SingleLogoutService> slo = sp.getSingleLogoutServices();
        assertThat(slo.get(0).getLocation()).isEqualTo((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/slo/post"));
        assertThat(slo.get(0).getBinding()).isEqualTo(SAML2_POST_BINDING_URI);
        assertThat(slo.get(1).getLocation()).isEqualTo((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/slo/redirect"));
        assertThat(slo.get(1).getBinding()).isEqualTo(SAML2_REDIRECT_BINDING_URI);
        final List<AssertionConsumerService> acs = sp.getAssertionConsumerServices();
        // index 0 (default)
        assertThat(acs.get(0).getIndex()).isEqualTo(0);
        assertThat(acs.get(0).isDefault()).isTrue();
        assertThat(acs.get(0).getLocation()).isEqualTo((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/acs/post"));
        assertThat(acs.get(0).getBinding()).isEqualTo(SAML2_POST_BINDING_URI);
        // index 1
        assertThat(acs.get(1).getIndex()).isEqualTo(1);
        assertThat(acs.get(1).isDefault()).isFalse();
        assertThat(acs.get(1).getLocation()).isEqualTo((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/acs/redirect"));
        assertThat(acs.get(1).getBinding()).isEqualTo(SAML2_REDIRECT_BINDING_URI);
    }

    @Test
    public void shouldConsumeAssertion_HttpPost() throws Exception {
        final Response response = getAuthResponse((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/acs/post"));
        final AggregatedHttpMessage msg = sendViaHttpPostBindingProtocol("/saml/acs/post", SamlHttpParameterNames.SAML_RESPONSE, response);
        assertThat(msg.status()).isEqualTo(FOUND);
        assertThat(msg.headers().get(LOCATION)).isEqualTo("/");
    }

    @Test
    public void shouldConsumeAssertion_HttpRedirect() throws Exception {
        final Response response = getAuthResponse((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/acs/redirect"));
        final AggregatedHttpMessage msg = sendViaHttpRedirectBindingProtocol("/saml/acs/redirect", SamlHttpParameterNames.SAML_RESPONSE, response);
        assertThat(msg.status()).isEqualTo(FOUND);
        assertThat(msg.headers().get(LOCATION)).isEqualTo("/");
    }

    @Test
    public void shouldConsumeLogoutRequest_HttpPost() throws Exception {
        final LogoutRequest logoutRequest = getLogoutRequest((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/slo/post"), "http://idp.example.com/post");
        final AggregatedHttpMessage msg = sendViaHttpPostBindingProtocol("/saml/slo/post", SamlHttpParameterNames.SAML_REQUEST, logoutRequest);
        assertThat(msg.status()).isEqualTo(OK);
        assertThat(msg.contentType()).isEqualTo(HTML_UTF_8);
        final Document doc = Jsoup.parse(msg.contentUtf8());
        assertThat(doc.body().attr("onLoad")).isEqualTo("document.forms[0].submit()");
        // SAMLResponse will be posted to the IdP's logout response URL.
        final Element form = doc.body().child(0);
        assertThat(form.attr("method")).isEqualTo("post");
        assertThat(form.attr("action")).isEqualTo("http://idp.example.com/saml/slo/post");
        assertThat(form.child(0).attr("name")).isEqualTo(SamlHttpParameterNames.SAML_RESPONSE);
    }

    @Test
    public void shouldConsumeLogoutRequest_HttpRedirect() throws Exception {
        final LogoutRequest logoutRequest = getLogoutRequest((((("http://" + (SamlServiceProviderTest.spHostname)) + ':') + (SamlServiceProviderTest.rule.httpPort())) + "/saml/slo/redirect"), "http://idp.example.com/redirect");
        final AggregatedHttpMessage msg = sendViaHttpRedirectBindingProtocol("/saml/slo/redirect", SamlHttpParameterNames.SAML_REQUEST, logoutRequest);
        assertThat(msg.status()).isEqualTo(FOUND);
        // Check the order of the parameters in the quest string.
        final String location = msg.headers().get(LOCATION);
        final Pattern p = Pattern.compile(("http://idp\\.example\\.com/saml/slo/redirect\\?" + "SAMLResponse=([^&]+)&SigAlg=([^&]+)&Signature=(.+)$"));
        assertThat(p.matcher(location).matches()).isTrue();
    }
}

