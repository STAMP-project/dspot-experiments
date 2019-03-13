/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain.materials.scm;


import java.util.Calendar;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluggableSCMMaterialRevisionTest {
    @Test
    public void shouldFindSCMMaterialRevisionEqual() {
        Date now = new Date();
        PluggableSCMMaterialRevision revisionOne = new PluggableSCMMaterialRevision("go-agent-12.1.0", now);
        PluggableSCMMaterialRevision revisionTwo = new PluggableSCMMaterialRevision("go-agent-12.1.0", now);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        Date later = calendar.getTime();
        PluggableSCMMaterialRevision revisionThree = new PluggableSCMMaterialRevision("go-agent-12.1.0", later);
        Assert.assertThat(revisionOne.equals(revisionTwo), Matchers.is(true));
        Assert.assertThat(revisionOne.equals(revisionThree), Matchers.is(false));
    }
}

