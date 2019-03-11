/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.iiop.openjdk;


import IIOPExtension.SUBSYSTEM_NAME;
import Namespace.CURRENT;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * <?>
 * IIOP subsystem tests.
 * </?>
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="sguilhen@jboss.com">Stefan Guilhen</a>
 * @author <a href="mailto:tadamski@redhat.com">Tomasz Adamski</a>
 */
public class IIOPSubsystemTestCase extends AbstractSubsystemBaseTest {
    public IIOPSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new IIOPExtension());
    }

    @Test
    public void testExpressions() throws Exception {
        standardSubsystemTest("expressions.xml");
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
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
        subsystemXml = ((((("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\">") + "    <orb>") + "        <poa/>") + "    </orb>") + "</subsystem>";
        try {
            super.parse(subsystemXml);
            Assert.fail("Should not have parsed bad child");
        } catch (XMLStreamException expected) {
        }
    }

    @Test
    public void testSubsystemWithSecurityIdentity() throws Exception {
        super.standardSubsystemTest("subsystem-security-identity.xml");
    }

    @Test
    public void testSubsystemWithSecurityClient() throws Exception {
        super.standardSubsystemTest("subsystem-security-client.xml");
    }

    @Test
    public void testSubsystem_1_0() throws Exception {
        standardSubsystemTest("subsystem-1.0.xml", false);
    }
}

