/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.segments.internal.asah.client.data.binding;


import com.liferay.segments.internal.asah.client.model.Individual;
import com.liferay.segments.internal.asah.client.model.Results;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Arques
 */
public class IndividualJSONObjectMapperTest {
    @Test
    public void testMap() throws Exception {
        Individual individual = IndividualJSONObjectMapperTest._individualJSONObjectMapper.map(_read("get-individual.json"));
        Assert.assertNotNull(individual);
        Assert.assertEquals("327206046201282216", individual.getId());
        Map<String, Set<String>> dataSourceIndividualPKs = individual.getDataSourceIndividualPKs();
        Assert.assertEquals(new HashSet<>(Collections.singletonList("fa08b246-2f0c-25c3-588a-6da21da8cb46")), dataSourceIndividualPKs.get("327205908165727542"));
    }

    @Test(expected = IOException.class)
    public void testMapThrowsIOException() throws Exception {
        IndividualJSONObjectMapperTest._individualJSONObjectMapper.map("invalid json");
    }

    @Test
    public void testMapToResults() throws Exception {
        Results<Individual> results = IndividualJSONObjectMapperTest._individualJSONObjectMapper.mapToResults(_read("get-individuals.json"));
        Assert.assertEquals(1, results.getTotal());
        List<Individual> individuals = results.getItems();
        Individual individual = individuals.get(0);
        Assert.assertEquals("327206046201282216", individual.getId());
        Map<String, Set<String>> dataSourceIndividualPKs = individual.getDataSourceIndividualPKs();
        Assert.assertEquals(new HashSet<>(Collections.singletonList("fa08b246-2f0c-25c3-588a-6da21da8cb46")), dataSourceIndividualPKs.get("327205908165727542"));
    }

    @Test(expected = IOException.class)
    public void testMapToResultsThrowsIOException() throws Exception {
        IndividualJSONObjectMapperTest._individualJSONObjectMapper.mapToResults("invalid json");
    }

    @Test
    public void testMapToResultsWithNoResults() throws Exception {
        Results<Individual> results = IndividualJSONObjectMapperTest._individualJSONObjectMapper.mapToResults(_read("get-individuals-no-results.json"));
        Assert.assertEquals(0, results.getTotal());
        List<Individual> individuals = results.getItems();
        Assert.assertEquals(individuals.toString(), 0, individuals.size());
    }

    private static final IndividualJSONObjectMapper _individualJSONObjectMapper = new IndividualJSONObjectMapper();
}

