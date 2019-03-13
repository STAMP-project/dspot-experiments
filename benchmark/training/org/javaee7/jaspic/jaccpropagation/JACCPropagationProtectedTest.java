package org.javaee7.jaspic.jaccpropagation;


import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that the established authenticated identity set from JASPIC propagates correctly
 * to a JACC provider.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class JACCPropagationProtectedTest extends ArquillianBase {
    @Test
    public void callingJACCWhenAuthenticated() {
        String response = getFromServerPath("protected/servlet?doLogin=true");
        // This can basically only fail if JACC itself somehow doesn't work.
        // Unfortunately this is the case for a bunch of certified servers, which
        // either demand some activation of JACC, or don't ship with a default
        // provider at all (which are both spec violations)
        Assert.assertFalse("JACC doesn't seem to be available.", response.contains("JACC doesn't seem to be available."));
        // Test if we have access to protected/servlet from within that servlet.
        // If this fails role propagation and/or JACC failed, since this is obviously
        // impossible.
        Assert.assertTrue(("Did not have access to protected servlet from within that Servlet. " + (" Perhaps the roles did not propogate from JASPIC to JACC and the" + " server didn't use JACC to grant access to invoking said Servlet?")), response.contains("Has access to /protected/servlet: true"));
    }
}

