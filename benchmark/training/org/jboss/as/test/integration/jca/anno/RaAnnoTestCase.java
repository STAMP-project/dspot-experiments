/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jca.anno;


import ConnectorServices.IRONJACAMAR_MDR;
import Namespace.RESOURCEADAPTERS_1_0;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.connector.subsystems.resourceadapters.ResourceAdapterSubsystemParser;
import org.jboss.as.test.integration.jca.annorar.AnnoAdminObject;
import org.jboss.as.test.integration.jca.annorar.AnnoConnectionFactory;
import org.jboss.as.test.integration.jca.annorar.AnnoConnectionImpl;
import org.jboss.as.test.integration.jca.annorar.AnnoManagedConnectionFactory;
import org.jboss.as.test.integration.jca.annorar.AnnoMessageListener;
import org.jboss.as.test.integration.management.base.AbstractMgmtServerSetupTask;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.shared.FileUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.jca.common.metadata.spec.ConnectorImpl;
import org.jboss.jca.core.spi.mdr.MetadataRepository;
import org.jboss.logging.Logger;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Activation of annotated RA, overridden by descriptor
 *
 * @author <a href="mailto:vrastsel@redhat.com">Vladimir Rastseluev</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(RaAnnoTestCase.NoRaAnnoTestCaseSetup.class)
public class RaAnnoTestCase extends ContainerResourceMgmtTestBase {
    /**
     * The logger
     */
    private static Logger log = Logger.getLogger("RaAnnoTestCase");

    static class NoRaAnnoTestCaseSetup extends AbstractMgmtServerSetupTask {
        private ModelNode address;

        @Override
        public void doSetup(final ManagementClient managementClient) throws Exception {
            String xml = FileUtils.readFile(RaAnnoTestCase.class, "ra16anno.xml");
            List<ModelNode> operations = xmlToModelOperations(xml, RESOURCEADAPTERS_1_0.getUriString(), new ResourceAdapterSubsystemParser());
            address = operations.get(1).get("address");
            executeOperation(operationListToCompositeOperation(operations));
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            remove(address);
        }
    }

    /**
     * Resource
     */
    @Resource(mappedName = "java:/eis/ra16anno")
    private AnnoConnectionFactory connectionFactory1;

    /**
     * Resource
     */
    @Resource(mappedName = "java:/eis/ao/ra16anno")
    private AnnoAdminObject adminObject;

    @ArquillianResource
    ServiceContainer serviceContainer;

    /**
     * Test getConnection
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testGetConnection1() throws Throwable {
        Assert.assertNotNull(connectionFactory1);
        AnnoConnectionImpl connection1 = ((AnnoConnectionImpl) (connectionFactory1.getConnection()));
        Assert.assertNotNull(connection1);
        AnnoManagedConnectionFactory mcf = connection1.getMCF();
        Assert.assertNotNull(mcf);
        RaAnnoTestCase.log.trace(((((("MCF:" + mcf) + "//1//") + (mcf.getFirst())) + "//2//") + (mcf.getSecond())));
        Assert.assertEquals(((byte) (23)), ((byte) (mcf.getFirst())));
        Assert.assertEquals(((short) (55)), ((short) (mcf.getSecond())));
        connection1.close();
    }

    /**
     * Test admin objects
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testAdminOjbect() throws Throwable {
        Assert.assertNotNull(adminObject);
        RaAnnoTestCase.log.trace(((((("AO:" + (adminObject)) + "//1//") + (adminObject.getFirst())) + "//2//") + (adminObject.getSecond())));
        Assert.assertEquals(((long) (54321)), ((long) (adminObject.getFirst())));
        Assert.assertEquals(true, adminObject.getSecond());
    }

    /**
     * test activation 1
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testActivation1() throws Throwable {
        testActivation(AnnoMessageListener.class);
    }

    /**
     * Test metadata
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testMetaData() throws Throwable {
        ServiceController<?> controller = serviceContainer.getService(IRONJACAMAR_MDR);
        Assert.assertNotNull(controller);
        MetadataRepository mdr = ((MetadataRepository) (controller.getValue()));
        Assert.assertNotNull(mdr);
        Set<String> ids = mdr.getResourceAdapters();
        Assert.assertNotNull(ids);
        Assert.assertTrue(((ids.size()) > 0));
        String piId = getElementContaining(ids, "ra16anno");
        Assert.assertNotNull(mdr.getResourceAdapter(piId));
        Assert.assertTrue(((mdr.getResourceAdapter(piId)) instanceof ConnectorImpl));
    }
}

