package org.jboss.as.test.integration.sar.context.classloader;


import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that the MBean instance lifecycle has the correct TCCL set. The TCCL is expected to be the classloader of the deployment through which the MBean was deployed.
 *
 * @unknown Jaikiran Pai
 * @see https://issues.jboss.org/browse/WFLY-822
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MBeanTCCLTestCase {
    private static final String EAR_NAME = "tccl-mbean-test-app";

    private static final String SAR_NAME = "tccl-mbean-test-sar";

    @ContainerResource
    private ManagementClient managementClient;

    private JMXConnector connector;

    /**
     * Tests the MBean was deployed successfully and can be invoked. The fact that the MBean deployed successfully is a sign that the TCCL access from within the MBean code, worked fine
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTCCLInMBeanInvocation() throws Exception {
        final MBeanServerConnection mBeanServerConnection = this.getMBeanServerConnection();
        final ObjectName mbeanObjectName = new ObjectName("wildfly:name=tccl-test-mbean");
        final int num1 = 3;
        final int num2 = 4;
        // invoke the operation on MBean
        final Integer sum = ((Integer) (mBeanServerConnection.invoke(mbeanObjectName, "add", new Object[]{ num1, num2 }, new String[]{ Integer.TYPE.getName(), Integer.TYPE.getName() })));
        Assert.assertEquals(("Unexpected return value from MBean: " + mbeanObjectName), (num1 + num2), ((int) (sum)));
    }
}

