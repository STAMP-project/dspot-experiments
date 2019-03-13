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
package com.github.dozermapper.core.functional_tests.mapperaware;


import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperAware;
import com.github.dozermapper.core.vo.mapperaware.MapperAwareSimpleDest;
import com.github.dozermapper.core.vo.mapperaware.MapperAwareSimpleInternal;
import com.github.dozermapper.core.vo.mapperaware.MapperAwareSimpleSrc;
import org.junit.Assert;
import org.junit.Test;


/**
 * Fixes issue 45
 *
 * @see <a href="https://github.com/DozerMapper/dozer/issues/45">issue</a>
 */
public class SecondUsingMapInternalTest {
    Mapper mapper;

    @Test
    public void twiceInnerMapperAwareConverterMapping() {
        MapperAwareSimpleSrc src = new MapperAwareSimpleSrc();
        src.setOne(new MapperAwareSimpleInternal());
        MapperAwareSimpleDest dst = new MapperAwareSimpleDest();
        mapper.map(src, dst);
        Assert.assertNotNull(dst.getOne());
    }

    private static class TwiceInnerMapperAwareConverter implements CustomConverter , MapperAware {
        private Mapper mapper;

        @Override
        public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
            MapperAwareSimpleInternal a = ((MapperAwareSimpleInternal) (sourceFieldValue));
            MapperAwareSimpleInternal b = new MapperAwareSimpleInternal();
            mapper.map(a, b);
            MapperAwareSimpleInternal b2 = new MapperAwareSimpleInternal();
            mapper.map(a, b2);// throws NPE in issue45

            return b2;
        }

        @Override
        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }
    }
}

