package com.vaadin.tests.server;


import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.ServerRpcHandler.RpcRequest;
import com.vaadin.tests.util.MockDeploymentConfiguration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the actual csrf token validation by the server.
 *
 * @author Vaadin Ltd
 */
public class CsrfTokenMissingTest {
    // Dummy fields just to run the test.
    private VaadinServlet mockServlet;

    // The mock deployment configuration.
    private MockDeploymentConfiguration mockDeploymentConfiguration;

    private VaadinServletService mockService;

    // The mock UI session.
    private VaadinSession mockSession;

    // The mock vaadin request.
    private VaadinServletRequest vaadinRequest;

    private enum TokenType {

        MISSING,
        INVALID,
        VALID;}

    private CsrfTokenMissingTest.TokenType tokenType;

    private String invalidToken;

    private static final Logger LOGGER = Logger.getLogger(CsrfTokenMissingTest.class.getName());

    static {
        CsrfTokenMissingTest.LOGGER.setLevel(Level.ALL);
    }

    @Test
    public void securityOnAndNoToken() {
        initTest(true, CsrfTokenMissingTest.TokenType.MISSING);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isDefaultToken(rpcRequest));
        Assert.assertFalse(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndNoToken() {
        initTest(false, CsrfTokenMissingTest.TokenType.MISSING);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isDefaultToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOnAndInvalidToken() {
        initTest(true, CsrfTokenMissingTest.TokenType.INVALID);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isInvalidToken(rpcRequest));
        Assert.assertFalse(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndInvalidToken() {
        initTest(false, CsrfTokenMissingTest.TokenType.INVALID);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isInvalidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOnAndValidToken() {
        initTest(true, CsrfTokenMissingTest.TokenType.VALID);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isValidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndValidToken() {
        initTest(false, CsrfTokenMissingTest.TokenType.VALID);
        RpcRequest rpcRequest = createRequest();
        Assert.assertTrue(isValidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }
}

