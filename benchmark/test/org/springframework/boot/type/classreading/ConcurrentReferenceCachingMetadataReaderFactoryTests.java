/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.type.classreading;


import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;


/**
 * Tests for {@link ConcurrentReferenceCachingMetadataReaderFactory}.
 *
 * @author Phillip Webb
 */
public class ConcurrentReferenceCachingMetadataReaderFactoryTests {
    @Test
    public void getMetadataReaderUsesCache() throws Exception {
        ConcurrentReferenceCachingMetadataReaderFactoryTests.TestConcurrentReferenceCachingMetadataReaderFactory factory = Mockito.spy(new ConcurrentReferenceCachingMetadataReaderFactoryTests.TestConcurrentReferenceCachingMetadataReaderFactory());
        MetadataReader metadataReader1 = getMetadataReader(getClass().getName());
        MetadataReader metadataReader2 = getMetadataReader(getClass().getName());
        assertThat(metadataReader1).isSameAs(metadataReader2);
        Mockito.verify(factory, Mockito.times(1)).createMetadataReader(ArgumentMatchers.any(Resource.class));
    }

    @Test
    public void clearResetsCache() throws Exception {
        ConcurrentReferenceCachingMetadataReaderFactoryTests.TestConcurrentReferenceCachingMetadataReaderFactory factory = Mockito.spy(new ConcurrentReferenceCachingMetadataReaderFactoryTests.TestConcurrentReferenceCachingMetadataReaderFactory());
        MetadataReader metadataReader1 = getMetadataReader(getClass().getName());
        clearCache();
        MetadataReader metadataReader2 = getMetadataReader(getClass().getName());
        assertThat(metadataReader1).isNotEqualTo(Matchers.sameInstance(metadataReader2));
        Mockito.verify(factory, Mockito.times(2)).createMetadataReader(ArgumentMatchers.any(Resource.class));
    }

    private static class TestConcurrentReferenceCachingMetadataReaderFactory extends ConcurrentReferenceCachingMetadataReaderFactory {
        @Override
        public MetadataReader createMetadataReader(Resource resource) {
            return Mockito.mock(MetadataReader.class);
        }
    }
}

