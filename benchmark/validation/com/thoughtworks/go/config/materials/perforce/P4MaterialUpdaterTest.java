/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.materials.perforce;


import JobResult.Passed;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class P4MaterialUpdaterTest extends P4MaterialUpdaterTestBase {
    @Test
    public void shouldNotDisplayPassword() throws Exception {
        P4Material material = p4Fixture.material(P4MaterialUpdaterTestBase.VIEW);
        material.setPassword("wubba lubba dub dub");
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("wubba lubba dub dub")));
    }
}

