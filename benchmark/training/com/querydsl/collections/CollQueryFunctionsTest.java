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
package com.querydsl.collections;


import org.junit.Assert;
import org.junit.Test;


public class CollQueryFunctionsTest {
    @Test
    public void coalesce() {
        Assert.assertEquals("1", CollQueryFunctions.coalesce("1", null));
        Assert.assertEquals("1", CollQueryFunctions.coalesce(null, "1", "2"));
        Assert.assertNull(CollQueryFunctions.coalesce(null, null));
    }

    @Test
    public void like() {
        Assert.assertTrue(CollQueryFunctions.like("abcDOG", "%DOG"));
        Assert.assertTrue(CollQueryFunctions.like("DOGabc", "DOG%"));
        Assert.assertTrue(CollQueryFunctions.like("abcDOGabc", "%DOG%"));
    }

    @Test
    public void like_with_special_chars() {
        Assert.assertTrue(CollQueryFunctions.like("$DOG", "$DOG"));
        Assert.assertTrue(CollQueryFunctions.like("$DOGabc", "$DOG%"));
    }
}

