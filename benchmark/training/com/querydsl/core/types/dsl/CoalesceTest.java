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
package com.querydsl.core.types.dsl;


import org.junit.Assert;
import org.junit.Test;


public class CoalesceTest {
    private final StringPath firstname = new StringPath("firstname");

    private final StringPath lastname = new StringPath("lastname");

    @Test
    public void mutable() {
        Coalesce<String> c = new Coalesce<String>(firstname, lastname).add("xxx");
        Assert.assertEquals("coalesce(firstname, lastname, xxx)", c.toString());
        Assert.assertEquals("coalesce(firstname, lastname, xxx, yyy)", c.add("yyy").toString());
    }

    @Test
    public void withList() {
        Coalesce<String> c = new Coalesce<String>(firstname, lastname).add("xxx");
        Assert.assertEquals("coalesce(firstname, lastname, xxx)", c.toString());
    }

    @Test
    public void withSingleArg() {
        Coalesce<String> c = new Coalesce<String>().add("xxx");
        Assert.assertEquals("coalesce(xxx)", c.toString());
    }

    @Test
    public void asComparable() {
        Coalesce<String> c = new Coalesce<String>(firstname, lastname);
        c.getValue().asc();
    }

    @Test
    public void asString() {
        Coalesce<String> c = new Coalesce<String>(firstname, lastname);
        c.asString().lower();
    }

    @Test
    public void withoutWarnings() {
        Coalesce<String> c = new Coalesce<String>(String.class).add(firstname).add(lastname);
        Assert.assertEquals("coalesce(firstname, lastname)", c.toString());
    }

    @Test
    public void dsl() {
        Assert.assertEquals("coalesce(firstname, lastname)", firstname.coalesce(lastname).toString());
    }

    @Test
    public void dsl2() {
        Assert.assertEquals("coalesce(firstname, lastname, xxx)", firstname.coalesce(lastname).add("xxx").toString());
    }

    @Test
    public void dsl3() {
        Assert.assertEquals("coalesce(firstname, xxx)", firstname.coalesce("xxx").toString());
    }

    @Test
    public void asc() {
        Assert.assertEquals("coalesce(firstname, xxx) ASC", firstname.coalesce("xxx").asc().toString());
    }

    @Test
    public void desc() {
        Assert.assertEquals("coalesce(firstname, xxx) DESC", firstname.coalesce("xxx").desc().toString());
    }
}

