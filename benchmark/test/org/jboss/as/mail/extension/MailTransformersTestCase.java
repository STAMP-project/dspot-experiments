/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.mail.extension;


import MailExtension.SUBSYSTEM_NAME;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 *
 *
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
public class MailTransformersTestCase extends AbstractSubsystemBaseTest {
    public MailTransformersTestCase() {
        super(SUBSYSTEM_NAME, new MailExtension());
    }

    @Test
    public void testTransformerEAP700() throws Exception {
        testTransformation(EAP_7_0_0, MailTransformers.MODEL_VERSION_EAP70);
    }

    @Test
    public void testTransformerEAP640() throws Exception {
        testTransformation(EAP_6_4_0, MailTransformers.MODEL_VERSION_EAP6X);
    }

    @Test
    public void testRejectingTransformersEAP_7_0_0() throws Exception {
        testRejectingTransformers(EAP_7_0_0, MailTransformers.MODEL_VERSION_EAP70);
    }

    @Test
    public void testRejectingTransformersEAP_6_4_0() throws Exception {
        testRejectingTransformers(EAP_6_4_0, MailTransformers.MODEL_VERSION_EAP6X);
    }
}

