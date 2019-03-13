package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import CasWebflowConstants.TRANSITION_ID_SUCCESS_WITH_WARNINGS;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.DefaultMessageDescriptor;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationResultBuilder;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PrincipalElectionStrategy;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link CreateTicketGrantingTicketActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CreateTicketGrantingTicketActionTests extends AbstractWebflowActionsTests {
    @Autowired
    @Qualifier("createTicketGrantingTicketAction")
    private Action action;

    private MockRequestContext context;

    @Test
    public void verifyCreateTgt() throws Exception {
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        val builder = Mockito.mock(AuthenticationResultBuilder.class);
        val authentication = CoreAuthenticationTestUtils.getAuthentication();
        Mockito.when(builder.getInitialAuthentication()).thenReturn(Optional.of(authentication));
        Mockito.when(builder.collect(ArgumentMatchers.any(Authentication.class))).thenReturn(builder);
        val result = Mockito.mock(AuthenticationResult.class);
        Mockito.when(result.getAuthentication()).thenReturn(authentication);
        Mockito.when(builder.build(ArgumentMatchers.any(PrincipalElectionStrategy.class))).thenReturn(result);
        Mockito.when(builder.build(ArgumentMatchers.any(PrincipalElectionStrategy.class), ArgumentMatchers.any(Service.class))).thenReturn(result);
        WebUtils.putAuthenticationResultBuilder(builder, context);
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn("TGT-123456");
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.execute(this.context).getId());
        Mockito.when(tgt.getId()).thenReturn("TGT-111111");
        val handlerResult = new DefaultAuthenticationHandlerExecutionResult();
        handlerResult.getWarnings().addAll(CollectionUtils.wrapList(new DefaultMessageDescriptor("some.authn.message")));
        authentication.getSuccesses().putAll(CollectionUtils.wrap("handler", handlerResult));
        Mockito.when(tgt.getAuthentication()).thenReturn(authentication);
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS_WITH_WARNINGS, this.action.execute(this.context).getId());
    }
}

