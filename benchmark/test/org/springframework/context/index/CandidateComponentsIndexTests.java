/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.context.index;


import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CandidateComponentsIndex}.
 *
 * @author Stephane Nicoll
 */
public class CandidateComponentsIndexTests {
    @Test
    public void getCandidateTypes() {
        CandidateComponentsIndex index = new CandidateComponentsIndex(Collections.singletonList(CandidateComponentsIndexTests.createSampleProperties()));
        Set<String> actual = index.getCandidateTypes("com.example.service", "service");
        Assert.assertThat(actual, containsInAnyOrder("com.example.service.One", "com.example.service.sub.Two", "com.example.service.Three"));
    }

    @Test
    public void getCandidateTypesSubPackage() {
        CandidateComponentsIndex index = new CandidateComponentsIndex(Collections.singletonList(CandidateComponentsIndexTests.createSampleProperties()));
        Set<String> actual = index.getCandidateTypes("com.example.service.sub", "service");
        Assert.assertThat(actual, containsInAnyOrder("com.example.service.sub.Two"));
    }

    @Test
    public void getCandidateTypesSubPackageNoMatch() {
        CandidateComponentsIndex index = new CandidateComponentsIndex(Collections.singletonList(CandidateComponentsIndexTests.createSampleProperties()));
        Set<String> actual = index.getCandidateTypes("com.example.service.none", "service");
        Assert.assertThat(actual, hasSize(0));
    }

    @Test
    public void getCandidateTypesNoMatch() {
        CandidateComponentsIndex index = new CandidateComponentsIndex(Collections.singletonList(CandidateComponentsIndexTests.createSampleProperties()));
        Set<String> actual = index.getCandidateTypes("com.example.service", "entity");
        Assert.assertThat(actual, hasSize(0));
    }

    @Test
    public void mergeCandidateStereotypes() {
        CandidateComponentsIndex index = new CandidateComponentsIndex(Arrays.asList(CandidateComponentsIndexTests.createProperties("com.example.Foo", "service"), CandidateComponentsIndexTests.createProperties("com.example.Foo", "entity")));
        Assert.assertThat(index.getCandidateTypes("com.example", "service"), contains("com.example.Foo"));
        Assert.assertThat(index.getCandidateTypes("com.example", "entity"), contains("com.example.Foo"));
    }
}

