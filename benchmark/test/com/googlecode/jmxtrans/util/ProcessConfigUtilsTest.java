/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.util;


import com.google.common.base.Predicate;
import com.google.common.io.Closer;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.test.RequiresIO;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.annotation.Nullable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(RequiresIO.class)
public class ProcessConfigUtilsTest {
    private ProcessConfigUtils processConfigUtils;

    private Closer closer = Closer.create();

    @Test
    public void loadingFromSimpleJsonFile() throws IOException, URISyntaxException, MalformedObjectNameException {
        loadFromFile("example.json");
    }

    @Test
    public void loadingFromSimpleYamlFile() throws IOException, URISyntaxException, MalformedObjectNameException {
        loadFromFile("example.yaml");
    }

    @Test
    public void loadingFromJsonFileWithVariables() throws Exception {
        loadFromFile("exampleWithVariables.json");
    }

    @Test
    public void loadingFromYamlFileWithVariables() throws Exception {
        loadFromFile("exampleWithVariables.yaml");
    }

    private static class ByObj implements Predicate<Query> {
        private final ObjectName obj;

        private ByObj(String obj) throws MalformedObjectNameException {
            this.obj = new ObjectName(obj);
        }

        @Override
        public boolean apply(@Nullable
        Query query) {
            return query.getObjectName().equals(this.obj);
        }
    }
}

