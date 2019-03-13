/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.jaxrpc.security;


import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import stub.MyHelloService_Impl;


/**
 * This test demonstrates doing a SOAP request using client side generated stubs to a remote
 * JAX-RPC SOAP service that is protected by an authentication mechanism that requires an
 * encrypted username/password credential.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class HelloTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL url;

    @Test
    @RunAsClient
    public void testHelloStaticStub() throws MalformedURLException, RemoteException, ServiceException {
        stub.HelloService helloService = new MyHelloService_Impl().getHelloServicePort();
        ((Stub) (helloService))._setProperty(USERNAME_PROPERTY, "u1");
        ((Stub) (helloService))._setProperty(PASSWORD_PROPERTY, "p1");
        ((Stub) (helloService))._setProperty(ENDPOINT_ADDRESS_PROPERTY, ((url) + "hello"));
        String result = helloService.sayHello("Sailor");
        Assert.assertEquals("Hello Sailor", result);
    }
}

