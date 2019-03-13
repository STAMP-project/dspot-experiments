/**
 * Copyright 2015-2017 the original author or authors.
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
package org.springframework.security.jackson2;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class SecurityJackson2ModulesTests {
    private ObjectMapper mapper;

    @Test
    public void readValueWhenNotWhitelistedOrMappedThenThrowsException() throws Exception {
        String content = "{\"@class\":\"org.springframework.security.jackson2.SecurityJackson2ModulesTests$NotWhitelisted\",\"property\":\"bar\"}";
        assertThatThrownBy(() -> {
            mapper.readValue(content, .class);
        }).hasStackTraceContaining("whitelisted");
    }

    @Test
    public void readValueWhenExplicitDefaultTypingAfterSecuritySetupThenReadsAsSpecificType() throws Exception {
        mapper.enableDefaultTyping(NON_FINAL, PROPERTY);
        String content = "{\"@class\":\"org.springframework.security.jackson2.SecurityJackson2ModulesTests$NotWhitelisted\",\"property\":\"bar\"}";
        assertThat(mapper.readValue(content, Object.class)).isInstanceOf(SecurityJackson2ModulesTests.NotWhitelisted.class);
    }

    @Test
    public void readValueWhenExplicitDefaultTypingBeforeSecuritySetupThenReadsAsSpecificType() throws Exception {
        mapper = new ObjectMapper();
        mapper.enableDefaultTyping(NON_FINAL, PROPERTY);
        SecurityJackson2Modules.enableDefaultTyping(mapper);
        String content = "{\"@class\":\"org.springframework.security.jackson2.SecurityJackson2ModulesTests$NotWhitelisted\",\"property\":\"bar\"}";
        assertThat(mapper.readValue(content, Object.class)).isInstanceOf(SecurityJackson2ModulesTests.NotWhitelisted.class);
    }

    @Test
    public void readValueWhenAnnotatedThenReadsAsSpecificType() throws Exception {
        String content = "{\"@class\":\"org.springframework.security.jackson2.SecurityJackson2ModulesTests$NotWhitelistedButAnnotated\",\"property\":\"bar\"}";
        assertThat(mapper.readValue(content, Object.class)).isInstanceOf(SecurityJackson2ModulesTests.NotWhitelistedButAnnotated.class);
    }

    @Test
    public void readValueWhenMixinProvidedThenReadsAsSpecificType() throws Exception {
        mapper.addMixIn(SecurityJackson2ModulesTests.NotWhitelisted.class, SecurityJackson2ModulesTests.NotWhitelistedMixin.class);
        String content = "{\"@class\":\"org.springframework.security.jackson2.SecurityJackson2ModulesTests$NotWhitelisted\",\"property\":\"bar\"}";
        assertThat(mapper.readValue(content, Object.class)).isInstanceOf(SecurityJackson2ModulesTests.NotWhitelisted.class);
    }

    @Test
    public void readValueWhenHashMapThenReadsAsSpecificType() throws Exception {
        mapper.addMixIn(SecurityJackson2ModulesTests.NotWhitelisted.class, SecurityJackson2ModulesTests.NotWhitelistedMixin.class);
        String content = "{\"@class\":\"java.util.HashMap\"}";
        assertThat(mapper.readValue(content, Object.class)).isInstanceOf(HashMap.class);
    }

    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface NotJacksonAnnotation {}

    @SecurityJackson2ModulesTests.NotJacksonAnnotation
    static class NotWhitelisted {
        private String property = "bar";

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
        }
    }

    @JsonIgnoreType(false)
    static class NotWhitelistedButAnnotated {
        private String property = "bar";

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
        }
    }

    @JsonTypeInfo(use = CLASS, include = PROPERTY)
    @JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    abstract class NotWhitelistedMixin {}
}

