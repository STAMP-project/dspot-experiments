/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.inheritance.cc.A;
import com.github.dozermapper.core.vo.inheritance.cc.C;
import com.github.dozermapper.core.vo.inheritance.cc.X;
import com.github.dozermapper.core.vo.inheritance.cc.Z;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InheritanceCustomConverterTest extends AbstractFunctionalTest {
    /* Bug #1953410 */
    @Test
    public void shouldUseConverter() {
        Z source = new Z();
        source.setTest("testString");
        C result = mapper.map(source, C.class);
        Assert.assertThat(result.getTest(), CoreMatchers.equalTo("customConverter"));
    }

    @Test
    public void shouldUseConverter2() {
        X source = new X();
        source.setTest("testString");
        A result = mapper.map(source, A.class);
        Assert.assertThat(result.getTest(), CoreMatchers.equalTo("customConverter"));
    }

    @Test
    public void shouldUseConverter3() {
        X source = new X();
        source.setTest("testString");
        C result = mapper.map(source, C.class);
        Assert.assertThat(result.getTest(), CoreMatchers.equalTo("customConverter"));
    }

    @Test
    public void shouldUseConverter4() {
        Z source = new Z();
        source.setTest("testString");
        A result = mapper.map(source, A.class);
        Assert.assertThat(result.getTest(), CoreMatchers.equalTo("customConverter"));
    }
}

