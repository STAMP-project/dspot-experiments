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
package org.elasticsearch.hadoop.serialization;


import ConfigurationOptions.ES_INDEX_AUTO_CREATE;
import ConfigurationOptions.ES_MAPPING_ID;
import ConfigurationOptions.ES_MAPPING_PARENT;
import ConfigurationOptions.ES_MAPPING_ROUTING;
import ConfigurationOptions.ES_MAPPING_TIMESTAMP;
import ConfigurationOptions.ES_MAPPING_TTL;
import ConfigurationOptions.ES_MAPPING_VERSION;
import ConfigurationOptions.ES_OPERATION_UPDATE;
import ConfigurationOptions.ES_UPDATE_RETRY_ON_CONFLICT;
import ConfigurationOptions.ES_UPDATE_SCRIPT_FILE;
import ConfigurationOptions.ES_UPDATE_SCRIPT_INLINE;
import ConfigurationOptions.ES_UPDATE_SCRIPT_LANG;
import ConfigurationOptions.ES_UPDATE_SCRIPT_PARAMS;
import EsMajorVersion.V_1_X;
import EsMajorVersion.V_6_X;
import EsMajorVersion.V_7_X;
import EsMajorVersion.V_8_X;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.util.BytesArray;
import org.elasticsearch.hadoop.util.EsMajorVersion;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CommandTest {
    private final BytesArray ba = new BytesArray(1024);

    private Object data;

    private final String operation;

    private boolean noId = false;

    private boolean jsonInput = false;

    private final EsMajorVersion version;

    public CommandTest(String operation, boolean jsonInput, EsMajorVersion version) {
        this.operation = operation;
        this.jsonInput = jsonInput;
        this.version = version;
    }

    @Test
    public void testNoHeader() throws Exception {
        Assume.assumeFalse(ES_OPERATION_UPDATE.equals(operation));
        create(settings()).write(data).copyTo(ba);
        String result = ((prefix()) + "}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    // check user friendliness and escape the string if needed
    @Test
    public void testConstantId() throws Exception {
        Settings settings = settings();
        noId = true;
        settings.setProperty(ES_MAPPING_ID, "<foobar>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_id\":\"foobar\"}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testParent() throws Exception {
        Assume.assumeTrue(version.onOrBefore(V_6_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_PARENT, "<5>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_parent\":5}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testParent7X() throws Exception {
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_PARENT, "<5>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"parent\":5}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testVersion() throws Exception {
        Assume.assumeTrue(version.onOrBefore(V_6_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_VERSION, "<3>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_version\":3,\"_version_type\":\"external\"}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testVersion7X() throws Exception {
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_VERSION, "<3>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"version\":3,\"version_type\":\"external\"}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testTtl() throws Exception {
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_TTL, "<2>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_ttl\":2}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testTimestamp() throws Exception {
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_TIMESTAMP, "<3>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_timestamp\":3}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testRouting() throws Exception {
        Assume.assumeTrue(version.onOrBefore(V_6_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_ROUTING, "<4>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"_routing\":4}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testRouting7X() throws Exception {
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_ROUTING, "<4>");
        create(settings).write(data).copyTo(ba);
        String result = ((prefix()) + "\"routing\":4}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testAll() throws Exception {
        Assume.assumeTrue(version.onOrBefore(V_6_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_ID, "n");
        settings.setProperty(ES_MAPPING_TTL, "<2>");
        settings.setProperty(ES_MAPPING_ROUTING, "s");
        create(settings).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1,\"_routing\":\"v\",\"_ttl\":2}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testAll7X() throws Exception {
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings settings = settings();
        settings.setProperty(ES_MAPPING_ID, "n");
        settings.setProperty(ES_MAPPING_TTL, "<2>");
        settings.setProperty(ES_MAPPING_ROUTING, "s");
        create(settings).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1,\"routing\":\"v\",\"_ttl\":2}}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testIdPattern() throws Exception {
        Settings settings = settings();
        if (version.onOrAfter(V_8_X)) {
            settings.setResourceWrite("{n}");
        } else {
            settings.setResourceWrite("foo/{n}");
        }
        create(settings).write(data).copyTo(ba);
        String header;
        if (version.onOrAfter(V_8_X)) {
            header = ("{\"_index\":\"1\"" + (isUpdateOp() ? ",\"_id\":2" : "")) + "}";
        } else {
            header = ("{\"_index\":\"foo\",\"_type\":\"1\"" + (isUpdateOp() ? ",\"_id\":2" : "")) + "}";
        }
        String result = (((("{\"" + (operation)) + "\":") + header) + "}") + (map());
        Assert.assertEquals(result, ba.toString());
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void testIdMandatory() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "");
        create(set).write(data).copyTo(ba);
    }

    @Test
    public void testUpdateOnlyInlineScript1X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrBefore(V_1_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = 3");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"_retry_on_conflict\":3}}\n") + "{\"lang\":\"groovy\",\"script\":\"counter = 3\"}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyInlineScript5X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.after(V_1_X));
        Assume.assumeTrue(version.before(V_6_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = 3");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"_retry_on_conflict\":3}}\n") + "{\"script\":{\"inline\":\"counter = 3\",\"lang\":\"groovy\"}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyInlineScript6X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.on(V_6_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = 3");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"_retry_on_conflict\":3}}\n") + "{\"script\":{\"source\":\"counter = 3\",\"lang\":\"groovy\"}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyInlineScript7X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = 3");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"retry_on_conflict\":3}}\n") + "{\"script\":{\"source\":\"counter = 3\",\"lang\":\"groovy\"}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyFileScript1X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrBefore(V_1_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_FILE, "set_count");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"_retry_on_conflict\":3}}\n") + "{\"lang\":\"groovy\",\"script_file\":\"set_count\"}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyFileScript5X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.after(V_1_X));
        Assume.assumeTrue(version.onOrBefore(V_6_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_FILE, "set_count");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"_retry_on_conflict\":3}}\n") + "{\"script\":{\"file\":\"set_count\",\"lang\":\"groovy\"}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyFileScript7X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrAfter(V_7_X));
        Settings set = settings();
        set.setProperty(ES_INDEX_AUTO_CREATE, "yes");
        set.setProperty(ES_UPDATE_RETRY_ON_CONFLICT, "3");
        set.setProperty(ES_UPDATE_SCRIPT_FILE, "set_count");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":2,\"retry_on_conflict\":3}}\n") + "{\"script\":{\"file\":\"set_count\",\"lang\":\"groovy\"}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyParamInlineScript1X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrBefore(V_1_X));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "n");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = param1; anothercounter = param2");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        set.setProperty(ES_UPDATE_SCRIPT_PARAMS, " param1:<1>,   param2:n ");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1}}\n") + "{\"params\":{\"param1\":1,\"param2\":1},\"lang\":\"groovy\",\"script\":\"counter = param1; anothercounter = param2\"}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyParamInlineScript5X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.after(V_1_X));
        Assume.assumeTrue(version.before(V_6_X));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "n");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = param1; anothercounter = param2");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        set.setProperty(ES_UPDATE_SCRIPT_PARAMS, " param1:<1>,   param2:n ");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1}}\n") + "{\"script\":{\"inline\":\"counter = param1; anothercounter = param2\",\"lang\":\"groovy\",\"params\":{\"param1\":1,\"param2\":1}}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyParamInlineScript6X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrAfter(V_6_X));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "n");
        set.setProperty(ES_UPDATE_SCRIPT_INLINE, "counter = param1; anothercounter = param2");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        set.setProperty(ES_UPDATE_SCRIPT_PARAMS, " param1:<1>,   param2:n ");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1}}\n") + "{\"script\":{\"source\":\"counter = param1; anothercounter = param2\",\"lang\":\"groovy\",\"params\":{\"param1\":1,\"param2\":1}}}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyParamFileScript1X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.onOrBefore(V_1_X));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "n");
        set.setProperty(ES_UPDATE_SCRIPT_FILE, "set_counter");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        set.setProperty(ES_UPDATE_SCRIPT_PARAMS, " param1:<1>,   param2:n ");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1}}\n") + "{\"params\":{\"param1\":1,\"param2\":1},\"lang\":\"groovy\",\"script_file\":\"set_counter\"}\n";
        Assert.assertEquals(result, ba.toString());
    }

    @Test
    public void testUpdateOnlyParamFileScript5X() throws Exception {
        Assume.assumeTrue(ES_OPERATION_UPDATE.equals(operation));
        Assume.assumeTrue(version.after(V_1_X));
        Settings set = settings();
        set.setProperty(ES_MAPPING_ID, "n");
        set.setProperty(ES_UPDATE_SCRIPT_FILE, "set_counter");
        set.setProperty(ES_UPDATE_SCRIPT_LANG, "groovy");
        set.setProperty(ES_UPDATE_SCRIPT_PARAMS, " param1:<1>,   param2:n ");
        create(set).write(data).copyTo(ba);
        String result = (("{\"" + (operation)) + "\":{\"_id\":1}}\n") + "{\"script\":{\"file\":\"set_counter\",\"lang\":\"groovy\",\"params\":{\"param1\":1,\"param2\":1}}}\n";
        Assert.assertEquals(result, ba.toString());
    }
}

