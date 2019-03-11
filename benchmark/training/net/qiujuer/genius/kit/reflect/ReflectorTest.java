/**
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.kit.reflect;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * The {@link Reflector} test
 */
public class ReflectorTest {
    private static class UserBean {}

    private static class ResultBean<M> {}

    private abstract static class BaseGeneric<T, V> {}

    private abstract static class ChildOne<U, T> extends ReflectorTest.BaseGeneric<T, U> {}

    private abstract static class ChildTwo<M> extends ReflectorTest.ChildOne<ReflectorTest.UserBean, ReflectorTest.ResultBean<M>> {}

    @Test
    public void getActualTypeArgumentsStatic() throws Exception {
        ReflectorTest.ChildTwo childTwo = new ReflectorTest.ChildTwo<ReflectorTest.UserBean>() {};
        Type[] types = Reflector.getActualTypeArguments(ReflectorTest.BaseGeneric.class, childTwo.getClass());
        Assert.assertEquals(true, ((types.length) == 2));
        Assert.assertEquals(true, ((types[0]) instanceof ParameterizeTypeActualArgsDelegate));
        ParameterizedType delegate = ((ParameterizedType) (types[0]));
        Assert.assertEquals(ReflectorTest.ResultBean.class, delegate.getRawType());
        Assert.assertEquals(true, ((delegate.getActualTypeArguments().length) == 1));
        Assert.assertEquals(ReflectorTest.UserBean.class, delegate.getActualTypeArguments()[0]);
        Assert.assertEquals(ReflectorTest.UserBean.class, types[1]);
        Logger.getLogger("ReflectorTest").log(Level.INFO, Arrays.toString(types));
    }
}

