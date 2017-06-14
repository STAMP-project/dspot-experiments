/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.type;


public class AmplSimpleTypeRegistryTest {
    @org.junit.Test
    public void shouldTestIfClassIsSimpleTypeAndReturnTrue() {
        org.junit.Assert.assertTrue(org.apache.ibatis.type.SimpleTypeRegistry.isSimpleType(java.lang.String.class));
    }

    @org.junit.Test
    public void shouldTestIfClassIsSimpleTypeAndReturnFalse() {
        org.junit.Assert.assertFalse(org.apache.ibatis.type.SimpleTypeRegistry.isSimpleType(org.apache.ibatis.domain.misc.RichType.class));
    }

    @org.junit.Test
    public void shouldTestIfMapIsSimpleTypeAndReturnFalse() {
        org.junit.Assert.assertFalse(org.apache.ibatis.type.SimpleTypeRegistry.isSimpleType(java.util.HashMap.class));// see issue #165, a Map is not a simple type
        
    }
}

