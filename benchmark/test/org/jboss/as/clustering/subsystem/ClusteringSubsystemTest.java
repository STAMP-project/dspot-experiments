/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.clustering.subsystem;


import org.jboss.as.clustering.controller.Schema;
import org.jboss.as.controller.Extension;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 * Base class for clustering subsystem tests.
 *
 * @author Paul Ferraro
 */
public abstract class ClusteringSubsystemTest<S extends Schema<S>> extends AbstractSubsystemBaseTest {
    private final Schema<S> testSchema;

    private final Schema<S> currentSchema;

    private final String xmlPattern;

    private final String xsdPattern;

    protected ClusteringSubsystemTest(String name, Extension extension, Schema<S> testSchema, Schema<S> currentSchema, String xmlPattern, String xsdPattern) {
        super(name, extension);
        this.testSchema = testSchema;
        this.currentSchema = currentSchema;
        this.xmlPattern = xmlPattern;
        this.xsdPattern = xsdPattern;
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        if ((this.testSchema) == (this.currentSchema)) {
            super.testSchemaOfSubsystemTemplates();
        }
    }
}

