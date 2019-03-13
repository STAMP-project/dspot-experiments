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
package org.springframework.boot.context.properties.bind.test;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;


/**
 * Tests for {@link Binder} using package private Java beans.
 *
 * @author Madhura Bhave
 */
public class PackagePrivateBeanBindingTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    private ConfigurationPropertyName name;

    @Test
    public void bindToPackagePrivateClassShouldBindToInstance() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "999");
        this.sources.add(source);
        PackagePrivateBeanBindingTests.ExamplePackagePrivateBean bean = this.binder.bind(this.name, Bindable.of(PackagePrivateBeanBindingTests.ExamplePackagePrivateBean.class)).get();
        assertThat(bean.getBar()).isEqualTo(999);
    }

    static class ExamplePackagePrivateBean {
        private int bar;

        public int getBar() {
            return this.bar;
        }

        public void setBar(int bar) {
            this.bar = bar;
        }
    }
}

