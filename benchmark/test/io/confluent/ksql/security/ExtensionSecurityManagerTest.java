/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.security;


import ExtensionSecurityManager.INSTANCE;
import io.confluent.ksql.function.udf.PluggableUdf;
import org.junit.Test;


public class ExtensionSecurityManagerTest {
    private final SecurityManager securityManager = System.getSecurityManager();

    @Test
    public void shouldAllowExec() {
        INSTANCE.checkExec("cmd");
    }

    @Test
    public void shouldAllowExit() {
        INSTANCE.checkExit(0);
    }

    @Test
    public void shouldAllowAccept() {
        INSTANCE.checkAccept("host", 90);
    }

    @Test
    public void shouldAllowConnect() {
        INSTANCE.checkConnect("host", 90);
    }

    @Test
    public void shouldAllowListen() {
        INSTANCE.checkListen(90);
    }

    @Test(expected = SecurityException.class)
    public void shouldNotAllowExecWhenPluggableUDF() {
        new PluggableUdf(( thiz, args) -> {
            try {
                return Runtime.getRuntime().exec("cmd");
            } catch ( e) {
                return null;
            }
        }, new Object()).evaluate();
    }

    @Test(expected = SecurityException.class)
    public void shouldNotAllowExitWhenPluggableUDF() {
        new PluggableUdf(( thiz, args) -> {
            System.exit(1);
            return null;
        }, new Object()).evaluate();
    }
}

