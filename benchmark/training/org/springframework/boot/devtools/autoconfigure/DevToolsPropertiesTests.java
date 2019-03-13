/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.devtools.autoconfigure;


import DevToolsProperties.Restart;
import org.junit.Test;


/**
 * Tests for {@link DevToolsProperties}.
 *
 * @author Stephane Nicoll
 */
public class DevToolsPropertiesTests {
    private final DevToolsProperties devToolsProperties = new DevToolsProperties();

    @Test
    public void additionalExcludeKeepsDefaults() {
        DevToolsProperties.Restart restart = this.devToolsProperties.getRestart();
        restart.setAdditionalExclude("foo/**,bar/**");
        assertThat(restart.getAllExclude()).containsOnly("META-INF/maven/**", "META-INF/resources/**", "resources/**", "static/**", "public/**", "templates/**", "**/*Test.class", "**/*Tests.class", "git.properties", "META-INF/build-info.properties", "foo/**", "bar/**");
    }

    @Test
    public void additionalExcludeNoDefault() {
        DevToolsProperties.Restart restart = this.devToolsProperties.getRestart();
        restart.setExclude("");
        restart.setAdditionalExclude("foo/**,bar/**");
        assertThat(restart.getAllExclude()).containsOnly("foo/**", "bar/**");
    }

    @Test
    public void additionalExcludeCustomDefault() {
        DevToolsProperties.Restart restart = this.devToolsProperties.getRestart();
        restart.setExclude("biz/**");
        restart.setAdditionalExclude("foo/**,bar/**");
        assertThat(restart.getAllExclude()).containsOnly("biz/**", "foo/**", "bar/**");
    }
}

