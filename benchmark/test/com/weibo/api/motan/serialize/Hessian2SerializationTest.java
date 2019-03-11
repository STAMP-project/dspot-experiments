/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.serialize;


import java.io.IOException;
import java.sql.Timestamp;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @version ?????2013-6-3
 */
public class Hessian2SerializationTest extends TestCase {
    Hessian2Serialization hessian2Serialization = new Hessian2Serialization();

    @Test
    public void testCompatibility() throws Exception {
        SubModel.SerializationObject1 test1 = new SubModel.SerializationObject1();
        SubModel.SerializationObject1 result = hessian2Serialization.deserialize(hessian2Serialization.serialize(test1), SubModel.SerializationObject1.class);
        TestCase.assertEquals(test1.a, result.a);
        TestCase.assertEquals(test1.b, result.b);
    }

    @Test
    public void testSerialization() throws IOException {
        Model model = new Model();
        model.add("world1", new SubModel("hello1", 1));
        model.add("world2", new SubModel("hello2", 2));
        model.add("world3", new SubModel("hello3", 2));
        Model result = hessian2Serialization.deserialize(hessian2Serialization.serialize(model), Model.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(model.toString(), result.toString());
        UserAttentions user = new UserAttentions();
        user.setAddTimes(null);
        UserAttentions userResult = hessian2Serialization.deserialize(hessian2Serialization.serialize(user), UserAttentions.class);
        TestCase.assertNull(userResult.getAddTimes());
    }

    @Test
    public void testHessianBug() throws Exception {
        UserAttentions ua1 = new UserAttentions(1234L, new long[]{  }, new long[]{  }, 0, 0L);
        UserAttentions ua2 = new UserAttentions(1234L, new long[]{  }, new long[]{  }, 0);
        Hessian2Serialization s = new Hessian2Serialization();
        byte[] bytes1 = s.serialize(ua1);
        byte[] bytes2 = s.serialize(ua2);
        TestCase.assertEquals(bytes1.length, bytes2.length);
        UserAttentions result = s.deserialize(bytes1, UserAttentions.class);
        TestCase.assertEquals(ua2.getUid(), result.getUid());
    }

    @Test
    public void testHessianTimeStampBug() throws Exception {
        UserAttentions ua = new UserAttentions(1234L, new long[]{  }, new long[]{  }, 0, 0L);
        Timestamp timestamp = ua.getTimeStamp();
        Hessian2Serialization s = new Hessian2Serialization();
        byte[] b = s.serialize(ua);
        UserAttentions result = s.deserialize(b, UserAttentions.class);
        TestCase.assertEquals(result.getTimeStamp().getTime(), timestamp.getTime());
        ua.setTimeStamp(null);
        b = s.serialize(ua);
        result = s.deserialize(b, UserAttentions.class);
        TestCase.assertNull(result.getTimeStamp());
    }

    @Test
    public void testHessianDeserializeException() throws Exception {
        UserAttentions user = new UserAttentions();
        user.setAddTimes(null);
        try {
            hessian2Serialization.deserialize(hessian2Serialization.serialize(user), UnModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSerializeMulti() throws Exception {
        Object[] objects = new Object[]{ "123", 456, true, 45.67F };
        byte[] bytes = hessian2Serialization.serializeMulti(objects);
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < (objects.length); i++) {
            classes[i] = objects[i].getClass();
        }
        Object[] newObjs = hessian2Serialization.deserializeMulti(bytes, classes);
        for (int i = 0; i < (objects.length); i++) {
            TestCase.assertEquals(objects[i], newObjs[i]);
        }
    }
}

