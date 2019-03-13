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
package com.liferay.portal.search.elasticsearch6.internal.index;


import LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE;
import StringPool.BLANK;
import StringPool.SPACE;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.document.SingleFieldFixture;
import com.liferay.portal.search.elasticsearch6.internal.settings.BaseIndexSettingsContributor;
import com.liferay.portal.search.elasticsearch6.settings.IndexSettingsHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.common.settings.Settings;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class CompanyIndexFactoryTest {
    @Test
    public void testAdditionalIndexConfigurations() throws Exception {
        _companyIndexFactory.setAdditionalIndexConfigurations("index.number_of_replicas: 1\nindex.number_of_shards: 2");
        createIndices();
        Settings settings = getIndexSettings();
        Assert.assertEquals("1", settings.get("index.number_of_replicas"));
        Assert.assertEquals("2", settings.get("index.number_of_shards"));
    }

    @Test
    public void testAdditionalTypeMappings() throws Exception {
        _companyIndexFactory.setAdditionalTypeMappings(loadAdditionalTypeMappings());
        assertAdditionalTypeMappings();
    }

    @Test
    public void testAdditionalTypeMappingsFromContributor() throws Exception {
        addIndexSettingsContributor(loadAdditionalTypeMappings());
        assertAdditionalTypeMappings();
    }

    @Test
    public void testAdditionalTypeMappingsWithRootType() throws Exception {
        _companyIndexFactory.setAdditionalTypeMappings(loadAdditionalTypeMappingsWithRootType());
        assertAdditionalTypeMappings();
    }

    @Test
    public void testAdditionalTypeMappingsWithRootTypeFromContributor() throws Exception {
        addIndexSettingsContributor(loadAdditionalTypeMappingsWithRootType());
        assertAdditionalTypeMappings();
    }

    @Test
    public void testCreateIndicesWithBlankStrings() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("additionalIndexConfigurations", BLANK);
        properties.put("additionalTypeMappings", SPACE);
        properties.put("indexNumberOfReplicas", BLANK);
        properties.put("indexNumberOfShards", SPACE);
        _companyIndexFactory.activate(properties);
        createIndices();
    }

    @Test
    public void testCreateIndicesWithEmptyConfiguration() throws Exception {
        _companyIndexFactory.activate(new HashMap<String, Object>());
        createIndices();
    }

    @Test
    public void testDefaultIndexSettings() throws Exception {
        createIndices();
        Settings settings = getIndexSettings();
        Assert.assertEquals("0", settings.get("index.number_of_replicas"));
        Assert.assertEquals("1", settings.get("index.number_of_shards"));
    }

    @Test
    public void testDefaultIndices() throws Exception {
        _companyIndexFactory.activate(Collections.emptyMap());
        createIndices();
        assertIndicesExist(LIFERAY_DOCUMENT_TYPE);
    }

    @Test
    public void testIndexConfigurations() throws Exception {
        _companyIndexFactory.setIndexNumberOfReplicas("1");
        _companyIndexFactory.setIndexNumberOfShards("2");
        createIndices();
        Settings settings = getIndexSettings();
        Assert.assertEquals("1", settings.get("index.number_of_replicas"));
        Assert.assertEquals("2", settings.get("index.number_of_shards"));
    }

    @Test
    public void testIndexSettingsContributor() throws Exception {
        _companyIndexFactory.addIndexSettingsContributor(new BaseIndexSettingsContributor(1) {
            @Override
            public void populate(IndexSettingsHelper indexSettingsHelper) {
                indexSettingsHelper.put("index.number_of_replicas", "2");
                indexSettingsHelper.put("index.number_of_shards", "3");
            }
        });
        _companyIndexFactory.setAdditionalIndexConfigurations("index.number_of_replicas: 0\nindex.number_of_shards: 0");
        createIndices();
        Settings settings = getIndexSettings();
        Assert.assertEquals("2", settings.get("index.number_of_replicas"));
        Assert.assertEquals("3", settings.get("index.number_of_shards"));
    }

    @Test
    public void testIndexSettingsContributorTypeMappings() throws Exception {
        final String mappings = loadAdditionalTypeMappings();
        addIndexSettingsContributor(replaceAnalyzer(mappings, "brazilian"));
        _companyIndexFactory.setAdditionalTypeMappings(replaceAnalyzer(mappings, "portuguese"));
        createIndices();
        String field = (RandomTestUtil.randomString()) + "_ja";
        indexOneDocument(field);
        assertAnalyzer(field, "brazilian");
    }

    @Test
    public void testOverrideTypeMappings() throws Exception {
        _companyIndexFactory.setAdditionalIndexConfigurations(loadAdditionalAnalyzers());
        _companyIndexFactory.setOverrideTypeMappings(loadOverrideTypeMappings());
        createIndices();
        String field = "title";
        indexOneDocument(field);
        assertAnalyzer(field, "kuromoji_liferay_custom");
        String field2 = "description";
        indexOneDocument(field2);
        assertNoAnalyzer(field2);
    }

    @Test
    public void testOverrideTypeMappingsHonorDefaultIndices() throws Exception {
        _companyIndexFactory.activate(Collections.emptyMap());
        _companyIndexFactory.setAdditionalIndexConfigurations(loadAdditionalAnalyzers());
        _companyIndexFactory.setOverrideTypeMappings(loadOverrideTypeMappings());
        createIndices();
        assertIndicesExist(LIFERAY_DOCUMENT_TYPE);
    }

    @Test
    public void testOverrideTypeMappingsIgnoreOtherContributions() throws Exception {
        String mappings = replaceAnalyzer(loadAdditionalTypeMappings(), RandomTestUtil.randomString());
        addIndexSettingsContributor(mappings);
        _companyIndexFactory.setAdditionalIndexConfigurations(loadAdditionalAnalyzers());
        _companyIndexFactory.setAdditionalTypeMappings(mappings);
        _companyIndexFactory.setOverrideTypeMappings(loadOverrideTypeMappings());
        createIndices();
        String field = (RandomTestUtil.randomString()) + "_ja";
        indexOneDocument(field);
        assertNoAnalyzer(field);
    }

    @Rule
    public TestName testName = new TestName();

    protected class TestIndexNameBuilder implements IndexNameBuilder {
        @Override
        public String getIndexName(long companyId) {
            return getTestIndexName();
        }
    }

    private CompanyIndexFactory _companyIndexFactory;

    private ElasticsearchFixture _elasticsearchFixture;

    private SingleFieldFixture _singleFieldFixture;
}

