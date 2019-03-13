package org.apereo.cas.support.openid.web.mvc;


import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.apereo.cas.support.openid.AbstractOpenIdTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test case of the Smart OpenId Controller.
 *
 * @author Frederic Esnault
 * @since 3.0.0
 */
public class SmartOpenIdControllerTests extends AbstractOpenIdTests {
    private static final String OPENID_MODE_PARAM = "openid.mode";

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final HttpServletResponse response = new MockHttpServletResponse();

    @Autowired
    @Qualifier("smartOpenIdAssociationController")
    private SmartOpenIdController smartOpenIdController;

    @Test
    public void verifyCanHandle() {
        request.addParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM, "associate");
        val canHandle = smartOpenIdController.canHandle(request, response);
        request.removeParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM);
        Assertions.assertTrue(canHandle);
    }

    @Test
    public void verifyCannotHandle() {
        request.addParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM, "anythingElse");
        val canHandle = smartOpenIdController.canHandle(request, response);
        request.removeParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM);
        Assertions.assertFalse(canHandle);
    }

    @Test
    public void verifyGetAssociationResponse() {
        request.addParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM, "associate");
        request.addParameter("openid.session_type", "DH-SHA1");
        request.addParameter("openid.assoc_type", "HMAC-SHA1");
        request.addParameter("openid.dh_consumer_public", ("NzKoFMyrzFn/5iJFPdX6MVvNA/BChV1/sJdnYbupDn7ptn+cerwEzyFfWFx25KsoLSkxQCaSMmYtc1GPy/2GI1BSKSDhpdJmDBb" + "QRa/9Gs+giV/5fHcz/mHz8sREc7RTGI+0Ka9230arwrWt0fnoaJLRKEGUsmFR71rCo4EUOew="));
        val assocResponse = smartOpenIdController.getAssociationResponse(request);
        Assertions.assertTrue(assocResponse.containsKey("assoc_handle"));
        Assertions.assertTrue(assocResponse.containsKey("expires_in"));
        Assertions.assertTrue(assocResponse.containsKey("dh_server_public"));
        Assertions.assertTrue(assocResponse.containsKey("enc_mac_key"));
        request.removeParameter(SmartOpenIdControllerTests.OPENID_MODE_PARAM);
    }
}

