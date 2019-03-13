/**
 * Copyright 2008 the original author or authors.
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
package org.springframework.batch.item.file;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SimpleResourceSuffixCreator}.
 */
public class SimpleResourceSuffixCreatorTests {
    private SimpleResourceSuffixCreator tested = new SimpleResourceSuffixCreator();

    @Test
    public void testGetSuffix() {
        Assert.assertEquals(".0", tested.getSuffix(0));
        Assert.assertEquals(".1", tested.getSuffix(1));
        Assert.assertEquals(".3463457", tested.getSuffix(3463457));
    }
}

