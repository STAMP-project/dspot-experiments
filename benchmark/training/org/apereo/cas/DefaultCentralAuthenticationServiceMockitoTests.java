package org.apereo.cas;


import java.util.stream.IntStream;
import lombok.val;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.UnauthorizedProxyingException;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatcher;


/**
 * Unit tests with the help of Mockito framework.
 *
 * @author Dmitriy Kopylenko
 * @since 3.0.0
 */
public class DefaultCentralAuthenticationServiceMockitoTests extends BaseCasCoreTests {
    private static final String TGT_ID = "tgt-id";

    private static final String TGT2_ID = "tgt2-id";

    private static final String ST_ID = "st-id";

    private static final String ST2_ID = "st2-id";

    private static final String SVC1_ID = "test1";

    private static final String SVC2_ID = "test2";

    private static final String PRINCIPAL = "principal";

    private DefaultCentralAuthenticationService cas;

    private Authentication authentication;

    private TicketRegistry ticketRegMock;

    @Test
    public void verifyNonExistentServiceWhenDelegatingTicketGrantingTicket() {
        Assertions.assertThrows(InvalidTicketException.class, () -> cas.createProxyGrantingTicket("bad-st", getAuthenticationContext()));
    }

    @Test
    public void verifyInvalidServiceWhenDelegatingTicketGrantingTicket() {
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> this.cas.createProxyGrantingTicket(DefaultCentralAuthenticationServiceMockitoTests.ST_ID, getAuthenticationContext()));
    }

    @Test
    public void disallowVendingServiceTicketsWhenServiceIsNotAllowedToProxyCAS1019() {
        Assertions.assertThrows(UnauthorizedProxyingException.class, () -> this.cas.grantServiceTicket(DefaultCentralAuthenticationServiceMockitoTests.TGT_ID, RegisteredServiceTestUtils.getService(DefaultCentralAuthenticationServiceMockitoTests.SVC1_ID), getAuthenticationContext()));
    }

    @Test
    public void getTicketGrantingTicketIfTicketIdIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> this.cas.getTicket(null, TicketGrantingTicket.class));
    }

    @Test
    public void getTicketGrantingTicketIfTicketIdIsMissing() {
        Assertions.assertThrows(InvalidTicketException.class, () -> this.cas.getTicket("TGT-9000", TicketGrantingTicket.class));
    }

    @Test
    public void getTicketsWithNoPredicate() {
        val c = this.cas.getTickets(( ticket) -> true);
        Assertions.assertEquals(c.size(), this.ticketRegMock.getTickets().size());
    }

    @Test
    public void verifyChainedAuthenticationsOnValidation() {
        val svc = RegisteredServiceTestUtils.getService(DefaultCentralAuthenticationServiceMockitoTests.SVC2_ID);
        val st = this.cas.grantServiceTicket(DefaultCentralAuthenticationServiceMockitoTests.TGT2_ID, svc, getAuthenticationContext());
        Assertions.assertNotNull(st);
        val assertion = this.cas.validateServiceTicket(st.getId(), svc);
        Assertions.assertNotNull(assertion);
        Assertions.assertEquals(assertion.getService(), svc);
        Assertions.assertEquals(DefaultCentralAuthenticationServiceMockitoTests.PRINCIPAL, assertion.getPrimaryAuthentication().getPrincipal().getId());
        Assertions.assertSame(2, assertion.getChainedAuthentications().size());
        IntStream.range(0, assertion.getChainedAuthentications().size()).forEach(( i) -> assertEquals(assertion.getChainedAuthentications().get(i), authentication));
    }

    private static class VerifyServiceByIdMatcher implements ArgumentMatcher<Service> {
        private final String id;

        VerifyServiceByIdMatcher(final String id) {
            this.id = id;
        }

        @Override
        public boolean matches(final Service s) {
            return (s != null) && (s.getId().equals(this.id));
        }
    }
}

