package org.apereo.cas.authentication.principal;


import CasProtocolConstants.PARAMETER_SERVICE;
import Response.ResponseType.REDIRECT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.DefaultServicesManager;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServiceRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @author Arnaud Lesueur
 * @since 3.1
 */
public class SimpleWebApplicationServiceImplTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "simpleWebApplicationServiceImpl.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String SERVICE = "service";

    @Test
    public void verifySerializeACompletePrincipalToJson() throws IOException {
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, SimpleWebApplicationServiceImplTests.SERVICE);
        val serviceWritten = new WebApplicationServiceFactory().createService(request);
        SimpleWebApplicationServiceImplTests.MAPPER.writeValue(SimpleWebApplicationServiceImplTests.JSON_FILE, serviceWritten);
        val serviceRead = SimpleWebApplicationServiceImplTests.MAPPER.readValue(SimpleWebApplicationServiceImplTests.JSON_FILE, SimpleWebApplicationServiceImpl.class);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }

    @Test
    public void verifyResponse() {
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, SimpleWebApplicationServiceImplTests.SERVICE);
        val impl = new WebApplicationServiceFactory().createService(request);
        val response = new WebApplicationServiceResponseBuilder(new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(impl, "ticketId", RegisteredServiceTestUtils.getAuthentication());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(REDIRECT, response.getResponseType());
    }

    @Test
    public void verifyCreateSimpleWebApplicationServiceImplFromServiceAttribute() {
        val request = new MockHttpServletRequest();
        request.setAttribute(PARAMETER_SERVICE, SimpleWebApplicationServiceImplTests.SERVICE);
        val impl = new WebApplicationServiceFactory().createService(request);
        Assertions.assertNotNull(impl);
    }

    @Test
    public void verifyResponseForJsession() {
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, "http://www.cnn.com/;jsession=test");
        val impl = new WebApplicationServiceFactory().createService(request);
        Assertions.assertEquals("http://www.cnn.com/", impl.getId());
    }

    @Test
    public void verifyResponseWithNoTicket() {
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, SimpleWebApplicationServiceImplTests.SERVICE);
        val impl = new WebApplicationServiceFactory().createService(request);
        val response = new WebApplicationServiceResponseBuilder(new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(impl, null, RegisteredServiceTestUtils.getAuthentication());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(REDIRECT, response.getResponseType());
        Assertions.assertFalse(response.getUrl().contains("ticket="));
    }

    @Test
    public void verifyResponseWithNoTicketAndNoParameterInServiceURL() {
        val request = new MockHttpServletRequest();
        request.setParameter(SimpleWebApplicationServiceImplTests.SERVICE, "http://foo.com/");
        val impl = new WebApplicationServiceFactory().createService(request);
        val response = new WebApplicationServiceResponseBuilder(new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(impl, null, RegisteredServiceTestUtils.getAuthentication());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(REDIRECT, response.getResponseType());
        Assertions.assertFalse(response.getUrl().contains("ticket="));
        Assertions.assertEquals("http://foo.com/", response.getUrl());
    }

    @Test
    public void verifyResponseWithNoTicketAndOneParameterInServiceURL() {
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, "http://foo.com/?param=test");
        val impl = new WebApplicationServiceFactory().createService(request);
        val response = new WebApplicationServiceResponseBuilder(new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(impl, null, RegisteredServiceTestUtils.getAuthentication());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(REDIRECT, response.getResponseType());
        Assertions.assertEquals("http://foo.com/?param=test", response.getUrl());
    }
}

