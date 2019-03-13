/**
 * Copyright (c) 2013-2018 YCSB contributors. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. See accompanying LICENSE file.
 * <p>
 */
package com.yahoo.ycsb.db.ignite;


import Status.NOT_IMPLEMENTED;
import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.ignite.Ignite;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Common test class.
 */
public class IgniteClientTestBase {
    protected static final String DEFAULT_CACHE_NAME = "usertable";

    /**
     *
     */
    protected static Ignite cluster;

    /**
     *
     */
    protected DB client;

    @Test
    public void scanNotImplemented() {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key = "key";
        final Map<String, String> input = new HashMap<>();
        input.put("field0", "value1");
        input.put("field1", "value2");
        final Status status = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(status, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
        final Vector<HashMap<String, ByteIterator>> results = new Vector<>();
        final Status scan = client.scan(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, 1, null, results);
        MatcherAssert.assertThat(scan, Matchers.is(NOT_IMPLEMENTED));
    }
}

