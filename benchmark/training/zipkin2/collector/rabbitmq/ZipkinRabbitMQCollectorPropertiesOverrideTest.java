/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.collector.rabbitmq;


import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zipkin2.autoconfigure.collector.rabbitmq.Access;


@RunWith(Parameterized.class)
public class ZipkinRabbitMQCollectorPropertiesOverrideTest {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Parameterized.Parameter(0)
    public String property;

    @Parameterized.Parameter(1)
    public Object value;

    @Parameterized.Parameter(2)
    public Function<RabbitMQCollector.Builder, Object> builderExtractor;

    @Test
    public void propertyTransferredToCollectorBuilder() throws Exception {
        TestPropertyValues.of((((property) + ":") + (value))).applyTo(context);
        Access.registerRabbitMQProperties(context);
        context.refresh();
        assertThat(Access.collectorBuilder(context)).extracting(builderExtractor).isEqualTo(value);
    }
}

