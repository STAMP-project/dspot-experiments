/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.admin;


import AlertLevel.ERROR;
import AlertLevel.ERROR.ordinal;
import java.lang.reflect.Constructor;
import org.apache.geode.internal.Assert;
import org.junit.Test;

import static AlertLevel.ERROR;
import static AlertLevel.WARNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * AlertLevel Tester.
 */
public class AlertLevelJUnitTest {
    /**
     * Method: equals(Object other)
     */
    private AlertLevel alertLevel1 = WARNING;

    private AlertLevel alertLevel2 = ERROR;

    private AlertLevel alertLevel3 = WARNING;

    @Test
    public void testEquals() throws Exception {
        // TODO: Test goes here...
        Assert.assertTrue(alertLevel1.equals(alertLevel3));
        assertFalse(alertLevel1.equals(alertLevel2));
        assertFalse(alertLevel1.equals(null));
        Constructor<AlertLevel> constructor;
        constructor = AlertLevel.class.getDeclaredConstructor(int.class, String.class, int.class);
        constructor.setAccessible(true);
        AlertLevel level = constructor.newInstance(ERROR.getSeverity(), "ERROR", ordinal);
        org.junit.Assert.assertEquals(level.getSeverity(), ERROR.getSeverity());
        AlertLevel level1 = constructor.newInstance(ERROR.getSeverity(), new String("ERROR"), ordinal);
        org.junit.Assert.assertEquals(level1.getName(), alertLevel2.getName());
        Assert.assertTrue(level1.equals(alertLevel2));
    }

    @Test
    public void checkOrdinals() {
        for (int i = 0; i < (AlertLevel.values().length); i++) {
            assertEquals(i, AlertLevel.values()[i].ordinal);
        }
    }
}

