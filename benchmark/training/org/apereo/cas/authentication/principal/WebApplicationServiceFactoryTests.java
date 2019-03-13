package org.apereo.cas.authentication.principal;


import CasProtocolConstants.PARAMETER_METHOD;
import CasProtocolConstants.PARAMETER_SERVICE;
import CasProtocolConstants.PARAMETER_TARGET_SERVICE;
import CasProtocolConstants.PARAMETER_TICKET;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * Test cases for {@link WebApplicationServiceFactory}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public class WebApplicationServiceFactoryTests {
    @Test
    public void verifyServiceCreationSuccessfullyById() {
        val request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(request, new MockHttpServletResponse()));
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService("testservice");
        Assertions.assertNotNull(service);
    }

    @Test
    public void verifyServiceCreationSuccessfullyByService() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, "test");
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(request);
        Assertions.assertNotNull(service);
        Assertions.assertEquals(PARAMETER_SERVICE, service.getSource());
    }

    @Test
    public void verifyServiceCreationSuccessfullyByTargetService() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_TARGET_SERVICE, "test");
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(request);
        Assertions.assertNotNull(service);
        Assertions.assertEquals(PARAMETER_TARGET_SERVICE, service.getSource());
    }

    @Test
    public void verifyServiceCreationSuccessfullyByTargetServiceAndTicket() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_TARGET_SERVICE, "test");
        request.addParameter(PARAMETER_TICKET, "ticket");
        request.addParameter(PARAMETER_METHOD, "post");
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(request);
        Assertions.assertNotNull(service);
        Assertions.assertEquals("ticket", service.getArtifactId());
    }

    @Test
    public void verifyServiceCreationNoService() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_TICKET, "ticket");
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(request);
        Assertions.assertNull(service);
    }

    @Test
    public void verifyServiceCreationNoRequest() {
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService("testservice");
        Assertions.assertNotNull(service);
    }
}

