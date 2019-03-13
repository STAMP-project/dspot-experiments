/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.beanutil;


import JavaBeanAccessor.ALL;
import JavaBeanAccessor.FIELD;
import JavaBeanAccessor.METHOD;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JavaBeanAccessorTest {
    @Test
    public void testIsAccessByMethod() {
        Assertions.assertTrue(JavaBeanAccessor.isAccessByMethod(METHOD));
        Assertions.assertTrue(JavaBeanAccessor.isAccessByMethod(ALL));
        Assertions.assertFalse(JavaBeanAccessor.isAccessByMethod(FIELD));
    }

    @Test
    public void testIsAccessByField() {
        Assertions.assertTrue(JavaBeanAccessor.isAccessByField(FIELD));
        Assertions.assertTrue(JavaBeanAccessor.isAccessByField(ALL));
        Assertions.assertFalse(JavaBeanAccessor.isAccessByField(METHOD));
    }
}

