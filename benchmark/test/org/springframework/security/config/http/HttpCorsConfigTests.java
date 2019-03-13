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
package org.springframework.security.config.http;


import RequestMethod.GET;
import RequestMethod.POST;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 *
 *
 * @author Rob Winch
 * @author Tim Ysewyn
 * @author Josh Cummings
 */
public class HttpCorsConfigTests {
    private static final String CONFIG_LOCATION_PREFIX = "classpath:org/springframework/security/config/http/HttpCorsConfigTests";

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Test
    public void autowireWhenMissingMvcThenGivesInformativeError() {
        assertThatThrownBy(() -> this.spring.configLocations(this.xml("RequiresMvc")).autowire()).isInstanceOf(BeanCreationException.class).hasMessageContaining("Please ensure Spring Security & Spring MVC are configured in a shared ApplicationContext");
    }

    @Test
    public void getWhenUsingCorsThenDoesSpringSecurityCorsHandshake() throws Exception {
        this.spring.configLocations(this.xml("WithCors")).autowire();
        this.mvc.perform(get("/").with(this.approved())).andExpect(corsResponseHeaders()).andExpect(status().isIAmATeapot());
        this.mvc.perform(options("/").with(this.preflight())).andExpect(corsResponseHeaders()).andExpect(status().isOk());
    }

    @Test
    public void getWhenUsingCustomCorsConfigurationSourceThenDoesSpringSecurityCorsHandshake() throws Exception {
        this.spring.configLocations(this.xml("WithCorsConfigurationSource")).autowire();
        this.mvc.perform(get("/").with(this.approved())).andExpect(corsResponseHeaders()).andExpect(status().isIAmATeapot());
        this.mvc.perform(options("/").with(this.preflight())).andExpect(corsResponseHeaders()).andExpect(status().isOk());
    }

    @Test
    public void getWhenUsingCustomCorsFilterThenDoesSPringSecurityCorsHandshake() throws Exception {
        this.spring.configLocations(this.xml("WithCorsFilter")).autowire();
        this.mvc.perform(get("/").with(this.approved())).andExpect(corsResponseHeaders()).andExpect(status().isIAmATeapot());
        this.mvc.perform(options("/").with(this.preflight())).andExpect(corsResponseHeaders()).andExpect(status().isOk());
    }

    @RestController
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST })
    static class CorsController {
        @RequestMapping("/")
        String hello() {
            return "Hello";
        }
    }

    static class MyCorsConfigurationSource extends UrlBasedCorsConfigurationSource {
        MyCorsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList(GET.name(), POST.name()));
            super.registerCorsConfiguration("/**", configuration);
        }
    }
}

