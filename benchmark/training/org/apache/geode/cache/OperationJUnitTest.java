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
package org.apache.geode.cache;


import org.junit.Assert;
import org.junit.Test;

import static Operation.CACHE_CLOSE;
import static Operation.CACHE_CREATE;
import static Operation.CREATE;
import static Operation.DESTROY;
import static Operation.EVICT_DESTROY;
import static Operation.EXPIRE_DESTROY;
import static Operation.EXPIRE_INVALIDATE;
import static Operation.EXPIRE_LOCAL_DESTROY;
import static Operation.EXPIRE_LOCAL_INVALIDATE;
import static Operation.INVALIDATE;
import static Operation.LOCAL_DESTROY;
import static Operation.LOCAL_INVALIDATE;
import static Operation.LOCAL_LOAD_CREATE;
import static Operation.LOCAL_LOAD_UPDATE;
import static Operation.NET_LOAD_CREATE;
import static Operation.NET_LOAD_UPDATE;
import static Operation.PUTALL_CREATE;
import static Operation.PUTALL_UPDATE;
import static Operation.REGION_CLEAR;
import static Operation.REGION_CLOSE;
import static Operation.REGION_CREATE;
import static Operation.REGION_DESTROY;
import static Operation.REGION_EXPIRE_DESTROY;
import static Operation.REGION_EXPIRE_INVALIDATE;
import static Operation.REGION_EXPIRE_LOCAL_DESTROY;
import static Operation.REGION_EXPIRE_LOCAL_INVALIDATE;
import static Operation.REGION_INVALIDATE;
import static Operation.REGION_LOAD_SNAPSHOT;
import static Operation.REGION_LOCAL_CLEAR;
import static Operation.REGION_LOCAL_DESTROY;
import static Operation.REGION_LOCAL_INVALIDATE;
import static Operation.REGION_REINITIALIZE;
import static Operation.REMOVEALL_DESTROY;
import static Operation.SEARCH_CREATE;
import static Operation.SEARCH_UPDATE;
import static Operation.UPDATE;
import static Operation.UPDATE_VERSION_STAMP;


public class OperationJUnitTest {
    /**
     * Check CREATE Operation.
     */
    @Test
    public void testCREATE() {
        Operation op = CREATE;
        Assert.assertTrue(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check PUTALL_CREATE Operation.
     */
    @Test
    public void testPUTALL_CREATE() {
        Operation op = PUTALL_CREATE;
        Assert.assertTrue(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertTrue(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check SEARCH_CREATE Operation.
     */
    @Test
    public void testSEARCH_CREATE() {
        Operation op = SEARCH_CREATE;
        Assert.assertTrue(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertTrue(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check LOCAL_LOAD_CREATE Operation.
     */
    @Test
    public void testLOCAL_LOAD_CREATE() {
        Operation op = LOCAL_LOAD_CREATE;
        Assert.assertTrue(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertTrue(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertTrue(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check NET_LOAD_CREATE Operation.
     */
    @Test
    public void testNET_LOAD_CREATE() {
        Operation op = NET_LOAD_CREATE;
        Assert.assertTrue(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertTrue(op.isNetLoad());
        Assert.assertTrue(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check UPDATE Operation.
     */
    @Test
    public void testUPDATE() {
        Operation op = UPDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertTrue(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check PUTALL_UPDATE Operation.
     */
    @Test
    public void testPUTALL_UPDATE() {
        Operation op = PUTALL_UPDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertTrue(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertTrue(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check SEARCH_UPDATE Operation.
     */
    @Test
    public void testSEARCH_UPDATE() {
        Operation op = SEARCH_UPDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertTrue(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertTrue(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check LOCAL_LOAD_UPDATE Operation.
     */
    @Test
    public void testLOCAL_LOAD_UPDATE() {
        Operation op = LOCAL_LOAD_UPDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertTrue(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertTrue(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertTrue(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check NET_LOAD_UPDATE Operation.
     */
    @Test
    public void testNET_LOAD_UPDATE() {
        Operation op = NET_LOAD_UPDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertTrue(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertTrue(op.isNetLoad());
        Assert.assertTrue(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check INVALIDATE Operation.
     */
    @Test
    public void testINVALIDATE() {
        Operation op = INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertTrue(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check LOCAL_INVALIDATE Operation.
     */
    @Test
    public void testLOCAL_INVALIDATE() {
        Operation op = LOCAL_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertTrue(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check DESTROY Operation.
     */
    @Test
    public void testDESTROY() {
        Operation op = DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REMOVEALL Operation.
     */
    @Test
    public void testREMOVEALL() {
        Operation op = REMOVEALL_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertTrue(op.isRemoveAll());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check LOCAL_DESTROY Operation.
     */
    @Test
    public void testLOCAL_DESTROY() {
        Operation op = LOCAL_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check EVICT_DESTROY Operation.
     */
    @Test
    public void testEVICT_DESTROY() {
        Operation op = EVICT_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_LOAD_SNAPSHOT Operation.
     */
    @Test
    public void testREGION_LOAD_SNAPSHOT() {
        Operation op = REGION_LOAD_SNAPSHOT;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_LOCAL_DESTROY Operation.
     */
    @Test
    public void testREGION_LOCAL_DESTROY() {
        Operation op = REGION_LOCAL_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_CREATE Operation.
     */
    @Test
    public void testREGION_CREATE() {
        Operation op = REGION_CREATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_CLOSE Operation.
     */
    @Test
    public void testREGION_CLOSE() {
        Operation op = REGION_CLOSE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertTrue(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_DESTROY Operation.
     */
    @Test
    public void testREGION_DESTROY() {
        Operation op = REGION_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check EXPIRE_DESTROY Operation.
     */
    @Test
    public void testEXPIRE_DESTROY() {
        Operation op = EXPIRE_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check EXPIRE_LOCAL_DESTROY Operation.
     */
    @Test
    public void testEXPIRE_LOCAL_DESTROY() {
        Operation op = EXPIRE_LOCAL_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertTrue(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check EXPIRE_INVALIDATE Operation.
     */
    @Test
    public void testEXPIRE_INVALIDATE() {
        Operation op = EXPIRE_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertTrue(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check EXPIRE_LOCAL_INVALIDATE Operation.
     */
    @Test
    public void testEXPIRE_LOCAL_INVALIDATE() {
        Operation op = EXPIRE_LOCAL_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertTrue(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_EXPIRE_DESTROY Operation.
     */
    @Test
    public void testREGION_EXPIRE_DESTROY() {
        Operation op = REGION_EXPIRE_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_EXPIRE_LOCAL_DESTROY Operation.
     */
    @Test
    public void testREGION_EXPIRE_LOCAL_DESTROY() {
        Operation op = REGION_EXPIRE_LOCAL_DESTROY;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_EXPIRE_INVALIDATE Operation.
     */
    @Test
    public void testREGION_EXPIRE_INVALIDATE() {
        Operation op = REGION_EXPIRE_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertTrue(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_EXPIRE_LOCAL_INVALIDATE Operation.
     */
    @Test
    public void testREGION_EXPIRE_LOCAL_INVALIDATE() {
        Operation op = REGION_EXPIRE_LOCAL_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertTrue(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertTrue(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_LOCAL_INVALIDATE Operation.
     */
    @Test
    public void testREGION_LOCAL_INVALIDATE() {
        Operation op = REGION_LOCAL_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertTrue(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_INVALIDATE Operation.
     */
    @Test
    public void testREGION_INVALIDATE() {
        Operation op = REGION_INVALIDATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertTrue(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_CLEAR Operation.
     */
    @Test
    public void testREGION_CLEAR() {
        Operation op = REGION_CLEAR;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertTrue(op.isClear());
    }

    /**
     * Check REGION_LOCAL_CLEAR Operation.
     */
    @Test
    public void testREGION_LOCAL_CLEAR() {
        Operation op = REGION_LOCAL_CLEAR;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertTrue(op.isClear());
    }

    /**
     * Check CACHE_CREATE Operation
     */
    @Test
    public void testCACHE_CREATE() {
        Operation op = CACHE_CREATE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check CACHE_CLOSE Operation.
     */
    @Test
    public void testCACHE_CLOSE() {
        Operation op = CACHE_CLOSE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertTrue(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check REGION_REINITIALIZE Operation.
     */
    @Test
    public void testREGION_REINITIALIZE() {
        Operation op = REGION_REINITIALIZE;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertTrue(op.isRegionDestroy());
        Assert.assertTrue(op.isRegion());
        Assert.assertTrue(op.isLocal());
        Assert.assertFalse(op.isDistributed());
        Assert.assertFalse(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }

    /**
     * Check UPDATE_VERSION Operation.
     */
    @Test
    public void testUPDATE_VERSION() {
        Operation op = UPDATE_VERSION_STAMP;
        Assert.assertFalse(op.isCreate());
        Assert.assertFalse(op.isUpdate());
        Assert.assertFalse(op.isInvalidate());
        Assert.assertFalse(op.isDestroy());
        Assert.assertFalse(op.isPutAll());
        Assert.assertFalse(op.isRegionInvalidate());
        Assert.assertFalse(op.isRegionDestroy());
        Assert.assertFalse(op.isRegion());
        Assert.assertFalse(op.isLocal());
        Assert.assertTrue(op.isDistributed());
        Assert.assertTrue(op.isEntry());
        Assert.assertFalse(op.isExpiration());
        Assert.assertFalse(op.isLocalLoad());
        Assert.assertFalse(op.isNetLoad());
        Assert.assertFalse(op.isLoad());
        Assert.assertFalse(op.isNetSearch());
        Assert.assertFalse(op.isClose());
        Assert.assertFalse(op.isClear());
    }
}

