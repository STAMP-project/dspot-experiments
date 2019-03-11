/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.core.aggregation;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


/**
 *
 *
 * @author Remco Zigterman
 */
public class AggregatedPageImplTest {
    @Test
    public void constructFacetedPageWithPageable() {
        Page<String> page = new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(Arrays.asList("Test", "Test 2"), PageRequest.of(0, 2), 10);
        Assert.assertEquals(10, page.getTotalElements());
        Assert.assertEquals(2, page.getNumberOfElements());
        Assert.assertEquals(2, page.getSize());
        Assert.assertEquals(5, page.getTotalPages());
    }
}

