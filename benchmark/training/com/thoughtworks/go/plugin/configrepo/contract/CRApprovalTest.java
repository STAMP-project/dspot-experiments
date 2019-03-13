/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.configrepo.contract;


import CRApprovalCondition.manual;
import java.util.Arrays;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static CRApprovalCondition.manual;
import static CRApprovalCondition.success;


public class CRApprovalTest extends CRBaseTest<CRApproval> {
    private final CRApproval manual;

    private final CRApproval success;

    private final CRApproval manualWithAuth;

    private final CRApproval badType;

    public CRApprovalTest() {
        manual = new CRApproval(manual);
        success = new CRApproval(success);
        manualWithAuth = new CRApproval(manual);
        manualWithAuth.setAuthorizedRoles(Arrays.asList("manager"));
        badType = new CRApproval();
    }

    @Test
    public void shouldDeserializeFromAPILikeObject() {
        String json = "{\n" + ((((((("    \"type\": \"manual\",\n" + "      \"roles\": [\n") + "\n") + "      ],\n") + "      \"users\": [\n") + "\n\"joe\"") + "      ]\n") + "  }");
        CRApproval deserializedValue = gson.fromJson(json, CRApproval.class);
        Assert.assertThat(deserializedValue.getType(), Matchers.is(CRApprovalCondition.manual));
        Assert.assertThat(deserializedValue.getAuthorizedUsers().isEmpty(), Matchers.is(false));
        Assert.assertThat(deserializedValue.getAuthorizedRoles().isEmpty(), Matchers.is(true));
        ErrorCollection errors = deserializedValue.getErrors();
        TestCase.assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldAppendPrettyLocationInErrors() {
        CRApproval a = new CRApproval();
        ErrorCollection errors = new ErrorCollection();
        a.getErrors(errors, "Pipeline abc");
        String fullError = errors.getErrorsAsText();
        Assert.assertThat(fullError, contains("Pipeline abc; Approval"));
        Assert.assertThat(fullError, contains("Missing field 'type'."));
    }
}

