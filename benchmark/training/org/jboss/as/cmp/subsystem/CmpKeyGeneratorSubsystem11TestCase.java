/**
 * JBoss, Home of Professional Open Source.
 *  Copyright 2013, Red Hat, Inc., and individual contributors
 *  as indicated by the @author tags. See the copyright.txt file in the
 *  distribution for a full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * /
 */
package org.jboss.as.cmp.subsystem;


import java.util.List;
import org.jboss.as.controller.PathElement;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Manuel Fehlhammer
 */
public class CmpKeyGeneratorSubsystem11TestCase extends CmpKeyGeneratorSubsystem10TestCase {
    @Test
    public void testParseSubsystem() throws Exception {
        final List<ModelNode> operations = super.parse(getSubsystemXml("subsystem-cmp-key-generators_1_1.xml"));
        Assert.assertEquals(5, operations.size());
        assertOperation(operations.get(0), ADD, PathElement.pathElement(SUBSYSTEM, getMainSubsystemName()));
        assertOperation(operations.get(1), ADD, PathElement.pathElement(CmpSubsystemModel.UUID_KEY_GENERATOR, "uuid1"));
        final ModelNode uuid2 = operations.get(2);
        assertOperation(uuid2, ADD, PathElement.pathElement(CmpSubsystemModel.UUID_KEY_GENERATOR, "uuid2"));
        Assert.assertEquals("java:jboss/uuid2", uuid2.get(CmpSubsystemModel.JNDI_NAME).asString());
        final ModelNode hilo1 = operations.get(3);
        assertOperation(hilo1, ADD, PathElement.pathElement(CmpSubsystemModel.HILO_KEY_GENERATOR, "hilo1"));
        Assert.assertEquals("java:/jdbc/DB1", hilo1.get(CmpSubsystemModel.DATA_SOURCE).asString());
        Assert.assertEquals("HILOSEQUENCES1", hilo1.get(CmpSubsystemModel.TABLE_NAME).asString());
        Assert.assertEquals("SEQUENCENAME1", hilo1.get(CmpSubsystemModel.SEQUENCE_COLUMN).asString());
        Assert.assertEquals("HIGHVALUES1", hilo1.get(CmpSubsystemModel.ID_COLUMN).asString());
        Assert.assertEquals("create table HILOSEQUENCES1", hilo1.get(CmpSubsystemModel.CREATE_TABLE_DDL).asString());
        Assert.assertEquals("general1", hilo1.get(CmpSubsystemModel.SEQUENCE_NAME).asString());
        Assert.assertEquals(true, hilo1.get(CmpSubsystemModel.CREATE_TABLE).asBoolean());
        Assert.assertEquals(true, hilo1.get(CmpSubsystemModel.DROP_TABLE).asBoolean());
        Assert.assertEquals(10, hilo1.get(CmpSubsystemModel.BLOCK_SIZE).asLong());
        final ModelNode hilo2 = operations.get(4);
        assertOperation(hilo2, ADD, PathElement.pathElement(CmpSubsystemModel.HILO_KEY_GENERATOR, "hilo2"));
        Assert.assertEquals("java:jboss/hilo2", hilo2.get(CmpSubsystemModel.JNDI_NAME).asString());
        Assert.assertEquals("java:/jdbc/DB2", hilo2.get(CmpSubsystemModel.DATA_SOURCE).asString());
        Assert.assertEquals("HILOSEQUENCES2", hilo2.get(CmpSubsystemModel.TABLE_NAME).asString());
        Assert.assertEquals("SEQUENCENAME2", hilo2.get(CmpSubsystemModel.SEQUENCE_COLUMN).asString());
        Assert.assertEquals("HIGHVALUES2", hilo2.get(CmpSubsystemModel.ID_COLUMN).asString());
        Assert.assertEquals("create table HILOSEQUENCES2", hilo2.get(CmpSubsystemModel.CREATE_TABLE_DDL).asString());
        Assert.assertEquals("general2", hilo2.get(CmpSubsystemModel.SEQUENCE_NAME).asString());
        Assert.assertEquals(false, hilo2.get(CmpSubsystemModel.CREATE_TABLE).asBoolean());
        Assert.assertEquals(false, hilo2.get(CmpSubsystemModel.DROP_TABLE).asBoolean());
        Assert.assertEquals(11, hilo2.get(CmpSubsystemModel.BLOCK_SIZE).asLong());
    }
}

