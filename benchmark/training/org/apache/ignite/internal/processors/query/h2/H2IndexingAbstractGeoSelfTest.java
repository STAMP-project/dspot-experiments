/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.query.h2;


import java.io.Serializable;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;


/**
 * Geo-indexing test.
 */
public abstract class H2IndexingAbstractGeoSelfTest extends GridCacheAbstractSelfTest {
    /**
     *
     */
    private static final int CNT = 100;

    /**
     *
     */
    private static final long DUR = 60000L;

    /**
     * Number of generated samples.
     */
    public static final int ENEMYCAMP_SAMPLES_COUNT = 500;

    /**
     * Number of generated samples.
     */
    public static final int ENEMY_SAMPLES_COUNT = 1000;

    /**
     *
     */
    private static final int QRY_PARALLELISM_LVL = 7;

    /**
     * Segmented index flag.
     */
    private final boolean segmented;

    /**
     * Constructor.
     *
     * @param segmented
     * 		Segmented index flag.
     */
    protected H2IndexingAbstractGeoSelfTest(boolean segmented) {
        this.segmented = segmented;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrimitiveGeometry() throws Exception {
        IgniteCache<Long, Geometry> cache = createCache("geom", true, Long.class, Geometry.class);
        try {
            WKTReader r = new WKTReader();
            for (long i = 0; i < 100; i++)
                cache.put(i, r.read((((("POINT(" + i) + " ") + i) + ")")));

            String plan = cache.query(new SqlFieldsQuery("explain select _key from Geometry where _val && ?").setArgs(r.read("POLYGON((5 70, 5 80, 30 80, 30 70, 5 70))")).setLocal(true)).getAll().get(0).get(0).toString().toLowerCase();
            assertTrue(("__ explain: " + plan), plan.contains("_val_idx"));
        } finally {
            cache.destroy();
        }
    }

    /**
     * Test geo-index (static).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGeo() throws Exception {
        checkGeo(false);
    }

    /**
     * Test geo-index (dynamic).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGeoDynamic() throws Exception {
        checkGeo(true);
    }

    /**
     * Test geo indexing multithreaded.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGeoMultithreaded() throws Exception {
        checkGeoMultithreaded(false);
    }

    /**
     * Test geo indexing multithreaded with dynamic index creation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGeoMultithreadedDynamic() throws Exception {
        checkGeoMultithreaded(true);
    }

    /**
     * Test segmented geo-index join on PARTITIONED cache.
     *
     * @throws Exception
     * 		if fails.
     */
    @Test
    public void testSegmentedGeoIndexJoinPartitioned() throws Exception {
        checkSegmentedGeoIndexJoin(true, false);
    }

    /**
     * Test segmented geo-index join on PARTITIONED cache with dynamically created index.
     *
     * @throws Exception
     * 		if fails.
     */
    @Test
    public void testSegmentedGeoIndexJoinPartitionedDynamic() throws Exception {
        checkSegmentedGeoIndexJoin(true, true);
    }

    /**
     * Test segmented geo-index join on REPLICATED cache.
     *
     * @throws Exception
     * 		if fails.
     */
    @Test
    public void testSegmentedGeoIndexJoinReplicated() throws Exception {
        checkSegmentedGeoIndexJoin(false, false);
    }

    /**
     * Test segmented geo-index join on REPLICATED cache with dynamically created index.
     *
     * @throws Exception
     * 		if fails.
     */
    @Test
    public void testSegmentedGeoIndexJoinReplicatedDynamic() throws Exception {
        checkSegmentedGeoIndexJoin(false, true);
    }

    /**
     *
     */
    private static class Enemy {
        /**
         *
         */
        @QuerySqlField(index = true)
        int campId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param campId
         * 		Camp ID.
         * @param name
         * 		Name.
         */
        public Enemy(int campId, String name) {
            this.campId = campId;
            this.name = name;
        }
    }

    /**
     *
     */
    protected static class EnemyCamp implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        Geometry coords;

        /**
         *
         */
        @QuerySqlField
        private String name;

        /**
         *
         *
         * @param coords
         * 		Coordinates.
         * @param name
         * 		Name.
         */
        EnemyCamp(Geometry coords, String name) {
            this.coords = coords;
            this.name = name;
        }
    }
}

