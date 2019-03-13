/**
 * Copyright 2015 the original author or authors.
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
package org.powermock.core.transformers.javassist;


import javassist.CtPrimitiveType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.transformers.javassist.support.Primitives;


@RunWith(Parameterized.class)
public class TestPrimitives {
    final CtPrimitiveType ctType;

    public TestPrimitives(CtPrimitiveType ctType) {
        this.ctType = ctType;
    }

    @Test
    public void testMapping() {
        Class<?> mapping = Primitives.getClassFor(ctType);
        Assert.assertEquals(("Mapping for ctType=" + (ctType.getName())), ctType.getSimpleName(), mapping.getSimpleName());
    }
}

