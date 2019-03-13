package org.apereo.cas.web.flow;


import PasswordlessAuthenticationWebflowConfigurer.TRANSITION_ID_PASSWORDLESS_GET_USERID;
import lombok.val;
import org.apereo.cas.api.PasswordlessUserAccount;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link PrepareForPasswordlessAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PrepareForPasswordlessAuthenticationActionTests extends BasePasswordlessAuthenticationActionTests {
    @Autowired
    @Qualifier("initializeLoginAction")
    private Action initializeLoginAction;

    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        Assertions.assertEquals(TRANSITION_ID_PASSWORDLESS_GET_USERID, initializeLoginAction.execute(context).getId());
        WebUtils.putPasswordlessAuthenticationAccount(context, new PasswordlessUserAccount("casuser", "email", "phone", "casuser"));
        Assertions.assertEquals("success", initializeLoginAction.execute(context).getId());
    }
}

