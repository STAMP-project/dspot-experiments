package org.apereo.cas.web.flow.client;


import lombok.val;
import org.apereo.cas.util.RegexUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.test.MockRequestContext;


/**
 * Test cases for {@link BaseSpnegoKnownClientSystemsFilterAction}
 * and {@link HostNameSpnegoKnownClientSystemsFilterAction}.
 *
 * @author Sean Baker sean.baker@usuhs.edu
 * @author Misagh Moayyed
 * @since 4.1
 */
public class SpnegoKnownClientSystemsFilterActionTests {
    private static final String ALTERNATE_REMOTE_IP = "74.125.136.102";

    @Test
    public void ensureRemoteIpShouldBeChecked() {
        val action = new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"), "", 0);
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("192.158.5.781");
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureRemoteIpShouldNotBeChecked() {
        val action = new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"), "", 0);
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("193.158.5.781");
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertNotEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureAltRemoteIpHeaderShouldBeChecked() {
        val action = new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^74\\.125\\..+"), "alternateRemoteIp", 120);
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("555.555.555.555");
        req.addHeader("alternateRemoteIp", SpnegoKnownClientSystemsFilterActionTests.ALTERNATE_REMOTE_IP);
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureHostnameShouldDoSpnego() {
        val action = new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern(""), "", 0, "\\w+\\.\\w+\\.\\w+");
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(SpnegoKnownClientSystemsFilterActionTests.ALTERNATE_REMOTE_IP);
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureHostnameAndIpShouldDoSpnego() {
        val action = new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("74\\..+"), "", 0, "\\w+\\.\\w+\\.\\w+");
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(SpnegoKnownClientSystemsFilterActionTests.ALTERNATE_REMOTE_IP);
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void verifyIpMismatchWhenCheckingHostnameForSpnego() {
        val action = new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("14\\..+"), "", 0, "\\w+\\.\\w+\\.\\w+");
        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(SpnegoKnownClientSystemsFilterActionTests.ALTERNATE_REMOTE_IP);
        val extCtx = new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), req, new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);
        val ev = action.doExecute(ctx);
        Assertions.assertEquals(ev.getId(), new EventFactorySupport().no(this).getId());
    }
}

