/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.builders;


import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.api.Greeting;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


class ServiceBuilderTest {
    @Test
    public void testUniqueServiceName() throws Exception {
        ServiceBuilder<Greeting> builder = new ServiceBuilder();
        builder.group("dubbo").interfaceClass(Greeting.class).version("1.0.0");
        ServiceConfig<Greeting> service = builder.build();
        MatcherAssert.assertThat(service.getUniqueServiceName(), CoreMatchers.equalTo((("dubbo/" + (Greeting.class.getName())) + ":1.0.0")));
    }

    @Test
    public void generic() throws Exception {
        ServiceBuilder builder = new ServiceBuilder();
        builder.generic(GENERIC_SERIALIZATION_DEFAULT);
        MatcherAssert.assertThat(builder.build().getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_DEFAULT));
        builder.generic(GENERIC_SERIALIZATION_NATIVE_JAVA);
        MatcherAssert.assertThat(builder.build().getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_NATIVE_JAVA));
        builder.generic(GENERIC_SERIALIZATION_BEAN);
        MatcherAssert.assertThat(builder.build().getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_BEAN));
    }

    @Test
    public void generic1() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceBuilder builder = new ServiceBuilder();
            builder.generic("illegal").build();
        });
    }

    @Test
    public void Mock() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceBuilder builder = new ServiceBuilder();
            builder.mock("true");
        });
    }

    @Test
    public void Mock1() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceBuilder builder = new ServiceBuilder();
            builder.mock(true);
        });
    }
}

