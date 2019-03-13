/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.suggest.popular.impl;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.suggest.popular.impl.chronicle.ChronicleMapAdapter;


public class ChronicleMapAdapterTest {
    private static final String FIELD = "test";

    private ChronicleMapAdapter map;

    private Path tempFile;

    @Test
    public void dataNotLostAfterResizeTest() throws IOException {
        fillData(0, 10, map);
        map.resize(20, 20);
        checkData(10, map);
    }

    @Test
    public void testResize() throws IOException {
        fillData(0, 10, map);
        map.resize(500, 20);
        fillData(10, 500, map);
        checkData(500, map);
    }

    // for contains()
    @Test
    @SuppressWarnings("unchecked")
    public void testGetPopularityData() {
        Map.Entry<BytesRef, Integer> e1 = new java.util.AbstractMap.SimpleEntry(new BytesRef("test"), 1);
        Map.Entry<BytesRef, Integer> e2 = new java.util.AbstractMap.SimpleEntry(new BytesRef("test2"), 2);
        map.increment(e1.getKey(), e1.getValue());
        map.increment(e2.getKey(), e2.getValue());
        List<Map.Entry<BytesRef, Integer>> data = map.getPopularityData(0, 10);
        Assert.assertThat(data, contains(e2, e1));
    }

    // for contains()
    @Test
    @SuppressWarnings("unchecked")
    public void testGetPopularityPaging() {
        Map.Entry<BytesRef, Integer> e1 = new java.util.AbstractMap.SimpleEntry(new BytesRef("test"), 1);
        Map.Entry<BytesRef, Integer> e2 = new java.util.AbstractMap.SimpleEntry(new BytesRef("test2"), 2);
        Map.Entry<BytesRef, Integer> e3 = new java.util.AbstractMap.SimpleEntry(new BytesRef("test3"), 3);
        map.increment(e1.getKey(), e1.getValue());
        map.increment(e2.getKey(), e2.getValue());
        map.increment(e3.getKey(), e3.getValue());
        List<Map.Entry<BytesRef, Integer>> data = map.getPopularityData(0, 2);
        Assert.assertThat(data, contains(e3, e2));
        data = map.getPopularityData(1, 2);
        Assert.assertThat(data, contains(e1));
    }
}

