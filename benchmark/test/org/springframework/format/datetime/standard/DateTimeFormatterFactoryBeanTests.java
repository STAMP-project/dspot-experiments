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
package org.springframework.format.datetime.standard;


import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class DateTimeFormatterFactoryBeanTests {
    private final DateTimeFormatterFactoryBean factory = new DateTimeFormatterFactoryBean();

    @Test
    public void isSingleton() {
        Assert.assertThat(factory.isSingleton(), is(true));
    }

    @Test
    public void getObjectType() {
        Assert.assertThat(factory.getObjectType(), is(equalTo(DateTimeFormatter.class)));
    }

    @Test
    public void getObject() {
        factory.afterPropertiesSet();
        Assert.assertThat(factory.getObject().toString(), is(equalTo(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toString())));
    }

    @Test
    public void getObjectIsAlwaysSingleton() {
        factory.afterPropertiesSet();
        DateTimeFormatter formatter = factory.getObject();
        Assert.assertThat(formatter.toString(), is(equalTo(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toString())));
        factory.setStylePattern("LL");
        Assert.assertThat(factory.getObject(), is(sameInstance(formatter)));
    }
}

