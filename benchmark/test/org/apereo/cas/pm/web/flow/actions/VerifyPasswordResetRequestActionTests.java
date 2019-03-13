package org.apereo.cas.pm.web.flow.actions;


import HttpRequestUtils.USER_AGENT_HEADER;
import PasswordManagementWebflowUtils.FLOWSCOPE_PARAMETER_NAME_TOKEN;
import PasswordManagementWebflowUtils.REQUEST_PARAMETER_NAME_PASSWORD_RESET_TOKEN;
import java.io.Serializable;
import lombok.val;
import org.apereo.cas.pm.web.flow.PasswordManagementWebflowUtils;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link VerifyPasswordResetRequestActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfPortOpen(port = 25000)
@EnabledIfContinuousIntegration
@Tag("Mail")
public class VerifyPasswordResetRequestActionTests extends BasePasswordManagementActionTests {
    @Test
    public void verifyAction() {
        try {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("error", verifyPasswordResetRequestAction.execute(context).getId());
            request.setRemoteAddr("1.2.3.4");
            request.setLocalAddr("1.2.3.4");
            request.addHeader(USER_AGENT_HEADER, "test");
            ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
            val token = passwordManagementService.createToken("casuser");
            val transientFactory = ((TransientSessionTicketFactory) (this.ticketFactory.get(TransientSessionTicket.class)));
            val serverPrefix = casProperties.getServer().getPrefix();
            val service = webApplicationServiceFactory.createService(serverPrefix);
            val properties = CollectionUtils.<String, Serializable>wrap(FLOWSCOPE_PARAMETER_NAME_TOKEN, token);
            val ticket = transientFactory.create(service, properties);
            this.ticketRegistry.addTicket(ticket);
            request.addParameter(REQUEST_PARAMETER_NAME_PASSWORD_RESET_TOKEN, ticket.getId());
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", verifyPasswordResetRequestAction.execute(context).getId());
            Assertions.assertTrue(PasswordManagementWebflowUtils.isPasswordResetSecurityQuestionsEnabled(context));
            Assertions.assertNotNull(PasswordManagementWebflowUtils.getPasswordResetUsername(context));
            Assertions.assertNotNull(PasswordManagementWebflowUtils.getPasswordResetToken(context));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

