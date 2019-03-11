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


import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;

import static PathInits.DEFAULT;


public class BeanPathTest {
    public static class SubClass extends BeanPathTest {}

    public static class MyBeanPath extends BeanPath<BeanPathTest> {
        private static final long serialVersionUID = 6225684967115368814L;

        public MyBeanPath(PathMetadata metadata) {
            super(BeanPathTest.class, metadata);
        }

        public MyBeanPath(PathMetadata metadata, @Nullable
        PathInits inits) {
            super(BeanPathTest.class, metadata);
        }
    }

    private BeanPath<BeanPathTest> beanPath = new BeanPath<BeanPathTest>(BeanPathTest.class, "p");

    @Test
    public void as_path() {
        SimplePath<BeanPathTest> simplePath = new SimplePath<BeanPathTest>(BeanPathTest.class, "p");
        Assert.assertNotNull(beanPath.as(simplePath));
    }

    @Test
    public void as_class_cached() {
        BeanPathTest.MyBeanPath otherPath = beanPath.as(BeanPathTest.MyBeanPath.class);
        // assertEquals(beanPath, otherPath);
        Assert.assertTrue((otherPath == (beanPath.as(BeanPathTest.MyBeanPath.class))));
    }

    @Test
    public void as_class_with_inits_cached() {
        beanPath = new BeanPath<BeanPathTest>(BeanPathTest.class, PathMetadataFactory.forVariable("p"), DEFAULT);
        BeanPathTest.MyBeanPath otherPath = beanPath.as(BeanPathTest.MyBeanPath.class);
        // assertEquals(beanPath, otherPath);
        Assert.assertTrue((otherPath == (beanPath.as(BeanPathTest.MyBeanPath.class))));
    }

    @Test
    public void createEnum() {
        Assert.assertNotNull(beanPath.createEnum("property", PropertyType.class));
    }

    @Test
    public void instanceOf() {
        Assert.assertNotNull(beanPath.instanceOf(BeanPathTest.class));
    }

    @Test
    public void instanceOfAny() {
        BooleanExpression pred1 = beanPath.instanceOf(BeanPathTest.class).or(beanPath.instanceOf(BeanPathTest.SubClass.class));
        BooleanExpression pred2 = beanPath.instanceOfAny(BeanPathTest.class, BeanPathTest.SubClass.class);
        Assert.assertEquals(pred1, pred2);
        Assert.assertEquals(("p instanceof class com.querydsl.core.types.dsl.BeanPathTest || " + "p instanceof class com.querydsl.core.types.dsl.BeanPathTest$SubClass"), pred2.toString());
    }
}

