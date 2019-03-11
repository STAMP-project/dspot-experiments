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


import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRTimerTest extends CRBaseTest<CRTimer> {
    private final CRTimer timer;

    private final CRTimer invalidNoTimerSpec;

    public CRTimerTest() {
        timer = new CRTimer("0 15 10 * * ? *");
        invalidNoTimerSpec = new CRTimer();
    }

    @Test
    public void shouldDeserializeFromAPILikeObject() {
        String json = "{\n" + (("    \"spec\": \"0 0 22 ? * MON-FRI\",\n" + "    \"only_on_changes\": true\n") + "  }");
        CRTimer deserializedValue = gson.fromJson(json, CRTimer.class);
        Assert.assertThat(deserializedValue.getTimerSpec(), Matchers.is("0 0 22 ? * MON-FRI"));
        Assert.assertThat(deserializedValue.isOnlyOnChanges(), Matchers.is(true));
        ErrorCollection errors = deserializedValue.getErrors();
        TestCase.assertTrue(errors.isEmpty());
    }
}

