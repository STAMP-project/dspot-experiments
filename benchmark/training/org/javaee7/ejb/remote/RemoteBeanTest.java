/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.ejb.remote;


import javax.naming.Context;
import javax.naming.NamingException;
import org.javaee7.RemoteEJBContextProvider;
import org.javaee7.ejb.remote.remote.BeanRemote;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This class demonstrates and tests how to request an EJB bean from a remote server.
 *
 * <p>
 * {@link RemoteEJBContextProvider} is used, which is a test artifact abstracting the different
 * ways this is done for different servers.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class RemoteBeanTest {
    private RemoteEJBContextProvider remoteEJBContextProvider;

    @Test
    @RunAsClient
    public void callProtectedRemoteBean() throws NamingException {
        // Obtain the JNDI naming context in a vendor specific way.
        Context ejbRemoteContext = remoteEJBContextProvider.getContextWithCredentialsSet("u1", "p1");
        BeanRemote beanRemote = ((BeanRemote) (ejbRemoteContext.lookup("java:global/test/Bean")));
        Assert.assertEquals("method", beanRemote.method());
    }
}

