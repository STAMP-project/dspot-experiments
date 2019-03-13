/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.javanica.util.bridge;


import com.netflix.hystrix.contrib.javanica.utils.MethodProvider;
import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dmgcodevil
 */
public class UnbridgeMethodTest {
    @Test
    public void testUnbridgeFoo() throws IOException, ClassNotFoundException, NoSuchMethodException {
        // given
        Method bridgeMethod = UnbridgeMethodTest.getBridgeMethod(GenericInterfaceImpl.class, "foo");
        Assert.assertNotNull(bridgeMethod);
        // when
        Method genMethod = MethodProvider.getInstance().unbride(bridgeMethod, GenericInterfaceImpl.class);
        // then
        Assert.assertNotNull(bridgeMethod);
        UnbridgeMethodTest.assertReturnType(Child.class, genMethod);
        UnbridgeMethodTest.assertParamsTypes(genMethod, Child.class);
    }
}

