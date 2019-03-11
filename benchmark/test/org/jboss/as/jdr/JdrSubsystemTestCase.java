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
package org.jboss.as.jdr;


import JdrReportExtension.SUBSYSTEM_NAME;
import Namespace.CURRENT;
import javax.xml.stream.XMLStreamException;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 * Performs basic parsing and configuration testing of the JDR subsystem.
 *
 * @author Mike M. Clark
 */
public class JdrSubsystemTestCase extends AbstractSubsystemBaseTest {
    public JdrSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new JdrReportExtension());
    }

    @Test(expected = XMLStreamException.class)
    public void testParseSubsystemWithBadChild() throws Exception {
        String subsystemXml = ("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\"><invalid/></subsystem>";
        parse(subsystemXml);
    }

    @Test(expected = XMLStreamException.class)
    public void testParseSubsystemWithBadAttribute() throws Exception {
        String subsystemXml = ("<subsystem xmlns=\"" + (CURRENT.getUriString())) + "\" attr=\"wrong\"/>";
        parse(subsystemXml);
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }
}

