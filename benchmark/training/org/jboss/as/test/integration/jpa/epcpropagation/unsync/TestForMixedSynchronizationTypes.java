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
package org.jboss.as.test.integration.jpa.epcpropagation.unsync;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * "
 * If there is a persistence context of type SynchronizationType.UNSYNCHRONIZED
 * associated with the JTA transaction and the target component specifies a persistence context of
 * type SynchronizationType.SYNCHRONIZED, the IllegalStateException is
 * thrown by the container.
 * "
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class TestForMixedSynchronizationTypes {
    private static final String ARCHIVE_NAME = "jpa_testForMixedSynchronizationTypes";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void testShouldGetEjbExceptionBecauseEPCIsAddedToTxAfterPc() throws Exception {
        final String errorCode = "WFLYJPA0064";
        BMTEPCStatefulBean stateful = lookup("BMTEPCStatefulBean", BMTEPCStatefulBean.class);
        try {
            stateful.willThrowError();
        } catch (Throwable expected) {
            Throwable cause = expected.getCause();
            while (cause != null) {
                if ((cause instanceof IllegalStateException) && (cause.getMessage().contains(errorCode))) {
                    break;// success

                }
                cause = cause.getCause();
                if (cause == null) {
                    Assert.fail(String.format("didn't throw IllegalStateException that contains '%s', instead got message chain: %s", errorCode, messages(expected)));
                }
            } 
        }
    }

    @Test
    public void testAllowjoinedunsyncEm() throws Exception {
        BMTEPCStatefulBean stateful = lookup("BMTEPCStatefulBean", BMTEPCStatefulBean.class);
        stateful.allowjoinedunsync();
    }

    @Test
    public void testAllowjoinedunsyncEmPersistenceXML() throws Exception {
        BMTEPCStatefulBean stateful = lookup("BMTEPCStatefulBean", BMTEPCStatefulBean.class);
        stateful.allowjoinedunsyncPersistenceXML();
    }
}

