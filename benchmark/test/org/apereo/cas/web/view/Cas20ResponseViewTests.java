package org.apereo.cas.web.view;


import CasProtocolConstants.VALIDATION_CAS_MODEL_PROXY_GRANTING_TICKET_IOU;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_CHAINED_AUTHENTICATIONS;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_PRIMARY_AUTHENTICATION;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL;
import RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.apereo.cas.authentication.DefaultAuthenticationAttributeReleasePolicy;
import org.apereo.cas.authentication.DefaultAuthenticationServiceSelectionPlan;
import org.apereo.cas.web.AbstractServiceValidateControllerTests;
import org.apereo.cas.web.view.attributes.NoOpProtocolAttributesRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.View;


/**
 * Unit tests for {@link Cas20ResponseView}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class Cas20ResponseViewTests extends AbstractServiceValidateControllerTests {
    @Autowired
    @Qualifier("cas3ServiceJsonView")
    private View cas3ServiceJsonView;

    @Autowired
    @Qualifier("cas2SuccessView")
    private View cas2SuccessView;

    @Autowired
    @Qualifier("cas2ServiceFailureView")
    private View cas2ServiceFailureView;

    @Test
    public void verifyView() throws Exception {
        val modelAndView = this.getModelAndViewUponServiceValidationWithSecurePgtUrl();
        val req = new org.springframework.mock.web.MockHttpServletRequest(new MockServletContext());
        req.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, new org.springframework.web.context.support.GenericWebApplicationContext(req.getServletContext()));
        val resp = new MockHttpServletResponse();
        val delegatedView = new View() {
            @Override
            public String getContentType() {
                return "text/html";
            }

            @Override
            public void render(final Map<String, ?> map, final HttpServletRequest request, final HttpServletResponse response) {
                map.forEach(request::setAttribute);
            }
        };
        val view = new Cas20ResponseView(true, null, null, delegatedView, new DefaultAuthenticationAttributeReleasePolicy("attribute"), new DefaultAuthenticationServiceSelectionPlan(), new NoOpProtocolAttributesRenderer());
        view.render(modelAndView.getModel(), req, resp);
        Assertions.assertNotNull(req.getAttribute(MODEL_ATTRIBUTE_NAME_CHAINED_AUTHENTICATIONS));
        Assertions.assertNotNull(req.getAttribute(MODEL_ATTRIBUTE_NAME_PRIMARY_AUTHENTICATION));
        Assertions.assertNotNull(req.getAttribute(MODEL_ATTRIBUTE_NAME_PRINCIPAL));
        Assertions.assertNotNull(req.getAttribute(VALIDATION_CAS_MODEL_PROXY_GRANTING_TICKET_IOU));
    }
}

