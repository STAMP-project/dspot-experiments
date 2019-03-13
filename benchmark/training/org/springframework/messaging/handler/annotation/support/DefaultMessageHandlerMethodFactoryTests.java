/**
 * Copyright 2002-2018 the original author or authors.
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class DefaultMessageHandlerMethodFactoryTests {
    private final DefaultMessageHandlerMethodFactoryTests.SampleBean sample = new DefaultMessageHandlerMethodFactoryTests.SampleBean();

    @Rule
    public final TestName name = new TestName();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void customConversion() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(DefaultMessageHandlerMethodFactoryTests.SampleBean.class, String.class, new org.springframework.core.convert.converter.Converter<DefaultMessageHandlerMethodFactoryTests.SampleBean, String>() {
            @Override
            public String convert(DefaultMessageHandlerMethodFactoryTests.SampleBean source) {
                return "foo bar";
            }
        });
        instance.setConversionService(conversionService);
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "simpleString", String.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload(sample).build());
        assertMethodInvocation(sample, "simpleString");
    }

    @Test
    public void customConversionServiceFailure() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        GenericConversionService conversionService = new GenericConversionService();
        Assert.assertFalse("conversion service should fail to convert payload", conversionService.canConvert(Integer.class, String.class));
        instance.setConversionService(conversionService);
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "simpleString", String.class);
        thrown.expect(MessageConversionException.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload(123).build());
    }

    @Test
    public void customMessageConverterFailure() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        MessageConverter messageConverter = new ByteArrayMessageConverter();
        instance.setMessageConverter(messageConverter);
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "simpleString", String.class);
        thrown.expect(MessageConversionException.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload(123).build());
    }

    @Test
    public void customArgumentResolver() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        List<HandlerMethodArgumentResolver> customResolvers = new ArrayList<>();
        customResolvers.add(new DefaultMessageHandlerMethodFactoryTests.CustomHandlerMethodArgumentResolver());
        instance.setCustomArgumentResolvers(customResolvers);
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "customArgumentResolver", Locale.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload(123).build());
        assertMethodInvocation(sample, "customArgumentResolver");
    }

    @Test
    public void overrideArgumentResolvers() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        List<HandlerMethodArgumentResolver> customResolvers = new ArrayList<>();
        customResolvers.add(new DefaultMessageHandlerMethodFactoryTests.CustomHandlerMethodArgumentResolver());
        instance.setArgumentResolvers(customResolvers);
        instance.afterPropertiesSet();
        Message<String> message = MessageBuilder.withPayload("sample").build();
        // This will work as the local resolver is set
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "customArgumentResolver", Locale.class);
        invocableHandlerMethod.invoke(message);
        assertMethodInvocation(sample, "customArgumentResolver");
        // This won't work as no resolver is known for the payload
        InvocableHandlerMethod invocableHandlerMethod2 = createInvocableHandlerMethod(instance, "simpleString", String.class);
        thrown.expect(MethodArgumentResolutionException.class);
        thrown.expectMessage("No suitable resolver");
        invocableHandlerMethod2.invoke(message);
    }

    @Test
    public void noValidationByDefault() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "payloadValidation", String.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload("failure").build());
        assertMethodInvocation(sample, "payloadValidation");
    }

    @Test
    public void customValidation() throws Exception {
        DefaultMessageHandlerMethodFactory instance = createInstance();
        instance.setValidator(new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return String.class.isAssignableFrom(clazz);
            }

            @Override
            public void validate(Object target, Errors errors) {
                String value = ((String) (target));
                if ("failure".equals(value)) {
                    errors.reject("not a valid value");
                }
            }
        });
        instance.afterPropertiesSet();
        InvocableHandlerMethod invocableHandlerMethod = createInvocableHandlerMethod(instance, "payloadValidation", String.class);
        thrown.expect(MethodArgumentNotValidException.class);
        invocableHandlerMethod.invoke(MessageBuilder.withPayload("failure").build());
    }

    static class SampleBean {
        private final Map<String, Boolean> invocations = new HashMap<>();

        public void simpleString(String value) {
            invocations.put("simpleString", true);
        }

        public void payloadValidation(@Payload
        @Validated
        String value) {
            invocations.put("payloadValidation", true);
        }

        public void customArgumentResolver(Locale locale) {
            invocations.put("customArgumentResolver", true);
            Assert.assertEquals("Wrong value for locale", Locale.getDefault(), locale);
        }
    }

    static class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(Locale.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
            return Locale.getDefault();
        }
    }
}

