/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.template;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for application/json render.
 */
public class TemplateEngineJsonTest {
    Context context;

    ResponseStreams responseStreams;

    Result result;

    ObjectMapper objectMapper;

    ByteArrayOutputStream outputStream;

    @Test
    public void testJsonViewWorks() throws IOException {
        Mockito.<Class<?>>when(result.getJsonView()).thenReturn(TemplateEngineJsonTest.View.Public.class);
        TemplateEngineJson jsonEngine = new TemplateEngineJson(objectMapper);
        jsonEngine.invoke(context, result);
        String json = new String(outputStream.toByteArray(), "UTF-8");
        Assert.assertTrue(json.contains("field_one"));
        Assert.assertFalse(json.contains("field_two"));
        Mockito.verify(context).finalizeHeaders(result);
    }

    private static class TestObject {
        @JsonView(TemplateEngineJsonTest.View.Public.class)
        public String field1;

        @JsonView(TemplateEngineJsonTest.View.Private.class)
        public String field2;
    }

    private static class View {
        public static class Public {}

        public static class Private {}
    }
}

