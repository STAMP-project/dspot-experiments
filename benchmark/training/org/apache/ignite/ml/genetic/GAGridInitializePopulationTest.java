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
package org.apache.ignite.ml.genetic;


import GAGridConstants.GENE_CACHE;
import GAGridConstants.POPULATION_CACHE;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.junit.Assert;
import org.junit.Test;


/**
 * Initialize Gene and Chromosome Test
 */
public class GAGridInitializePopulationTest {
    /**
     * Ignite instance
     */
    private Ignite ignite = null;

    /**
     * GAGrid *
     */
    private GAGrid gaGrid = null;

    @Test
    public void testInitializeGenes() {
        try {
            IgniteCache<Long, Gene> geneCache = ignite.cache(GENE_CACHE);
            gaGrid.initializeGenePopulation();
            String sql = "select count(*) from Gene";
            // Execute query to keys for ALL Chromosomes by fittnessScore
            QueryCursor<List<?>> cursor = geneCache.query(new SqlFieldsQuery(sql));
            List<List<?>> res = cursor.getAll();
            Long cnt = 0L;
            for (List row : res)
                cnt = ((Long) (row.get(0)));

            Assert.assertEquals(83, cnt.longValue());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testInitializePopulation() {
        try {
            IgniteCache<Long, Chromosome> populationCache = ignite.cache(POPULATION_CACHE);
            gaGrid.initializePopulation();
            String sql = "select count(*) from Chromosome";
            // Execute query to keys for ALL Chromosomes by fittnessScore
            QueryCursor<List<?>> cursor = populationCache.query(new SqlFieldsQuery(sql));
            List<List<?>> res = cursor.getAll();
            Long cnt = 0L;
            for (List row : res)
                cnt = ((Long) (row.get(0)));

            Assert.assertEquals(500, cnt.longValue());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

