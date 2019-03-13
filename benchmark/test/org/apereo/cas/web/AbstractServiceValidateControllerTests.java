package org.apereo.cas.web;


import CasProtocolConstants.PARAMETER_FORMAT;
import CasProtocolConstants.PARAMETER_PROXY_GRANTING_TICKET_IOU;
import CasProtocolConstants.PARAMETER_PROXY_GRANTING_TICKET_URL;
import CasProtocolConstants.PARAMETER_SERVICE;
import CasProtocolConstants.PARAMETER_TICKET;
import ValidationResponseType.JSON;
import java.util.Objects;
import lombok.val;
import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.mock.MockValidationSpecification;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.proxy.support.Cas10ProxyHandler;
import org.apereo.cas.web.config.CasProtocolViewsConfiguration;
import org.apereo.cas.web.config.CasValidationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Import({ CasProtocolViewsConfiguration.class, CasValidationConfiguration.class, ThymeleafAutoConfiguration.class })
public abstract class AbstractServiceValidateControllerTests extends AbstractCentralAuthenticationServiceTests {
    protected static final String SUCCESS = "Success";

    protected static final Service SERVICE = RegisteredServiceTestUtils.getService();

    private static final String GITHUB_URL = "https://www.github.com";

    protected AbstractServiceValidateController serviceValidateController;

    @Test
    public void verifyEmptyParams() throws Exception {
        Assertions.assertNotNull(this.serviceValidateController.handleRequestInternal(new MockHttpServletRequest(), new MockHttpServletResponse()).getModel().get("code"));
    }

    @Test
    public void verifyValidServiceTicketWithValidPgtAndProxyHandlerFailing() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET_URL, AbstractServiceValidateControllerTests.SERVICE.getId());
        this.serviceValidateController.setProxyHandler(( credential, proxyGrantingTicketId) -> null);
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertFalse(Objects.requireNonNull(modelAndView.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
        Assertions.assertNull(modelAndView.getModel().get(PARAMETER_PROXY_GRANTING_TICKET_IOU));
    }

    @Test
    public void verifyValidServiceTicket() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        val mv = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertTrue(Objects.requireNonNull(mv.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketInvalidSpec() throws Exception {
        Assertions.assertFalse(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(getHttpServletRequest(), new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyRenewSpecFailsCorrectly() throws Exception {
        Assertions.assertFalse(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(getHttpServletRequest(), new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyInvalidServiceTicket() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        getCentralAuthenticationService().destroyTicketGrantingTicket(tId.getId());
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        Assertions.assertFalse(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketWithValidPgtAndProxyHandling() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET_URL, AbstractServiceValidateControllerTests.SERVICE.getId());
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertTrue(Objects.requireNonNull(modelAndView.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
        Assertions.assertNotNull(modelAndView.getModel().get(PARAMETER_PROXY_GRANTING_TICKET_IOU));
    }

    @Test
    public void verifyValidServiceTicketAndPgtUrlMismatch() throws Exception {
        val svc = RegisteredServiceTestUtils.getService("proxyService");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), svc, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, svc.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET_URL, "http://www.github.com");
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertFalse(Objects.requireNonNull(modelAndView.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
        Assertions.assertNull(modelAndView.getModel().get(PARAMETER_PROXY_GRANTING_TICKET_IOU));
    }

    @Test
    public void verifyValidServiceTicketAndFormatAsJson() throws Exception {
        val svc = RegisteredServiceTestUtils.getService("proxyService");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), svc, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, svc.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_FORMAT, JSON.name());
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertTrue(Objects.requireNonNull(modelAndView.getView()).toString().contains("Json"));
    }

    @Test
    public void verifyValidServiceTicketAndBadFormat() throws Exception {
        val svc = RegisteredServiceTestUtils.getService("proxyService");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), svc, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, svc.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_FORMAT, "NOTHING");
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertTrue(Objects.requireNonNull(modelAndView.getView()).toString().contains("Success"));
    }

    @Test
    public void verifyValidServiceTicketRuntimeExceptionWithSpec() throws Exception {
        this.serviceValidateController.addValidationSpecification(new MockValidationSpecification(false));
        Assertions.assertFalse(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(getHttpServletRequest(), new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    /* CAS10 Proxying Tests. */
    @Test
    public void verifyValidServiceTicketWithDifferentEncoding() throws Exception {
        val svc = RegisteredServiceTestUtils.getService("http://www.jasig.org?param=hello+world");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), svc, ctx);
        val reqSvc = "http://www.jasig.org?param=hello%20world";
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService(reqSvc).getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        this.serviceValidateController.setProxyHandler(new Cas10ProxyHandler());
        Assertions.assertTrue(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketWithSecurePgtUrl() throws Exception {
        this.serviceValidateController.setProxyHandler(new Cas10ProxyHandler());
        val modelAndView = getModelAndViewUponServiceValidationWithSecurePgtUrl();
        Assertions.assertTrue(Objects.requireNonNull(modelAndView.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketWithValidPgtNoProxyHandling() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET_URL, AbstractServiceValidateControllerTests.SERVICE.getId());
        this.serviceValidateController.setProxyHandler(new Cas10ProxyHandler());
        Assertions.assertTrue(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketWithDifferentEncodingAndIgnoringCase() throws Exception {
        val origSvc = "http://www.jasig.org?param=hello+world";
        val svc = RegisteredServiceTestUtils.getService(origSvc);
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), svc, ctx);
        val reqSvc = "http://WWW.JASIG.ORG?PARAM=hello%20world";
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService(reqSvc).getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        this.serviceValidateController.setProxyHandler(new Cas10ProxyHandler());
        Assertions.assertTrue(Objects.requireNonNull(this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse()).getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
    }

    @Test
    public void verifyValidServiceTicketWithInvalidPgt() throws Exception {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), AbstractServiceValidateControllerTests.SERVICE);
        val tId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val sId = getCentralAuthenticationService().grantServiceTicket(tId.getId(), AbstractServiceValidateControllerTests.SERVICE, ctx);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, AbstractServiceValidateControllerTests.SERVICE.getId());
        request.addParameter(PARAMETER_TICKET, sId.getId());
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET_URL, "duh");
        this.serviceValidateController.setProxyHandler(new Cas10ProxyHandler());
        val modelAndView = this.serviceValidateController.handleRequestInternal(request, new MockHttpServletResponse());
        Assertions.assertTrue(Objects.requireNonNull(modelAndView.getView()).toString().contains(AbstractServiceValidateControllerTests.SUCCESS));
        Assertions.assertNull(modelAndView.getModel().get(PARAMETER_PROXY_GRANTING_TICKET_IOU));
    }
}

