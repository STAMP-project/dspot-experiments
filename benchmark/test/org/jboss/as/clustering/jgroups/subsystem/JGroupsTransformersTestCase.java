/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.clustering.jgroups.subsystem;


import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import ModelTestControllerVersion.EAP_7_1_0;
import ModelTestControllerVersion.EAP_7_2_0;
import org.junit.Test;


/**
 * Test cases for transformers used in the JGroups subsystem.
 *
 * @author <a href="tomaz.cerar@redhat.com">Tomaz Cerar</a>
 * @author Richard Achmatowicz (c) 2011 Red Hat Inc.
 * @author Radoslav Husar
 */
public class JGroupsTransformersTestCase extends OperationTestCaseBase {
    @Test
    public void testTransformerEAP640() throws Exception {
        testTransformation(EAP_6_4_0);
    }

    @Test
    public void testTransformerEAP700() throws Exception {
        testTransformation(EAP_7_0_0);
    }

    @Test
    public void testTransformerEAP710() throws Exception {
        testTransformation(EAP_7_1_0);
    }

    @Test
    public void testTransformerEAP720() throws Exception {
        testTransformation(EAP_7_2_0);
    }

    @Test
    public void testRejectionsEAP640() throws Exception {
        testRejections(EAP_6_4_0);
    }

    @Test
    public void testRejectionsEAP700() throws Exception {
        testRejections(EAP_7_0_0);
    }

    @Test
    public void testRejectionsEAP710() throws Exception {
        testRejections(EAP_7_1_0);
    }

    @Test
    public void testRejectionsEAP720() throws Exception {
        testRejections(EAP_7_2_0);
    }
}

