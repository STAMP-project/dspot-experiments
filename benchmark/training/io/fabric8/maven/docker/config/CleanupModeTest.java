/**
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.config;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 01/03/16
 */
public class CleanupModeTest {
    @Test
    public void parse() {
        Object[] data = new Object[]{ null, TRY_TO_REMOVE, "try", TRY_TO_REMOVE, "FaLsE", NONE, "NONE", NONE, "true", REMOVE, "removE", REMOVE };
        for (int i = 0; i < (data.length); i += 2) {
            Assert.assertEquals(data[(i + 1)], CleanupMode.CleanupMode.parse(((String) (data[i]))));
        }
    }

    @Test
    public void invalid() {
        try {
            CleanupMode.CleanupMode.parse("blub");
            Assert.fail();
        } catch (IllegalArgumentException exp) {
            Assert.assertTrue(exp.getMessage().contains("blub"));
            Assert.assertTrue(exp.getMessage().contains("try"));
            Assert.assertTrue(exp.getMessage().contains("none"));
            Assert.assertTrue(exp.getMessage().contains("remove"));
        }
    }
}

