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
package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Assert;
import org.junit.Test;

import static QAbstractEntityTest_CategoryReference.categoryReference;


public class AbstractEntityTest {
    @QueryEntity
    public abstract static class Category<T extends AbstractEntityTest.Category<T>> {
        public AbstractEntityTest.Category<T> defaultChild;
    }

    @QueryEntity
    public static class CategoryReference {
        @QueryInit("defaultChild")
        public AbstractEntityTest.Category<?> category;
    }

    @Test
    public void path_is_available() {
        QAbstractEntityTest_CategoryReference categoryReference = categoryReference;
        Assert.assertNotNull(categoryReference.category.defaultChild);
    }
}

