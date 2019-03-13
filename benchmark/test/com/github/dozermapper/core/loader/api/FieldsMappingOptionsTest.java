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
package com.github.dozermapper.core.loader.api;


import DozerBuilder.FieldMappingBuilder;
import com.github.dozermapper.core.loader.DozerBuilder;
import org.junit.Test;
import org.mockito.Mockito;


public class FieldsMappingOptionsTest {
    @Test
    public void testHintAandB() {
        DozerBuilder.FieldMappingBuilder mappingBuilder = Mockito.mock(FieldMappingBuilder.class);
        FieldsMappingOption option = FieldsMappingOptions.hintA(String.class, Integer.class);
        option.apply(mappingBuilder);
        Mockito.verify(mappingBuilder).srcHintContainer("java.lang.String,java.lang.Integer");
    }
}

