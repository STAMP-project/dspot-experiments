/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks.base;


import java.util.Collection;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PairCheckStorageListTest {
    private PairCheckStorage pairCheckStorage;

    @Mock
    private RuleInspector a;

    @Mock
    private RuleInspector b;

    @Mock
    private RuleInspector c;

    private PairCheckBundle pairCheckListOne;

    private PairCheckBundle pairCheckListTwo;

    @Test
    public void getA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.get(a);
        Assert.assertEquals(4, pairCheckLists.size());
        Assert.assertTrue(pairCheckLists.contains(pairCheckListOne));
        Assert.assertTrue(pairCheckLists.contains(pairCheckListTwo));
    }

    @Test
    public void getB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.get(b);
        Assert.assertEquals(2, pairCheckLists.size());
        Assert.assertTrue(pairCheckLists.contains(pairCheckListOne));
        Assert.assertTrue(pairCheckLists.contains(pairCheckListTwo));
    }

    @Test
    public void removeB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.remove(b);
        Assert.assertEquals(2, pairCheckLists.size());
        Assert.assertTrue(pairCheckLists.contains(pairCheckListOne));
        Assert.assertTrue(pairCheckLists.contains(pairCheckListTwo));
        Assert.assertTrue(this.pairCheckStorage.get(b).isEmpty());
        final Collection<PairCheckBundle> pairChecksForAList = this.pairCheckStorage.get(a);
        Assert.assertEquals(2, pairChecksForAList.size());
        Assert.assertFalse(pairChecksForAList.contains(pairCheckListOne));
        Assert.assertFalse(pairChecksForAList.contains(pairCheckListTwo));
    }

    @Test
    public void removeA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.remove(a);
        Assert.assertEquals(4, pairCheckLists.size());
        Assert.assertTrue(this.pairCheckStorage.get(a).isEmpty());
        Assert.assertTrue(this.pairCheckStorage.get(b).isEmpty());
        Assert.assertTrue(this.pairCheckStorage.get(c).isEmpty());
    }
}

