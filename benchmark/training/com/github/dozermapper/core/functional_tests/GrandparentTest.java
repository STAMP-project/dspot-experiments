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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.excluded.TwoA;
import com.github.dozermapper.core.vo.excluded.TwoB;
import org.junit.Assert;
import org.junit.Test;


public class GrandparentTest extends AbstractFunctionalTest {
    @Test
    public void testParentProperty() {
        TwoA source = newInstance(TwoA.class);
        source.setId(new Integer(1));
        TwoB dest = mapper.map(source, TwoB.class);
        Assert.assertNotNull(dest);
        Assert.assertEquals(new Integer(1), dest.getId());
    }
}

