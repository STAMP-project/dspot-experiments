/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
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


import UpdateOperation.UpdateMessage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UpdateOperationJUnitTest {
    EntryEventImpl event;

    UpdateMessage message;

    DistributedRegion region;

    /**
     * AUO's doPutOrCreate will try with create first. If it succeed, it will not try update
     * retry update
     */
    @Test
    public void createSucceedShouldNotRetryAnymore() {
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(true);
        message.basicOperateOnRegion(event, region);
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(0)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(0)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
    }

    /**
     * AUO's doPutOrCreate will try with create first. If it failed with ConcurrencyConflict, should
     * not
     * retry update
     */
    @Test
    public void createFailWithConcurrencyConflictShouldNotRetry() {
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(false);
        Mockito.when(event.isConcurrencyConflict()).thenReturn(true);
        message.basicOperateOnRegion(event, region);
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(0)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(0)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
    }

    /**
     * AUO's doPutOrCreate will try with create first. If it failed, it will try update.
     * If update succeed, no more retry
     */
    @Test
    public void updateSucceedShouldNotRetryAnymore() {
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(false);
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(true);
        message.basicOperateOnRegion(event, region);
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(0)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
    }

    /**
     * AUO's doPutOrCreate() will try with create, then update.
     * If retry again, it should be an update with ifNew==false and ifOld==false
     * It should not retry with create again
     */
    @Test
    public void doPutOrCreate3rdRetryShouldBeUpdate() {
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(false);
        Mockito.when(region.basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true))).thenReturn(false);
        message.basicOperateOnRegion(event, region);
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
        Mockito.verify(region, Mockito.times(1)).basicUpdate(ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
    }
}

