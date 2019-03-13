/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz;


import junit.framework.TestCase;


/**
 * Base class for unit tests that wish to verify backwards compatibility of serialization with earlier versions
 * of Quartz.
 *
 * <p>The way to properly setup tests for subclass is it needs to generate a <ClassName>.ser
 * resource file under the same package. This ".ser" file only needs to be generated one time,
 * using the version of Quartz matching to the VERIONS values. Then during test, each of this
 * file will be deserialized to verify the data.</p>
 */
public abstract class SerializationTestSupport extends TestCase {
    /**
     * Test that we can successfully deserialize our target
     * class for all of the given Quartz versions.
     */
    public void testSerialization() throws Exception {
        Object targetObject = getTargetObject();
        for (int i = 0; i < (getVersions().length); i++) {
            String version = getVersions()[i];
            verifyMatch(targetObject, deserialize(version, targetObject.getClass()));
        }
    }
}

