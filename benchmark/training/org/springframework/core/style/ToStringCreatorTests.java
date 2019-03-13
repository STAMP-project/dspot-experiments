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
package org.springframework.core.style;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Keith Donald
 */
public class ToStringCreatorTests {
    private ToStringCreatorTests.SomeObject s1;

    private ToStringCreatorTests.SomeObject s2;

    private ToStringCreatorTests.SomeObject s3;

    @Test
    public void defaultStyleMap() {
        final Map<String, String> map = getMap();
        Object stringy = new Object() {
            @Override
            public String toString() {
                return new ToStringCreator(this).append("familyFavoriteSport", map).toString();
            }
        };
        Assert.assertEquals((("[ToStringCreatorTests.4@" + (ObjectUtils.getIdentityHexString(stringy))) + " familyFavoriteSport = map['Keri' -> 'Softball', 'Scot' -> 'Fishing', 'Keith' -> 'Flag Football']]"), stringy.toString());
    }

    @Test
    public void defaultStyleArray() {
        ToStringCreatorTests.SomeObject[] array = new ToStringCreatorTests.SomeObject[]{ s1, s2, s3 };
        String str = new ToStringCreator(array).toString();
        Assert.assertEquals((("[@" + (ObjectUtils.getIdentityHexString(array))) + " array<ToStringCreatorTests.SomeObject>[A, B, C]]"), str);
    }

    @Test
    public void primitiveArrays() {
        int[] integers = new int[]{ 0, 1, 2, 3, 4 };
        String str = new ToStringCreator(integers).toString();
        Assert.assertEquals((("[@" + (ObjectUtils.getIdentityHexString(integers))) + " array<Integer>[0, 1, 2, 3, 4]]"), str);
    }

    @Test
    public void appendList() {
        List<ToStringCreatorTests.SomeObject> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        list.add(s3);
        String str = new ToStringCreator(this).append("myLetters", list).toString();
        Assert.assertEquals((("[ToStringCreatorTests@" + (ObjectUtils.getIdentityHexString(this))) + " myLetters = list[A, B, C]]"), str);
    }

    @Test
    public void appendSet() {
        Set<ToStringCreatorTests.SomeObject> set = new LinkedHashSet<>();
        set.add(s1);
        set.add(s2);
        set.add(s3);
        String str = new ToStringCreator(this).append("myLetters", set).toString();
        Assert.assertEquals((("[ToStringCreatorTests@" + (ObjectUtils.getIdentityHexString(this))) + " myLetters = set[A, B, C]]"), str);
    }

    @Test
    public void appendClass() {
        String str = new ToStringCreator(this).append("myClass", this.getClass()).toString();
        Assert.assertEquals((("[ToStringCreatorTests@" + (ObjectUtils.getIdentityHexString(this))) + " myClass = ToStringCreatorTests]"), str);
    }

    @Test
    public void appendMethod() throws Exception {
        String str = new ToStringCreator(this).append("myMethod", this.getClass().getMethod("appendMethod")).toString();
        Assert.assertEquals((("[ToStringCreatorTests@" + (ObjectUtils.getIdentityHexString(this))) + " myMethod = appendMethod@ToStringCreatorTests]"), str);
    }

    public static class SomeObject {}
}

