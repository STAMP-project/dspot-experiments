package org.javaee7.jaspictest.customprincipal;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * Idential test as in basic-authentication, but now performed against a SAM which sets a custom principal.
 * Therefore tests that for this kind of usage of the PrincipalCallback JASPIC is stateless just as well.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class CustomPrincipalStatelessTest extends ArquillianBase {
    /**
     * Tests that access to a protected page does not depend on the authenticated identity that was established in a previous
     * request.
     */
    @Test
    public void testProtectedAccessIsStateless() throws IOException, SAXException {
        // -------------------- Request 1 ---------------------------
        // Accessing protected page without login
        String response = getFromServerPath("protected/servlet");
        // Not logged-in thus should not be accessible.
        Assert.assertFalse(response.contains("This is a protected servlet"));
        // -------------------- Request 2 ---------------------------
        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        // 
        // If the following fails but "testProtectedPageLoggedin" has succeeded,
        // the container has probably remembered the "unauthenticated identity", e.g. it has remembered that
        // we're not authenticated and it will deny further attempts to authenticate. This may happen when
        // the container does not correctly recognize the JASPIC protocol for "do nothing".
        response = getFromServerPath("protected/servlet?doLogin=true");
        // Now has to be logged-in so page is accessible
        Assert.assertTrue(("Could not access protected page, but should be able to. " + "Did the container remember the previously set 'unauthenticated identity'?"), response.contains("This is a protected servlet"));
        // -------------------- Request 3 ---------------------------
        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        // 
        // In the following method we do a call without logging in after one where we did login.
        // The container should not remember this login and has to deny access.
        response = getFromServerPath("protected/servlet");
        // Not logged-in thus should not be accessible.
        Assert.assertFalse(("Could access protected page, but should not be able to. " + "Did the container remember the authenticated identity that was set in previous request?"), response.contains("This is a protected servlet"));
    }

    /**
     * Tests that access to a protected page does not depend on the authenticated identity that was established in a previous
     * request, but use a different request order than the previous test.
     */
    @Test
    public void testProtectedAccessIsStateless2() throws IOException, SAXException {
        // -------------------- Request 1 ---------------------------
        // Start with doing a login
        String response = getFromServerPath("protected/servlet?doLogin=true");
        // -------------------- Request 2 ---------------------------
        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        // 
        // In the following method we do a call without logging in after one where we did login.
        // The container should not remember this login and has to deny access.
        // Accessing protected page without login
        response = getFromServerPath("protected/servlet");
        // Not logged-in thus should not be accessible.
        Assert.assertFalse(("Could access protected page, but should not be able to. " + "Did the container remember the authenticated identity that was set in previous request?"), response.contains("This is a protected servlet"));
    }

    @Test
    public void testPublicAccessIsStateless() throws IOException, SAXException {
        // -------------------- Request 1 ---------------------------
        String response = getFromServerPath("public/servlet");
        // Not logged-in
        Assert.assertTrue(response.contains("web username: null"));
        Assert.assertTrue(response.contains("web user has role \"architect\": false"));
        // -------------------- Request 2 ---------------------------
        response = getFromServerPath("public/servlet?doLogin=true");
        // Now has to be logged-in
        Assert.assertTrue("Username is not the expected one 'test'", response.contains("web username: test"));
        Assert.assertTrue("Username is correct, but the expected role 'architect' is not present.", response.contains("web user has role \"architect\": true"));
        // -------------------- Request 3 ---------------------------
        response = getFromServerPath("public/servlet");
        // Not logged-in
        Assert.assertTrue("Should not be authenticated, but username was not null. Did the container remember it from previous request?", response.contains("web username: null"));
        Assert.assertTrue("Request was not authenticated (username correctly null), but unauthenticated user incorrectly has role 'architect'", response.contains("web user has role \"architect\": false"));
    }

    /**
     * Tests independently from being able to access a protected resource if any details of a previously established
     * authenticated identity are remembered
     */
    @Test
    public void testProtectedThenPublicAccessIsStateless() throws IOException, SAXException {
        // -------------------- Request 1 ---------------------------
        // Accessing protected page with login
        String response = getFromServerPath("protected/servlet?doLogin=true");
        // -------------------- Request 2 ---------------------------
        // Accessing public page without login
        response = getFromServerPath("public/servlet");
        // No details should linger around
        Assert.assertFalse(("User principal was 'test', but it should be null here. " + "The container seemed to have remembered it from the previous request."), response.contains("web username: test"));
        Assert.assertTrue("User principal was not null, but it should be null here. ", response.contains("web username: null"));
        Assert.assertTrue(("The unauthenticated user has the role 'architect', which should not be the case. " + "The container seemed to have remembered it from the previous request."), response.contains("web user has role \"architect\": false"));
    }
}

