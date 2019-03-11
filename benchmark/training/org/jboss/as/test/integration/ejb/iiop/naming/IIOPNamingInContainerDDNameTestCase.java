package org.jboss.as.test.integration.ejb.iiop.naming;


import java.rmi.RemoteException;
import java.util.Properties;
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
 * Tests that corba name lookups work from inside the AS itself - IIOP bean name defined in deployment descriptor
 *
 * @author <a href="mailto:tadamski@redhat.com">Tomasz Adamski</a>
 */
@RunWith(Arquillian.class)
public class IIOPNamingInContainerDDNameTestCase {
    @ArquillianResource
    private ManagementClient managementClient;

    /**
     * <p>
     * Tests the lookup of a bean that used the jboss-ejb3.xml deployment descriptor to override the COSNaming binding.
     * So, insteand of looking for the standard test2/IIOPNamingBean context we will look for the configured
     * bean/custom/name/IIOPNamingBean context.
     * </p>
     *
     * @throws NamingException
     * 		if an error occurs while looking up the bean.
     * @throws RemoteException
     * 		if an error occurs while invoking the remote bean.
     */
    @Test
    @OperateOnDeployment("test")
    public void testIIOPNamingInvocationWithDDOverride() throws RemoteException, NamingException {
        final Properties prope = new Properties();
        final InitialContext context = new InitialContext(prope);
        final Object iiopObj = context.lookup((("corbaname:iiop:" + (managementClient.getMgmtAddress())) + ":3528#bean/custom/name/IIOPNamingBean"));
        final IIOPNamingHome object = ((IIOPNamingHome) (PortableRemoteObject.narrow(iiopObj, IIOPNamingHome.class)));
        final IIOPRemote result = object.create();
        Assert.assertEquals("hello", result.hello());
    }
}

