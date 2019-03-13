package org.jboss.as.test.integration.ejb.iiop.naming;


import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Properties;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that corba name lookups work from inside the AS itself
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class IIOPNamingInContainerTestCase {
    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    @OperateOnDeployment("test")
    public void testIIOPNamingInvocation() throws RemoteException, NamingException {
        final Properties prope = new Properties();
        final InitialContext context = new InitialContext(prope);
        final Object iiopObj = context.lookup((("corbaname:iiop:" + (managementClient.getMgmtAddress())) + ":3528#IIOPNamingBean"));
        final IIOPNamingHome object = ((IIOPNamingHome) (PortableRemoteObject.narrow(iiopObj, IIOPNamingHome.class)));
        final IIOPRemote result = object.create();
        Assert.assertEquals("hello", result.hello());
    }

    @Test
    @OperateOnDeployment("test")
    public void testStatefulIIOPNamingInvocation() throws RemoteException, RemoveException, NamingException {
        final Properties prope = new Properties();
        final InitialContext context = new InitialContext(prope);
        final Object iiopObj = context.lookup((("corbaname:iiop:" + (managementClient.getMgmtAddress())) + ":3528#IIOPStatefulNamingBean"));
        final IIOPStatefulNamingHome object = ((IIOPStatefulNamingHome) (PortableRemoteObject.narrow(iiopObj, IIOPStatefulNamingHome.class)));
        final IIOPStatefulRemote result = object.create(10);
        Assert.assertEquals(11, result.increment());
        Assert.assertEquals(12, result.increment());
        remove();
        try {
            result.increment();
            Assert.fail("Expected NoSuchObjectException");
        } catch (NoSuchObjectException expected) {
        }
    }
}

