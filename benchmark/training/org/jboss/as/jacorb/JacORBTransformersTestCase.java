/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
import ModelTestControllerVersion.EAP_6_4_0;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class JacORBTransformersTestCase extends AbstractSubsystemTest {
    public JacORBTransformersTestCase() {
        super(SUBSYSTEM_NAME, new JacORBExtension());
    }

    // eap62=1.3
    // eap63=1.4
    // eap64=1.4
    @Test
    public void testTransformersEAP640() throws Exception {
        testTransformers(EAP_6_4_0, ModelVersion.create(1, 4));
    }

    @Test
    public void testTransformersSecurityIdentityEAP640() throws Exception {
        testTransformersSecurityIdentity(EAP_6_4_0, ModelVersion.create(1, 4, 0));
    }

    @Test
    public void testTransformersSecurityClientEAP640() throws Exception {
        testTransformersSecurityClient(EAP_6_4_0, ModelVersion.create(1, 4, 0));
    }
}

