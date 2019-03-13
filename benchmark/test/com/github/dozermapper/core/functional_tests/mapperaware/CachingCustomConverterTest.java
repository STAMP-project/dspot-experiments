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


import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperAware;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CachingCustomConverterTest {
    Mapper mapper;

    @Test
    public void shouldNotExitWithStackOverFlow() {
        BidirectionalOne one = new BidirectionalOne();
        BidirectionalMany source = new BidirectionalMany();
        source.setOne(one);
        Set<BidirectionalMany> many = new HashSet<>();
        many.add(source);
        one.setMany(many);
        BidirectionalManyConvert destination = mapper.map(source, BidirectionalManyConvert.class);
        Assert.assertNotNull(destination);
        Assert.assertNotNull(destination.getOne());
        Assert.assertNotNull(destination.getOne().getMany());
        Assert.assertFalse(destination.getOne().getMany().isEmpty());
        for (BidirectionalManyConvert guiAccessObject : destination.getOne().getMany()) {
            Assert.assertNotNull(guiAccessObject.getOne());
        }
    }

    class AssociatedEntityConverter extends DozerConverter<BidirectionalOneConvert, BidirectionalOne> implements MapperAware {
        private Mapper mapper;

        AssociatedEntityConverter() {
            super(BidirectionalOneConvert.class, BidirectionalOne.class);
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public BidirectionalOneConvert convertFrom(BidirectionalOne arg0, BidirectionalOneConvert arg1) {
            return mapper.map(arg0, BidirectionalOneConvert.class);
        }

        @Override
        public BidirectionalOne convertTo(BidirectionalOneConvert arg0, BidirectionalOne arg1) {
            return mapper.map(arg0, BidirectionalOne.class);
        }
    }

    class CollectionConverter extends DozerConverter<Set, Set> implements MapperAware {
        private Mapper mapper;

        CollectionConverter() {
            super(Set.class, Set.class);
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        public Set convertTo(Set source, Set destination) {
            return convert(source);
        }

        private Set convert(Set source) {
            Set result = new HashSet();
            for (Object object : source) {
                if (object instanceof BidirectionalManyConvert) {
                    result.add(mapper.map(object, BidirectionalMany.class));
                } else {
                    result.add(mapper.map(object, BidirectionalManyConvert.class));
                }
            }
            return result;
        }

        @Override
        public Set convertFrom(Set source, Set destination) {
            return convert(source);
        }
    }
}

