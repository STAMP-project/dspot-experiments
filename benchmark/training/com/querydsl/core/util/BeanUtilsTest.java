/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.util;


import org.junit.Assert;
import org.junit.Test;


public class BeanUtilsTest {
    @Test
    public void capitalize() {
        Assert.assertEquals("X", BeanUtils.capitalize("x"));
        Assert.assertEquals("Prop", BeanUtils.capitalize("prop"));
        Assert.assertEquals("URL", BeanUtils.capitalize("URL"));
        Assert.assertEquals("cId", BeanUtils.capitalize("cId"));
        Assert.assertEquals("sEPOrder", BeanUtils.capitalize("sEPOrder"));
    }

    @Test
    public void uncapitalize() {
        Assert.assertEquals("x", BeanUtils.uncapitalize("X"));
        Assert.assertEquals("prop", BeanUtils.uncapitalize("Prop"));
        Assert.assertEquals("URL", BeanUtils.uncapitalize("URL"));
        Assert.assertEquals("cId", BeanUtils.uncapitalize("cId"));
        Assert.assertEquals("sEPOrder", BeanUtils.capitalize("sEPOrder"));
    }
}

