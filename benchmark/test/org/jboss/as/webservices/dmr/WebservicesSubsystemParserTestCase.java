/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.webservices.dmr;


import ModelTestControllerVersion.EAP_6_2_0;
import ModelTestControllerVersion.EAP_6_3_0;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import WSExtension.SUBSYSTEM_NAME;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * Checks the current parser can parse any webservices subsystem version of the model
 *
 * @author <a href="mailto:ema@rehdat.com>Jim Ma</a>
 * @author <a href="mailto:alessio.soldano@jboss.com>Alessio Soldano</a>
 */
public class WebservicesSubsystemParserTestCase extends AbstractSubsystemBaseTest {
    public WebservicesSubsystemParserTestCase() {
        super(SUBSYSTEM_NAME, new WSExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testParseV10() throws Exception {
        KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT).setSubsystemXmlResource("ws-subsystem.xml").build();
        ModelNode model = services.readWholeModel().get("subsystem", getMainSubsystemName());
        standardSubsystemTest("ws-subsystem.xml", false);
        checkSubsystemBasics(model);
    }

    @Test
    public void testParseV11() throws Exception {
        KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT).setSubsystemXmlResource("ws-subsystem11.xml").build();
        ModelNode model = services.readWholeModel().get("subsystem", getMainSubsystemName());
        standardSubsystemTest("ws-subsystem11.xml", false);
        checkSubsystemBasics(model);
        checkEndpointConfigs(model);
    }

    @Test
    public void testParseV12() throws Exception {
        KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT).setSubsystemXmlResource("ws-subsystem12.xml").build();
        ModelNode model = services.readWholeModel().get("subsystem", getMainSubsystemName());
        standardSubsystemTest("ws-subsystem12.xml", false);
        checkSubsystemBasics(model);
        checkEndpointConfigs(model);
        checkClientConfigs(model);
    }

    @Test
    public void testParseV20() throws Exception {
        // no need to do extra standardSubsystemTest("ws-subsystem20.xml") as that is default!
        KernelServices services = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT).setSubsystemXmlResource("ws-subsystem20.xml").build();
        ModelNode model = services.readWholeModel().get("subsystem", getMainSubsystemName());
        checkSubsystemBasics(model);
        checkEndpointConfigs(model);
        checkClientConfigs(model);
    }

    @Test
    public void testTransformersEAP620() throws Exception {
        testTransformers_1_2_0(EAP_6_2_0);
    }

    @Test
    public void testTransformersEAP630() throws Exception {
        testTransformers_1_2_0(EAP_6_3_0);
    }

    @Test
    public void testTransformersEAP640() throws Exception {
        testTransformers_1_2_0(EAP_6_4_0);
    }

    @Test
    public void testTransformersEAP700() throws Exception {
        testRejections_2_0_0(EAP_7_0_0);
    }
}

