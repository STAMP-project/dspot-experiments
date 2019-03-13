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
package com.github.dozermapper.core.loader.xml;


import com.github.dozermapper.core.AbstractDozerTest;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;


public class DozerResolverTest extends AbstractDozerTest {
    private DozerResolver dozerResolver;

    @Test
    public void testResolveEntity_OK() {
        InputSource inputSource = dozerResolver.resolveEntity(null, "http://dozermapper.github.io/schema/bean-mapping.xsd");
        Assert.assertNotNull(inputSource);
    }

    @Test
    public void testResolveEntity_Direct() {
        InputSource inputSource = dozerResolver.resolveEntity(null, "bean-mapping.xsd");
        Assert.assertNotNull(inputSource);
    }
}

