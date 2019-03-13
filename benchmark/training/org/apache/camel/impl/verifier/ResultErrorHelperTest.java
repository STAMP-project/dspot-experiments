/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl.verifier;


import StandardCode.ILLEGAL_PARAMETER_GROUP_COMBINATION;
import VerificationError.GroupAttribute.GROUP_NAME;
import VerificationError.GroupAttribute.GROUP_OPTIONS;
import java.util.List;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.apache.camel.component.extension.ComponentVerifierExtension.VerificationError;
import org.apache.camel.component.extension.verifier.OptionsGroup;
import org.apache.camel.component.extension.verifier.ResultErrorHelper;
import org.junit.Assert;
import org.junit.Test;


public class ResultErrorHelperTest {
    OptionsGroup[] groups = new OptionsGroup[]{ OptionsGroup.withName("optionA").options("param1", "param2", "!param3"), OptionsGroup.withName("optionB").options("param1", "!param2", "param3"), OptionsGroup.withName("optionC").options("!param1", "!param2", "param4") };

    @Test
    public void shouldValidateCorrectParameters() {
        // just giving param1 and param2 is OK
        Assert.assertTrue(ResultErrorHelper.requiresAny(ResultErrorHelperTest.map("param1", "param2"), groups).isEmpty());
        // just giving param1 and param3 is OK
        Assert.assertTrue(ResultErrorHelper.requiresAny(ResultErrorHelperTest.map("param1", "param3"), groups).isEmpty());
        // just giving param4 is OK
        Assert.assertTrue(ResultErrorHelper.requiresAny(ResultErrorHelperTest.map("param4"), groups).isEmpty());
    }

    @Test
    public void shouldValidateParameterExclusions() {
        // combining param2 and param3 is not OK
        final List<ComponentVerifierExtension.VerificationError> results = ResultErrorHelper.requiresAny(ResultErrorHelperTest.map("param1", "param2", "param3"), groups);
        Assert.assertEquals(3, results.size());
        final VerificationError param3Error = results.get(0);
        Assert.assertEquals(ILLEGAL_PARAMETER_GROUP_COMBINATION, param3Error.getCode());
        Assert.assertEquals("optionA", param3Error.getDetail(GROUP_NAME));
        Assert.assertEquals("param1,param2,param3", param3Error.getDetail(GROUP_OPTIONS));
        final VerificationError param2Error = results.get(1);
        Assert.assertEquals(ILLEGAL_PARAMETER_GROUP_COMBINATION, param2Error.getCode());
        Assert.assertEquals("optionB", param2Error.getDetail(GROUP_NAME));
        Assert.assertEquals("param1,param2,param3", param2Error.getDetail(GROUP_OPTIONS));
        final VerificationError param4Error = results.get(2);
        Assert.assertEquals(ILLEGAL_PARAMETER_GROUP_COMBINATION, param4Error.getCode());
        Assert.assertEquals("optionC", param4Error.getDetail(GROUP_NAME));
        Assert.assertEquals("param1,param2,param4", param4Error.getDetail(GROUP_OPTIONS));
    }
}

