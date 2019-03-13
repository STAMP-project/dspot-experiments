/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.field;


import EsMajorVersion.V_7_X;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.rest.InitializationUtils;
import org.elasticsearch.hadoop.serialization.MapFieldExtractor;
import org.elasticsearch.hadoop.util.ObjectUtils;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultIndexExtractorTest {
    private static final Log LOG = LogFactory.getLog(DefaultIndexExtractorTest.class);

    @Test
    public void createFieldExtractor() {
        Settings settings = new TestSettings();
        settings.setResourceWrite("test/{field}");
        settings.setInternalVersion(V_7_X);
        InitializationUtils.setFieldExtractorIfNotSet(settings, MapFieldExtractor.class, DefaultIndexExtractorTest.LOG);
        IndexExtractor iformat = ObjectUtils.instantiate(settings.getMappingIndexExtractorClassName(), settings);
        iformat.compile(new org.elasticsearch.hadoop.rest.Resource(settings, false).toString());
        Assert.assertThat(iformat.hasPattern(), Matchers.is(true));
        Map<String, String> data = new HashMap<String, String>();
        data.put("field", "data");
        Object field = iformat.field(data);
        Assert.assertThat(field.toString(), Matchers.equalTo("\"_index\":\"test\",\"_type\":\"data\""));
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void createFieldExtractorNull() {
        Settings settings = new TestSettings();
        settings.setResourceWrite("test/{field}");
        settings.setInternalVersion(V_7_X);
        InitializationUtils.setFieldExtractorIfNotSet(settings, MapFieldExtractor.class, DefaultIndexExtractorTest.LOG);
        IndexExtractor iformat = ObjectUtils.instantiate(settings.getMappingIndexExtractorClassName(), settings);
        iformat.compile(new org.elasticsearch.hadoop.rest.Resource(settings, false).toString());
        Assert.assertThat(iformat.hasPattern(), Matchers.is(true));
        Map<String, String> data = new HashMap<String, String>();
        data.put("field", null);
        iformat.field(data);
        Assert.fail();
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void createFieldExtractorFailure() {
        Settings settings = new TestSettings();
        settings.setResourceWrite("test/{optional}");
        settings.setInternalVersion(V_7_X);
        InitializationUtils.setFieldExtractorIfNotSet(settings, MapFieldExtractor.class, DefaultIndexExtractorTest.LOG);
        IndexExtractor iformat = ObjectUtils.instantiate(settings.getMappingIndexExtractorClassName(), settings);
        iformat.compile(new org.elasticsearch.hadoop.rest.Resource(settings, false).toString());
        Assert.assertThat(iformat.hasPattern(), Matchers.is(true));
        Map<String, String> data = new HashMap<String, String>();
        data.put("field", "data");
        iformat.field(data);
        Assert.fail();
    }
}

