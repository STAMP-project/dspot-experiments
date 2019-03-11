/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.test;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Drill2130StorageHiveCoreHamcrestConfigurationTest {
    private static final Logger logger = LoggerFactory.getLogger(Drill2130StorageHiveCoreHamcrestConfigurationTest.class);

    @SuppressWarnings("unused")
    private MatcherAssert forCompileTimeCheckForNewEnoughHamcrest;

    @Test
    public void testJUnitHamcrestMatcherFailureWorks() {
        try {
            Assert.assertThat(1, CoreMatchers.equalTo(2));
        } catch (NoSuchMethodError e) {
            Assert.fail((("Class search path seems broken re new JUnit and old Hamcrest." + "  Got NoSuchMethodError;  e: ") + e));
        } catch (AssertionError e) {
            Drill2130StorageHiveCoreHamcrestConfigurationTest.logger.info("Class path seems fine re new JUnit vs. old Hamcrest. (Got AssertionError, not NoSuchMethodError.)");
        }
    }
}

