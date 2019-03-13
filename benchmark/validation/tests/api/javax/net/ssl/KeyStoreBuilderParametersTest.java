/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.javax.net.ssl;


import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.KeyStoreBuilderParameters;
import junit.framework.TestCase;

import static java.security.KeyStore.Builder.newInstance;


public class KeyStoreBuilderParametersTest extends TestCase {
    /**
     * javax.net.ssl.KeyStoreBuilderParameters#KeyStoreBuilderParameters(KeyStore.Builder builder)
     */
    public void test_Constructor01() {
        // Null parameter
        try {
            new KeyStoreBuilderParameters(((KeyStore.Builder) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Not null parameter
        KeyStore.ProtectionParameter pp = new KeyStoreBuilderParametersTest.ProtectionParameterImpl();
        KeyStore.Builder bld = newInstance("testType", null, pp);
        TestCase.assertNotNull("Null object KeyStore.Builder", bld);
        KeyStoreBuilderParameters ksp = new KeyStoreBuilderParameters(bld);
        TestCase.assertNotNull(ksp.getParameters());
    }

    /**
     * javax.net.ssl.KeyStoreBuilderParameters#KeyStoreBuilderParameters(List parameters)
     */
    public void test_Constructor02() {
        // Null parameter
        try {
            KeyStoreBuilderParameters ksp = new KeyStoreBuilderParameters(((List) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Empty parameter
        List lsEmpty = new ArrayList<String>();
        try {
            KeyStoreBuilderParameters ksp = new KeyStoreBuilderParameters(lsEmpty);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // Not null parameter
        List lsFiled = new ArrayList<String>();
        lsFiled.add("Parameter1");
        lsFiled.add("Parameter2");
        new KeyStoreBuilderParameters(lsFiled);
    }

    /**
     * javax.net.ssl.KeyStoreBuilderParameters#getParameters()
     */
    public void test_getParameters() {
        String[] param = new String[]{ "Parameter1", "Parameter2", "Parameter3" };
        List ls = new ArrayList<String>();
        for (int i = 0; i < (param.length); i++) {
            ls.add(param[i]);
        }
        KeyStoreBuilderParameters ksp = new KeyStoreBuilderParameters(ls);
        List res_list = ksp.getParameters();
        try {
            res_list.add("test");
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        Object[] res = res_list.toArray();
        TestCase.assertEquals(res.length, param.length);
        for (int i = 0; i < (res.length); i++) {
            TestCase.assertEquals(param[i], res[i]);
        }
    }

    private static class ProtectionParameterImpl implements KeyStore.ProtectionParameter {
        private ProtectionParameterImpl() {
        }
    }
}

