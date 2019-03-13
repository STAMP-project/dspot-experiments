/**
 * Copyright 2019 http://www.hswebframework.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hswebframework.web.dictionary.starter;


import DataStatus.STATUS_ENABLED;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import org.hswebframework.web.dictionary.api.entity.DictionaryEntity;
import org.hswebframework.web.dictionary.api.entity.DictionaryItemEntity;
import org.hswebframework.web.starter.convert.FastJsonGenericHttpMessageConverter;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * TODO ??????
 *
 * @author hsweb-generator-online
 */
public class DictionaryTests extends SimpleWebApplicationTests {
    @Autowired
    private FastJsonGenericHttpMessageConverter fastJsonHttpMessageConverter;

    @Test
    public void testCrud() throws Exception {
        DictionaryEntity entity = entityFactory.newInstance(DictionaryEntity.class);
        // todo ??????
        entity.setName("test");
        entity.setCreatorId("admin");
        entity.setCreateTime(System.currentTimeMillis());
        entity.setId("test");
        entity.setDescribe("test");
        entity.setClassifiedId("test");
        entity.setStatus(STATUS_ENABLED);
        String json = "[" + (((((((((((((("{'value':'1','text':'??','children':" + "[") + "{'value':'101','text':'??',") + "'children':[") + "{'value':'10102','text':'???'}") + ",{'value':'10103','text':'???'}") + // ?????????
        ",{'value':'10105','text':'????'") + ",'textExpression':'${#value}[${#context[otherApple]}]'") + ",\'valueExpression\':\'${(#context.put(\\\'otherApple\\\',#pattern.split(\"[ \\\\[ \\\\]]\")[1])==null)?#value:#value}\'") + "}") + "]}") + ",{'value':'102','text':'??'}]") + "}") + ",{'value':'2','text':'??'}") + "]");
        List<DictionaryItemEntity> itemEntities = JSON.parseArray(json, DictionaryItemEntity.class);
        entity.setItems(itemEntities);
        // test add data
        String requestBody = JSON.toJSONString(entity);
        JSONObject result = testPost("/dictionary").setUp(( setup) -> setup.contentType(MediaType.APPLICATION_JSON).content(requestBody)).exec().resultAsJson();
        Assert.assertEquals(200, result.get("status"));
        String id = result.getString("result");
        Assert.assertNotNull(id);
        entity.setId(id);
        // test get data
        result = testGet(("/dictionary/" + id)).exec().resultAsJson();
        entity = result.getObject("result", entityFactory.getInstanceType(DictionaryEntity.class));
        Assert.assertEquals(200, result.get("status"));
        Assert.assertNotNull(result.getJSONObject("result"));
        Assert.assertEquals(fastJsonHttpMessageConverter.converter(entity), fastJsonHttpMessageConverter.converter(result.getObject("result", entityFactory.getInstanceType(DictionaryEntity.class))));
        // todo ??????
        DictionaryEntity newEntity = entityFactory.newInstance(DictionaryEntity.class);
        newEntity.setName("test");
        result = testPut(("/dictionary/" + id)).setUp(( setup) -> setup.contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(newEntity))).exec().resultAsJson();
        Assert.assertEquals(200, result.get("status"));
        result = testGet(("/dictionary/" + id)).exec().resultAsJson();
        result = result.getJSONObject("result");
        Assert.assertNotNull(result);
        result = testDelete(("/dictionary/" + id)).exec().resultAsJson();
        Assert.assertEquals(200, result.get("status"));
        result = testGet(("/dictionary/" + id)).exec().resultAsJson();
        Assert.assertEquals(404, result.get("status"));
    }
}

