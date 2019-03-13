package org.apereo.cas.config;


import Attr_ReplyMessage.NAME;
import TestMultifactorAuthenticationProvider.ID;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.radius.RadiusClientProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.test.MockRequestContext;


/**
 *
 *
 * @author Jozef Kotlar
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasMultifactorAuthenticationWebflowConfiguration.class, RadiusConfiguration.class, CasCoreConfiguration.class, CasCoreUtilConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.radius.client.sharedSecret=NoSecret", "cas.authn.radius.client.inetAddress=localhost,localguest", "cas.authn.mfa.radius.id=" + (TestMultifactorAuthenticationProvider.ID) })
@Tag("Radius")
public class RadiusConfigurationTests {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("radiusConfiguration")
    private RadiusConfiguration radiusConfiguration;

    @Autowired
    @Qualifier("radiusAccessChallengedAuthenticationWebflowEventResolver")
    private CasWebflowEventResolver radiusAccessChallengedAuthenticationWebflowEventResolver;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void emptyAddress() {
        val clientProperties = new RadiusClientProperties();
        clientProperties.setInetAddress("  ");
        val ips = RadiusConfiguration.getClientIps(clientProperties);
        Assertions.assertEquals(0, ips.size());
    }

    @Test
    public void someAddressesWithSpaces() {
        val clientProperties = new RadiusClientProperties();
        clientProperties.setInetAddress("localhost,  localguest  ");
        val ips = RadiusConfiguration.getClientIps(clientProperties);
        Assertions.assertEquals(2, ips.size());
        Assertions.assertTrue(ips.contains("localhost"));
        Assertions.assertTrue(ips.contains("localguest"));
    }

    @Test
    public void radiusServer() {
        Assertions.assertNotNull(radiusConfiguration.radiusServer());
    }

    @Test
    public void radiusServers() {
        Assertions.assertEquals("localhost,localguest", casProperties.getAuthn().getRadius().getClient().getInetAddress());
        val servers = radiusConfiguration.radiusServers();
        Assertions.assertNotNull(servers);
        Assertions.assertEquals(2, servers.size());
    }

    @Test
    public void verifyAccessChallengedWebflowEventResolver() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        var result = radiusAccessChallengedAuthenticationWebflowEventResolver.resolve(context);
        Assertions.assertNull(result);
        val principal = CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap(NAME, "Reply-Back", Attr_State.NAME, "State".getBytes(StandardCharsets.UTF_8)));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(principal), context);
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val targetResolver = new org.springframework.webflow.engine.support.DefaultTargetStateResolver(TestMultifactorAuthenticationProvider.ID);
        val transition = new org.springframework.webflow.engine.Transition(new DefaultTransitionCriteria(new org.springframework.binding.expression.support.LiteralExpression(TestMultifactorAuthenticationProvider.ID)), targetResolver);
        context.getRootFlow().getGlobalTransitionSet().add(transition);
        result = radiusAccessChallengedAuthenticationWebflowEventResolver.resolve(context);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(ID, result.iterator().next().getId());
    }
}

