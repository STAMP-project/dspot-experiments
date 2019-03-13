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
package com.github.dozermapper.core.classmap;


import RelationshipType.CUMULATIVE;
import RelationshipType.NON_CUMULATIVE;
import com.github.dozermapper.core.AbstractDozerTest;
import org.junit.Assert;
import org.junit.Test;


public class RelationshipTypeTest extends AbstractDozerTest {
    @Test
    public void testValueOf() {
        Assert.assertNull(RelationshipType.valueOf(""));
        Assert.assertNull(RelationshipType.valueOf(null));
        Assert.assertEquals(CUMULATIVE, RelationshipType.valueOf("cumulative"));
        Assert.assertEquals(NON_CUMULATIVE, RelationshipType.valueOf("non-cumulative"));
    }
}

