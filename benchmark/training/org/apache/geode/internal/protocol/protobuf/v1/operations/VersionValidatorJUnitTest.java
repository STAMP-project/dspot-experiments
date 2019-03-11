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
package org.apache.geode.internal.protocol.protobuf.v1.operations;


import ProtocolVersion.MajorVersions.INVALID_MAJOR_VERSION_VALUE;
import ProtocolVersion.MinorVersions.INVALID_MINOR_VERSION_VALUE;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class VersionValidatorJUnitTest {
    private static final int MAJOR_VERSION = 3;

    private static final int MINOR_VERSION = 3;

    private static final VersionValidator validator = new VersionValidator(VersionValidatorJUnitTest.MAJOR_VERSION, VersionValidatorJUnitTest.MINOR_VERSION);

    @Test
    public void testInvalidVersions() throws Exception {
        Assert.assertFalse(VersionValidatorJUnitTest.validator.isValid(VersionValidatorJUnitTest.MAJOR_VERSION, INVALID_MINOR_VERSION_VALUE));
        Assert.assertFalse(VersionValidatorJUnitTest.validator.isValid(INVALID_MAJOR_VERSION_VALUE, VersionValidatorJUnitTest.MINOR_VERSION));
        Assert.assertFalse(VersionValidatorJUnitTest.validator.isValid(INVALID_MAJOR_VERSION_VALUE, INVALID_MINOR_VERSION_VALUE));
    }

    @Test
    public void testCurrentVersions() throws Exception {
        Assert.assertTrue(VersionValidatorJUnitTest.validator.isValid(VersionValidatorJUnitTest.MAJOR_VERSION, VersionValidatorJUnitTest.MINOR_VERSION));
    }

    @Test
    public void testPreviousMajorVersions() throws Exception {
        Assert.assertFalse(VersionValidatorJUnitTest.validator.isValid(((VersionValidatorJUnitTest.MAJOR_VERSION) - 1), VersionValidatorJUnitTest.MINOR_VERSION));
        Assert.assertFalse(VersionValidatorJUnitTest.validator.isValid(((VersionValidatorJUnitTest.MAJOR_VERSION) - 2), VersionValidatorJUnitTest.MINOR_VERSION));
    }

    @Test
    public void testPreviousMinorVersions() throws Exception {
        Assert.assertTrue(VersionValidatorJUnitTest.validator.isValid(VersionValidatorJUnitTest.MAJOR_VERSION, ((VersionValidatorJUnitTest.MINOR_VERSION) - 1)));
        Assert.assertTrue(VersionValidatorJUnitTest.validator.isValid(VersionValidatorJUnitTest.MAJOR_VERSION, ((VersionValidatorJUnitTest.MINOR_VERSION) - 2)));
    }
}

