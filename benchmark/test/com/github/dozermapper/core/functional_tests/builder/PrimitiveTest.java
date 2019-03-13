/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests.builder;


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PrimitiveTest {
    @Test
    public void shouldMapPrimitiveTypes() throws Exception {
        PrimitiveTest.Source source = new PrimitiveTest.Source();
        source.file = "a";
        source.url = "http://a";
        source.type = "java.lang.String";
        source.bigDecimal = new BigDecimal("1");
        source.myDouble = new Double("1");
        PrimitiveTest.Destination result = DozerBeanMapperBuilder.buildDefault().map(source, PrimitiveTest.Destination.class);
        MatcherAssert.assertThat(result.file, CoreMatchers.equalTo(new File("a")));
        MatcherAssert.assertThat(result.url, CoreMatchers.equalTo(new URL("http://a")));
        MatcherAssert.assertThat(result.type, CoreMatchers.sameInstance(String.class));
        MatcherAssert.assertThat(result.bigDecimal, CoreMatchers.equalTo(new Double("1")));
        MatcherAssert.assertThat(result.myDouble, CoreMatchers.equalTo(new BigDecimal("1.0")));
    }

    @Test
    public void shouldMapOneWayOnly() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(type(PrimitiveTest.Source.class), type(PrimitiveTest.Destination.class), TypeMappingOptions.oneWay());
                mapping(type(PrimitiveTest.Destination.class), type(PrimitiveTest.Source.class), TypeMappingOptions.oneWay(), TypeMappingOptions.wildcard(false));
            }
        }).build();
        {
            PrimitiveTest.Source source = new PrimitiveTest.Source();
            source.bigDecimal = new BigDecimal(1);
            PrimitiveTest.Destination result = mapper.map(source, PrimitiveTest.Destination.class);
            MatcherAssert.assertThat(result.bigDecimal, CoreMatchers.equalTo(new Double(1)));
        }
        {
            PrimitiveTest.Destination destination = new PrimitiveTest.Destination();
            destination.bigDecimal = new Double(1);
            PrimitiveTest.Source result = mapper.map(destination, PrimitiveTest.Source.class);
            MatcherAssert.assertThat(result.bigDecimal, CoreMatchers.equalTo(null));
        }
    }

    public static class Source {
        String url;

        String type;

        String file;

        BigDecimal bigDecimal;

        Double myDouble;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public void setMyDouble(Double myDouble) {
            this.myDouble = myDouble;
        }

        public String getUrl() {
            return url;
        }

        public String getType() {
            return type;
        }

        public String getFile() {
            return file;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public Double getMyDouble() {
            return myDouble;
        }
    }

    public static class Destination {
        URL url;

        Class<String> type;

        File file;

        Double bigDecimal;

        BigDecimal myDouble;

        public URL getUrl() {
            return url;
        }

        public Class<String> getType() {
            return type;
        }

        public File getFile() {
            return file;
        }

        public Double getBigDecimal() {
            return bigDecimal;
        }

        public BigDecimal getMyDouble() {
            return myDouble;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public void setType(Class<String> type) {
            this.type = type;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public void setBigDecimal(Double bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public void setMyDouble(BigDecimal myDouble) {
            this.myDouble = myDouble;
        }
    }
}

