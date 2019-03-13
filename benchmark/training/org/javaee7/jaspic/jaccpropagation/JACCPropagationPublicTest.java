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
public class JACCPropagationPublicTest extends ArquillianBase {
    @Test
    public void callingJACCWhenAuthenticated() {
        String response = getFromServerPath("public/servlet?doLogin=true");
        // This can basically only fail if JACC itself somehow doesn't work.
        // Unfortunately this is the case for a bunch of certified servers, which
        // either demand some activation of JACC, or don't ship with a default
        // provider at all (which are both spec violations)
        Assert.assertFalse("JACC doesn't seem to be available.", response.contains("JACC doesn't seem to be available."));
        // Test if we have access to public/servlet. This would be rare to fail
        Assert.assertTrue(("Did not have access to public servlet from within that Servlet. " + " Something is seriously wrong."), response.contains("Has access to /public/servlet: true"));
        // Test if we have access to protected/servlet. Since we authenticated with JASPIC
        // with a role that this path is protected with, we should have access if those
        // roles were indeed propagated correctly.
        Assert.assertTrue(("Did not have access to protected servlet from within public servlet. " + " Perhaps the roles did not propogate from JASPIC to JACC?"), response.contains("Has access to /protected/servlet: true"));
    }

    @Test
    public void callingJACCWhenNotAuthenticated() {
        String response = getFromServerPath("public/servlet");
        // This can basically only fail if JACC itself somehow doesn't work.
        // Unfortunately this is the case for a bunch of certified servers, which
        // either demand some activation of JACC, or don't ship with a default
        // provider at all (which are both spec violations)
        Assert.assertFalse("JACC doesn't seem to be available.", response.contains("JACC doesn't seem to be available."));
        // Test if we have access to public/servlet. This would be rare to fail
        Assert.assertTrue(("Did not have access to public servlet from within that Servlet. " + " Something is seriously wrong."), response.contains("Has access to /public/servlet: true"));
        // Test that we do NOT have access to protected/servlet. Passing this test
        // doesn't necessarily means JASPIC to JACC propagation works correctly, as it will also pass if
        // JACC doesn't work at all. Failing this test does indicate that something is wrong.
        Assert.assertTrue(("Has access to protected servlet from within public servlet without being authenticated. " + " This should not be the case."), response.contains("Has access to /protected/servlet: false"));
    }
}

