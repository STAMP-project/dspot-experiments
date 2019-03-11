/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.settings;


import com.cloudera.oryx.common.OryxTest;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public final class ConfigToPropertiesTest extends OryxTest {
    @Test
    public void testToProperties() {
        List<String> propertiesLines = ConfigToProperties.buildPropertiesLines();
        OryxTest.assertContains(propertiesLines, "oryx.serving.api.secure-port=443");
        OryxTest.assertNotContains(propertiesLines, "oryx.id=null");
        propertiesLines.forEach(( line) -> Assert.assertTrue(line.startsWith("oryx.")));
    }
}

