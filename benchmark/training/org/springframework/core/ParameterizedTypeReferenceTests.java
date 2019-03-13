/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test fixture for {@link ParameterizedTypeReference}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class ParameterizedTypeReferenceTests {
    @Test
    public void stringTypeReference() {
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>() {};
        Assert.assertEquals(String.class, typeReference.getType());
    }

    @Test
    public void mapTypeReference() throws Exception {
        Type mapType = getClass().getMethod("mapMethod").getGenericReturnType();
        ParameterizedTypeReference<Map<Object, String>> typeReference = new ParameterizedTypeReference<Map<Object, String>>() {};
        Assert.assertEquals(mapType, typeReference.getType());
    }

    @Test
    public void listTypeReference() throws Exception {
        Type listType = getClass().getMethod("listMethod").getGenericReturnType();
        ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<List<String>>() {};
        Assert.assertEquals(listType, typeReference.getType());
    }

    @Test
    public void reflectiveTypeReferenceWithSpecificDeclaration() throws Exception {
        Type listType = getClass().getMethod("listMethod").getGenericReturnType();
        ParameterizedTypeReference<List<String>> typeReference = ParameterizedTypeReference.forType(listType);
        Assert.assertEquals(listType, typeReference.getType());
    }

    @Test
    public void reflectiveTypeReferenceWithGenericDeclaration() throws Exception {
        Type listType = getClass().getMethod("listMethod").getGenericReturnType();
        ParameterizedTypeReference<?> typeReference = ParameterizedTypeReference.forType(listType);
        Assert.assertEquals(listType, typeReference.getType());
    }
}

