/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.kerberos;


import KDCKerberosOperationHandler.InteractivePasswordHandler;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.easymock.IArgumentMatcher;
import org.junit.Test;


public abstract class KDCKerberosOperationHandlerTest extends KerberosOperationHandlerTest {
    static Method methodExecuteCommand;

    static Method methodGetExecutable;

    @Test
    public void testInteractivePasswordHandler() {
        KDCKerberosOperationHandler.InteractivePasswordHandler handler = new KDCKerberosOperationHandler.InteractivePasswordHandler("admin_password", "user_password");
        handler.start();
        Assert.assertEquals("admin_password", handler.getResponse("password"));
        Assert.assertFalse(handler.done());
        Assert.assertEquals("user_password", handler.getResponse("password"));
        Assert.assertFalse(handler.done());
        Assert.assertEquals("user_password", handler.getResponse("password"));
        Assert.assertTrue(handler.done());
        // Test restarting
        handler.start();
        Assert.assertEquals("admin_password", handler.getResponse("password"));
        Assert.assertFalse(handler.done());
        Assert.assertEquals("user_password", handler.getResponse("password"));
        Assert.assertFalse(handler.done());
        Assert.assertEquals("user_password", handler.getResponse("password"));
        Assert.assertTrue(handler.done());
    }

    public static class ArrayContains implements IArgumentMatcher {
        private String[] startItems;

        ArrayContains(String startItem) {
            this.startItems = new String[]{ startItem };
        }

        ArrayContains(String[] startItems) {
            this.startItems = startItems;
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof String[]) {
                String[] array = ((String[]) (o));
                for (String item : startItems) {
                    boolean valueContains = false;
                    for (String value : array) {
                        if (value.contains(item)) {
                            valueContains = true;
                            break;
                        }
                    }
                    if (!valueContains) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append("arrayContains(");
            stringBuffer.append(StringUtils.join(startItems, ", "));
            stringBuffer.append("\")");
        }
    }
}

