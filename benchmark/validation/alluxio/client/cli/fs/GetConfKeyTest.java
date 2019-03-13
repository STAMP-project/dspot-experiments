/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.cli.fs;


import alluxio.SystemOutRule;
import alluxio.conf.PropertyKey;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests for {@link GetConfKey}.
 */
public final class GetConfKeyTest {
    private ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();

    @Rule
    public SystemOutRule mOutputStreamRule = new SystemOutRule(mOutputStream);

    @Test
    public void getConfKeyWithAllPropertyNames() throws Exception {
        for (Field field : PropertyKey.class.getDeclaredFields()) {
            if (field.getType().equals(PropertyKey.class)) {
                String key = ((PropertyKey) (field.get(PropertyKey.class))).toString();
                assertConfKey(key.toUpperCase().replace(".", "_"), key, 0);
            }
        }
    }

    @Test
    public void getConfKeyWithInvalidName() throws Exception {
        assertConfKey("ALLUXIO_INVALID_COMMAND", "", 1);
    }
}

