/**
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.messaging.handler.annotation.support;


import DestinationVariableMethodArgumentResolver.DESTINATION_TEMPLATE_VARIABLES_HEADER;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessagingPredicates;
import org.springframework.messaging.handler.invocation.ResolvableMethod;
import org.springframework.messaging.support.MessageBuilder;


/**
 * Test fixture for {@link DestinationVariableMethodArgumentResolver} tests.
 *
 * @author Brian Clozel
 */
public class DestinationVariableMethodArgumentResolverTests {
    private final DestinationVariableMethodArgumentResolver resolver = new DestinationVariableMethodArgumentResolver(new DefaultConversionService());

    private final ResolvableMethod resolvable = ResolvableMethod.on(getClass()).named("handleMessage").build();

    @Test
    public void supportsParameter() {
        Assert.assertTrue(resolver.supportsParameter(this.resolvable.annot(MessagingPredicates.destinationVar().noValue()).arg()));
        Assert.assertFalse(resolver.supportsParameter(this.resolvable.annotNotPresent(DestinationVariable.class).arg()));
    }

    @Test
    public void resolveArgument() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("foo", "bar");
        vars.put("name", "value");
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeader(DESTINATION_TEMPLATE_VARIABLES_HEADER, vars).build();
        MethodParameter param = this.resolvable.annot(MessagingPredicates.destinationVar().noValue()).arg();
        Object result = this.resolver.resolveArgument(param, message);
        Assert.assertEquals("bar", result);
        param = this.resolvable.annot(MessagingPredicates.destinationVar("name")).arg();
        result = this.resolver.resolveArgument(param, message);
        Assert.assertEquals("value", result);
    }

    @Test(expected = MessageHandlingException.class)
    public void resolveArgumentNotFound() throws Exception {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
        this.resolver.resolveArgument(this.resolvable.annot(MessagingPredicates.destinationVar().noValue()).arg(), message);
    }
}

