/**
 * Copyright 2002-2016 the original author or authors.
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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;


/**
 * Test fixture for {@link PayloadArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Stephane Nicoll
 */
public class PayloadArgumentResolverTests {
    private PayloadArgumentResolver resolver;

    private MethodParameter paramAnnotated;

    private MethodParameter paramAnnotatedNotRequired;

    private MethodParameter paramAnnotatedRequired;

    private MethodParameter paramWithSpelExpression;

    private MethodParameter paramNotAnnotated;

    private MethodParameter paramValidatedNotAnnotated;

    private MethodParameter paramValidated;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void supportsParameter() throws Exception {
        Assert.assertTrue(this.resolver.supportsParameter(this.paramAnnotated));
        Assert.assertTrue(this.resolver.supportsParameter(this.paramNotAnnotated));
        PayloadArgumentResolver strictResolver = new PayloadArgumentResolver(new StringMessageConverter(), testValidator(), false);
        Assert.assertTrue(strictResolver.supportsParameter(this.paramAnnotated));
        Assert.assertFalse(strictResolver.supportsParameter(this.paramNotAnnotated));
    }

    @Test
    public void resolveRequired() throws Exception {
        Message<?> message = MessageBuilder.withPayload("ABC".getBytes()).build();
        Object actual = this.resolver.resolveArgument(paramAnnotated, message);
        Assert.assertEquals("ABC", actual);
    }

    @Test
    public void resolveRequiredEmpty() throws Exception {
        Message<?> message = MessageBuilder.withPayload("").build();
        thrown.expect(MethodArgumentNotValidException.class);// Required but empty

        this.resolver.resolveArgument(paramAnnotated, message);
    }

    @Test
    public void resolveRequiredEmptyNonAnnotatedParameter() throws Exception {
        Message<?> message = MessageBuilder.withPayload("").build();
        thrown.expect(MethodArgumentNotValidException.class);// Required but empty

        this.resolver.resolveArgument(this.paramNotAnnotated, message);
    }

    @Test
    public void resolveNotRequired() throws Exception {
        Message<?> emptyByteArrayMessage = MessageBuilder.withPayload(new byte[0]).build();
        Assert.assertNull(this.resolver.resolveArgument(this.paramAnnotatedNotRequired, emptyByteArrayMessage));
        Message<?> emptyStringMessage = MessageBuilder.withPayload("").build();
        Assert.assertNull(this.resolver.resolveArgument(this.paramAnnotatedNotRequired, emptyStringMessage));
        Message<?> notEmptyMessage = MessageBuilder.withPayload("ABC".getBytes()).build();
        Assert.assertEquals("ABC", this.resolver.resolveArgument(this.paramAnnotatedNotRequired, notEmptyMessage));
    }

    @Test
    public void resolveNonConvertibleParam() throws Exception {
        Message<?> notEmptyMessage = MessageBuilder.withPayload(123).build();
        thrown.expect(MessageConversionException.class);
        thrown.expectMessage("Cannot convert");
        this.resolver.resolveArgument(this.paramAnnotatedRequired, notEmptyMessage);
    }

    @Test
    public void resolveSpelExpressionNotSupported() throws Exception {
        Message<?> message = MessageBuilder.withPayload("ABC".getBytes()).build();
        thrown.expect(IllegalStateException.class);
        this.resolver.resolveArgument(paramWithSpelExpression, message);
    }

    @Test
    public void resolveValidation() throws Exception {
        Message<?> message = MessageBuilder.withPayload("ABC".getBytes()).build();
        this.resolver.resolveArgument(this.paramValidated, message);
    }

    @Test
    public void resolveFailValidation() throws Exception {
        // See testValidator()
        Message<?> message = MessageBuilder.withPayload("invalidValue".getBytes()).build();
        thrown.expect(MethodArgumentNotValidException.class);
        this.resolver.resolveArgument(this.paramValidated, message);
    }

    @Test
    public void resolveFailValidationNoConversionNecessary() throws Exception {
        Message<?> message = MessageBuilder.withPayload("invalidValue").build();
        thrown.expect(MethodArgumentNotValidException.class);
        this.resolver.resolveArgument(this.paramValidated, message);
    }

    @Test
    public void resolveNonAnnotatedParameter() throws Exception {
        Message<?> notEmptyMessage = MessageBuilder.withPayload("ABC".getBytes()).build();
        Assert.assertEquals("ABC", this.resolver.resolveArgument(this.paramNotAnnotated, notEmptyMessage));
        Message<?> emptyStringMessage = MessageBuilder.withPayload("").build();
        thrown.expect(MethodArgumentNotValidException.class);
        this.resolver.resolveArgument(this.paramValidated, emptyStringMessage);
    }

    @Test
    public void resolveNonAnnotatedParameterFailValidation() throws Exception {
        // See testValidator()
        Message<?> message = MessageBuilder.withPayload("invalidValue".getBytes()).build();
        thrown.expect(MethodArgumentNotValidException.class);
        thrown.expectMessage("invalid value");
        Assert.assertEquals("invalidValue", this.resolver.resolveArgument(this.paramValidatedNotAnnotated, message));
    }

    @Validated
    @Target({ ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyValid {}
}

