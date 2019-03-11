/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.jacorb;


import JacORBExtension.SUBSYSTEM_NAME;
import JacORBSubsystemParser.Namespace.CURRENT;
import JacORBSubsystemParser.Namespace.JacORB_1_0;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * <?> JacORB subsystem tests. </?>
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="sguilhen@jboss.com">Stefan Guilhen</a>
 */
// TODO WFCORE-1353 means this doesn't have to always fail now; consider just deleting this
// @Override
// protected void validateDescribeOperation(KernelServices hc, AdditionalInitialization serverInit, ModelNode expectedModel) throws Exception {
// final ModelNode operation = createDescribeOperation();
// final ModelNode result = hc.executeOperation(operation);
// Assert.assertTrue("The subsystem describe operation must fail",
// result.hasDefined(ModelDescriptionConstants.FAILURE_DESCRIPTION));
// }
public class JacORBSubsystemTestCase extends AbstractSubsystemBaseTest {
    public JacORBSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new JacORBExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testExpressions() throws Exception {
        standardSubsystemTest("expressions.xml");
    }

    @Test
    public void testParseEmptySubsystem() throws Exception {
        // parse the subsystem xml into operations.
        String subsystemXml = (("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\">") + "</subsystem>";
        List<ModelNode> operations = parse(subsystemXml);
        // check that we have the expected number of operations.
        Assert.assertEquals(1, operations.size());
        // check that each operation has the correct content.
        ModelNode addSubsystem = operations.get(0);
        Assert.assertEquals(ADD, addSubsystem.get(OP).asString());
        PathAddress addr = PathAddress.pathAddress(addSubsystem.get(OP_ADDR));
        Assert.assertEquals(1, addr.size());
        PathElement element = addr.getElement(0);
        Assert.assertEquals(SUBSYSTEM, element.getKey());
        Assert.assertEquals(SUBSYSTEM_NAME, element.getValue());
    }

    @Test
    public void testParseSubsystemWithBadChild() throws Exception {
        // try parsing a XML with an invalid element.
        String subsystemXml = ((("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\">") + "   <invalid/>") + "</subsystem>";
        try {
            super.parse(subsystemXml);
            Assert.fail("Should not have parsed bad child");
        } catch (XMLStreamException expected) {
        }
        // now try parsing a valid element in an invalid position.
        subsystemXml = "<subsystem xmlns=\"urn:jboss:domain:jacorb:1.0\">" + ((("    <orb name=\"JBoss\" print-version=\"off\">" + "        <poa/>") + "    </orb>") + "</subsystem>");
        try {
            super.parse(subsystemXml);
            Assert.fail("Should not have parsed bad child");
        } catch (XMLStreamException expected) {
        }
    }

    @Test
    public void testParseSubsystemWithBadAttribute() throws Exception {
        String subsystemXml = (("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\" bad=\"very_bad\">") + "</subsystem>";
        try {
            super.parse(subsystemXml);
            Assert.fail("Should not have parsed bad attribute");
        } catch (XMLStreamException expected) {
        }
    }

    // Tests for the version 1.0 of the JacORB subsystem configuration.
    @Test
    public void testParseSubsystem_1_0() throws Exception {
        List<ModelNode> operations = super.parse(ModelTestUtils.readResource(this.getClass(), "subsystem-1.0.xml"));
        // check that we have the expected number of operations.
        Assert.assertEquals(1, operations.size());
        // check that each operation has the correct content.
        ModelNode addSubsystem = operations.get(0);
        Assert.assertEquals(ADD, addSubsystem.get(OP).asString());
        PathAddress addr = PathAddress.pathAddress(addSubsystem.get(OP_ADDR));
        Assert.assertEquals(1, addr.size());
        PathElement element = addr.getElement(0);
        Assert.assertEquals(SUBSYSTEM, element.getKey());
        Assert.assertEquals(SUBSYSTEM_NAME, element.getValue());
    }

    @Test
    public void testParseEmptySubsystem_1_0() throws Exception {
        // parse the subsystem xml into operations.
        String subsystemXml = (("<subsystem xmlns=\"" + (JacORB_1_0.getUriString())) + "\">") + "</subsystem>";
        List<ModelNode> operations = parse(subsystemXml);
        // check that we have the expected number of operations.
        Assert.assertEquals(1, operations.size());
        // check that each operation has the correct content.
        ModelNode addSubsystem = operations.get(0);
        Assert.assertEquals(ADD, addSubsystem.get(OP).asString());
        PathAddress addr = PathAddress.pathAddress(addSubsystem.get(OP_ADDR));
        Assert.assertEquals(1, addr.size());
        PathElement element = addr.getElement(0);
        Assert.assertEquals(SUBSYSTEM, element.getKey());
        Assert.assertEquals(SUBSYSTEM_NAME, element.getValue());
    }

    @Test
    public void testParseSubsystemWithBadInitializer_1_0() throws Exception {
        String subsystemXml = ((("<subsystem xmlns=\"" + (JacORB_1_0.getUriString())) + "\">") + "   <initializers>invalid</initializers>") + "</subsystem>";
        try {
            super.parse(subsystemXml);
            Assert.fail("Should not have parsed bad initializer");
        } catch (XMLStreamException expected) {
        }
    }

    @Test
    public void testSubsystemWithSecurityIdentity() throws Exception {
        standardSubsystemTest("subsystem-security-identity.xml");
    }

    @Test
    public void testSubsystemWithSecurityClient() throws Exception {
        standardSubsystemTest("subsystem-security-client.xml");
    }

    @Test
    public void testSubsystemWithIORSettings() throws Exception {
        standardSubsystemTest("subsystem-ior-settings.xml");
    }
}

