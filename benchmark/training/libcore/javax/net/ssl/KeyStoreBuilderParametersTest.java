/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.javax.net.ssl;


import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.KeyStoreBuilderParameters;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;

import static java.security.KeyStore.Builder.newInstance;


public class KeyStoreBuilderParametersTest extends TestCase {
    public void test_init_Builder_null() {
        try {
            new KeyStoreBuilderParameters(((KeyStore.Builder) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_init_Builder() {
        TestKeyStore testKeyStore = TestKeyStore.getClient();
        KeyStore.Builder builder = newInstance(testKeyStore.keyStore, new KeyStore.PasswordProtection(testKeyStore.storePassword));
        KeyStoreBuilderParameters ksbp = new KeyStoreBuilderParameters(builder);
        TestCase.assertNotNull(ksbp);
        TestCase.assertNotNull(ksbp.getParameters());
        TestCase.assertEquals(1, ksbp.getParameters().size());
        TestCase.assertSame(builder, ksbp.getParameters().get(0));
    }

    public void test_init_List_null() {
        try {
            new KeyStoreBuilderParameters(((List) (null)));
        } catch (NullPointerException expected) {
        }
    }

    public void test_init_List() {
        TestKeyStore testKeyStore1 = TestKeyStore.getClient();
        TestKeyStore testKeyStore2 = TestKeyStore.getServer();
        KeyStore.Builder builder1 = newInstance(testKeyStore1.keyStore, new KeyStore.PasswordProtection(testKeyStore1.storePassword));
        KeyStore.Builder builder2 = newInstance(testKeyStore2.keyStore, new KeyStore.PasswordProtection(testKeyStore2.storePassword));
        List list = Arrays.asList(builder1, builder2);
        KeyStoreBuilderParameters ksbp = new KeyStoreBuilderParameters(list);
        TestCase.assertNotNull(ksbp);
        TestCase.assertNotNull(ksbp.getParameters());
        TestCase.assertNotSame(list, ksbp.getParameters());
        TestCase.assertEquals(2, ksbp.getParameters().size());
        TestCase.assertSame(builder1, ksbp.getParameters().get(0));
        TestCase.assertSame(builder2, ksbp.getParameters().get(1));
        // confirm result is not modifiable
        try {
            ksbp.getParameters().set(0, builder2);
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        // confirm result is a copy of original
        list.set(0, builder2);
        TestCase.assertSame(builder1, ksbp.getParameters().get(0));
    }
}

