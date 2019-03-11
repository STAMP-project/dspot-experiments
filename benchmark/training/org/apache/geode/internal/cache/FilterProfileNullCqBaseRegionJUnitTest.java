/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.RegionNotFoundException;
import org.apache.geode.cache.query.internal.CqStateImpl;
import org.apache.geode.cache.query.internal.cq.CqService;
import org.apache.geode.cache.query.internal.cq.ServerCQ;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FilterProfileNullCqBaseRegionJUnitTest {
    private FilterProfile filterProfile;

    private CqService mockCqService;

    private GemFireCacheImpl mockCache;

    private CqStateImpl cqState;

    private ServerCQ serverCQ;

    @Test
    public void whenCqBaseRegionIsNullThenTheCqShouldNotBeAddedToTheCqMap() throws CqException, RegionNotFoundException {
        Mockito.doThrow(RegionNotFoundException.class).when(serverCQ).registerCq(ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.anyInt());
        filterProfile = new FilterProfile();
        filterProfile.processRegisterCq("TestCq", serverCQ, true, mockCache);
        Assert.assertEquals(0, filterProfile.getCqMap().size());
        filterProfile.processCloseCq("TestCq");
    }
}

