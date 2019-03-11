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


import com.github.dozermapper.core.vo.copybyreference.ReferenceHolderA;
import com.github.dozermapper.core.vo.copybyreference.ReferenceHolderB;
import com.github.dozermapper.core.vo.copybyreference.ReferencedObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Tests verify that CopyBtReference configuration is used for both one-way mapping and bi-directional mapping.
 */
public class CopyByReferenceFieldLevelOneWayTest extends AbstractFunctionalTest {
    @Test
    public void testOneWay() {
        ReferenceHolderA sourceBean = new ReferenceHolderA();
        ReferencedObject sourceReferencedObject = new ReferencedObject();
        sourceBean.setReferencedObject(sourceReferencedObject);
        ReferenceHolderB destinationBean = new ReferenceHolderB();
        mapper.map(sourceBean, destinationBean, "one-way");
        ReferencedObject mappedReferenceObject = destinationBean.getReferencedObject();
        MatcherAssert.assertThat(mappedReferenceObject, Matchers.sameInstance(sourceReferencedObject));
    }

    @Test
    public void testBidirectional() {
        ReferenceHolderA sourceBean = new ReferenceHolderA();
        ReferencedObject sourceReferencedObject = new ReferencedObject();
        sourceBean.setReferencedObject(sourceReferencedObject);
        ReferenceHolderB destinationBean = new ReferenceHolderB();
        mapper.map(sourceBean, destinationBean, "bi-directional");
        ReferencedObject mappedReferenceObject = destinationBean.getReferencedObject();
        MatcherAssert.assertThat(mappedReferenceObject, Matchers.sameInstance(sourceReferencedObject));
    }
}

