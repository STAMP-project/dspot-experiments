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
package org.jboss.as.security.service;


import org.jboss.security.RunAs;
import org.jboss.security.SecurityContext;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Mock test for {@link SimpleSecurityManager}.
 *
 * Tests fix of JBEAP-11462 - run-as identity gets lost in some cases.
 *
 * @author Jiri Ondrusek (jondruse@redhat.com)
 */
public class SimpleSecurityServiceManagerMockTest {
    private SimpleSecurityManager simpleSecurityManager;

    private SecurityContext orig;

    @Test
    public void testHandlingRunAs() {
        RunAs runAs1 = Mockito.mock(RunAs.class);
        Mockito.when(runAs1.toString()).thenReturn("RunAs-1");
        RunAs runAs2 = Mockito.mock(RunAs.class);
        Mockito.when(runAs2.toString()).thenReturn("RunAs-2");
        testHandlingRunAs(runAs1, runAs2);
        testHandlingRunAs(null, runAs2);
        testHandlingRunAs(runAs1, null);
        testHandlingRunAs(null, null);
    }
}

