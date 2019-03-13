/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.context.support;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 *
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 */
public class Spr7816Tests {
    @Test
    public void spr7816() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spr7816.xml", getClass());
        Spr7816Tests.FilterAdapter adapter = ctx.getBean(Spr7816Tests.FilterAdapter.class);
        Assert.assertEquals(Spr7816Tests.Building.class, adapter.getSupportedTypes().get("Building"));
        Assert.assertEquals(Spr7816Tests.Entrance.class, adapter.getSupportedTypes().get("Entrance"));
        Assert.assertEquals(Spr7816Tests.Dwelling.class, adapter.getSupportedTypes().get("Dwelling"));
    }

    public static class FilterAdapter {
        private String extensionPrefix;

        private Map<String, Class<? extends Spr7816Tests.DomainEntity>> supportedTypes;

        public FilterAdapter(final String extensionPrefix, final Map<String, Class<? extends Spr7816Tests.DomainEntity>> supportedTypes) {
            this.extensionPrefix = extensionPrefix;
            this.supportedTypes = supportedTypes;
        }

        public String getExtensionPrefix() {
            return extensionPrefix;
        }

        public Map<String, Class<? extends Spr7816Tests.DomainEntity>> getSupportedTypes() {
            return supportedTypes;
        }
    }

    public static class Building extends Spr7816Tests.DomainEntity {}

    public static class Entrance extends Spr7816Tests.DomainEntity {}

    public static class Dwelling extends Spr7816Tests.DomainEntity {}

    public abstract static class DomainEntity {}
}

