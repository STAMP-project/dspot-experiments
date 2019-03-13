package org.apereo.cas.web.flow.client;


import lombok.val;
import org.apereo.cas.util.SchedulingUtils;
import org.apereo.cas.web.flow.AbstractSpnegoTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.test.MockRequestContext;


/**
 * Test cases for {@link LdapSpnegoKnownClientSystemsFilterAction}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Import(BaseLdapSpnegoKnownClientSystemsFilterActionTests.CasTestConfiguration.class)
public abstract class BaseLdapSpnegoKnownClientSystemsFilterActionTests extends AbstractSpnegoTests {
    @Test
    public void ensureLdapAttributeShouldDoSpnego() throws Exception {
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("localhost");
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = ldapSpnegoClientAction.execute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @TestConfiguration
    public static class CasTestConfiguration implements InitializingBean {
        @Autowired
        protected ApplicationContext applicationContext;

        @Override
        public void afterPropertiesSet() {
            SchedulingUtils.prepScheduledAnnotationBeanPostProcessor(applicationContext);
        }
    }
}

