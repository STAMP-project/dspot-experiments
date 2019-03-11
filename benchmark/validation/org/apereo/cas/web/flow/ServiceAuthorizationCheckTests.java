package org.apereo.cas.web.flow;


import lombok.val;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.webflow.test.MockRequestContext;


/**
 * Mockito based tests for @{link ServiceAuthorizationCheck}
 *
 * @author Dmitriy Kopylenko
 * @since 3.5.0
 */
public class ServiceAuthorizationCheckTests {
    private final WebApplicationService authorizedService = Mockito.mock(WebApplicationService.class);

    private final WebApplicationService unauthorizedService = Mockito.mock(WebApplicationService.class);

    private final WebApplicationService undefinedService = Mockito.mock(WebApplicationService.class);

    private final ServicesManager servicesManager = Mockito.mock(ServicesManager.class);

    private ServiceAuthorizationCheckAction serviceAuthorizationCheck;

    @Test
    public void noServiceProvided() {
        val mockRequestContext = new MockRequestContext();
        val event = this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        Assertions.assertEquals("success", event.getId());
    }

    @Test
    public void authorizedServiceProvided() {
        val mockRequestContext = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(mockRequestContext, authorizedService);
        val event = this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        Assertions.assertEquals("success", event.getId());
    }

    @Test
    public void unauthorizedServiceProvided() {
        val mockRequestContext = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(mockRequestContext, unauthorizedService);
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> this.serviceAuthorizationCheck.doExecute(mockRequestContext));
    }

    @Test
    public void serviceThatIsNotRegisteredProvided() {
        val mockRequestContext = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(mockRequestContext, undefinedService);
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> this.serviceAuthorizationCheck.doExecute(mockRequestContext));
    }
}

