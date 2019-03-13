/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.messaging.test;


import MessagingExtension.SUBSYSTEM_NAME;
import org.jboss.as.messaging.MessagingExtension;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.junit.Test;


/**
 * * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2012 Red Hat inc
 */
public class MessagingSubsystem30TestCase extends AbstractLegacySubsystemBaseTest {
    public MessagingSubsystem30TestCase() {
        super(SUBSYSTEM_NAME, new MessagingExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    // //////////////////////////////////////
    // Tests for EAP versions        //
    // //
    // put most recent version at the top //
    // //////////////////////////////////////
    @Test
    public void testTransformersEAP_6_4_0() throws Exception {
        testTransformers(EAP_6_4_0, MessagingExtension.VERSION_1_4_0, ModelFixers.PATH_FIXER);
    }

    @Test
    public void testRejectExpressionsEAP_6_4_0() throws Exception {
        KernelServicesBuilder builder = createKernelServicesBuilder(EAP_6_4_0, MessagingExtension.VERSION_1_4_0, ModelFixers.PATH_FIXER, "empty_subsystem_3_0.xml");
        doTestRejectExpressions_1_4_0(builder);
    }

    @Test
    public void testTransformersEAP_6_3_0() throws Exception {
        testTransformers(EAP_6_3_0, MessagingExtension.VERSION_1_3_0, ModelFixers.PATH_FIXER);
    }

    @Test
    public void testRejectExpressionsEAP_6_3_0() throws Exception {
        KernelServicesBuilder builder = createKernelServicesBuilder(EAP_6_3_0, MessagingExtension.VERSION_1_3_0, ModelFixers.PATH_FIXER, "empty_subsystem_3_0.xml");
        doTestRejectExpressions_1_3_0(builder);
    }

    @Test
    public void testTransformersEAP_6_2_0() throws Exception {
        testTransformers(EAP_6_2_0, MessagingExtension.VERSION_1_3_0, ModelFixers.PATH_FIXER);
    }

    @Test
    public void testRejectExpressionsEAP_6_2_0() throws Exception {
        KernelServicesBuilder builder = createKernelServicesBuilder(EAP_6_2_0, MessagingExtension.VERSION_1_3_0, ModelFixers.PATH_FIXER, "empty_subsystem_3_0.xml");
        doTestRejectExpressions_1_3_0(builder);
    }
}

