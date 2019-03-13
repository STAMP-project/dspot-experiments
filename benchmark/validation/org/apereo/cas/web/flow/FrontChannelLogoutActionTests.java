package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_FINISH;
import java.util.ArrayList;
import lombok.val;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.logout.FrontChannelLogoutAction;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.webflow.execution.RequestContext;


/**
 *
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class FrontChannelLogoutActionTests {
    private static final String FLOW_EXECUTION_KEY = "12234";

    private FrontChannelLogoutAction frontChannelLogoutAction;

    private RequestContext requestContext;

    @Mock
    private ServicesManager servicesManager;

    public FrontChannelLogoutActionTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyLogoutNoIndex() {
        WebUtils.putLogoutRequests(this.requestContext, new ArrayList(0));
        val event = this.frontChannelLogoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
    }
}

