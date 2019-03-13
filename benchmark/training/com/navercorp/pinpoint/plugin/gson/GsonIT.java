/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.gson;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedAnnotation;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author ChaYoung You
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.google.code.gson:gson:[1.1],[1.4],[1.5],[1.6],[1.7.2],[2.0],[2.1],[2.2.4],[2.3.1,)" })
public class GsonIT {
    private static final boolean v1_2;

    private static final boolean v1_6;

    static {
        Method m = null;
        try {
            m = Gson.class.getMethod("fromJson", Reader.class, Class.class);
        } catch (NoSuchMethodException ignored) {
        }
        v1_2 = m != null;
        Class<?> c = null;
        try {
            c = Class.forName("com.google.gson.stream.JsonReader");
        } catch (ClassNotFoundException ignored) {
        }
        v1_6 = c != null;
    }

    private static final String java = "Pinpoint";

    private static final String json = new Gson().toJson(GsonIT.java);

    private static final String serviceType = "GSON";

    private static final String annotationKeyName = "gson.json.length";

    private static final ExpectedAnnotation expectedAnnotation = annotation(GsonIT.annotationKeyName, GsonIT.json.length());

    @Test
    public void test() throws Exception {
        final Gson gson = new Gson();
        /**
         *
         *
         * @see Gson#fromJson(String, Class)
         * @see Gson#fromJson(String, InterceptPoint)
         */
        gson.fromJson(GsonIT.json, String.class);
        gson.fromJson(GsonIT.json, ((Type) (String.class)));
        Method fromJson1 = Gson.class.getDeclaredMethod("fromJson", String.class, Class.class);
        Method fromJson2 = Gson.class.getDeclaredMethod("fromJson", String.class, Type.class);
        /**
         *
         *
         * @see Gson#toJson(Object)
         * @see Gson#toJson(Object, InterceptPoint)
         */
        gson.toJson(GsonIT.java);
        gson.toJson(GsonIT.java, String.class);
        Method toJson1 = Gson.class.getDeclaredMethod("toJson", Object.class);
        Method toJson2 = Gson.class.getDeclaredMethod("toJson", Object.class, Type.class);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTrace(event(GsonIT.serviceType, fromJson1, GsonIT.expectedAnnotation), event(GsonIT.serviceType, fromJson2, GsonIT.expectedAnnotation));
        verifier.verifyTrace(event(GsonIT.serviceType, toJson1, GsonIT.expectedAnnotation), event(GsonIT.serviceType, toJson2, GsonIT.expectedAnnotation));
        // No more traces
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testFromV1_2() throws Exception {
        if (!(GsonIT.v1_2)) {
            return;
        }
        final Gson gson = new Gson();
        final JsonElement jsonElement = getParseElements();
        /**
         *
         *
         * @see Gson#fromJson(Reader, Class)
         * @see Gson#fromJson(Reader, InterceptPoint)
         * @see Gson#fromJson(JsonElement, Class)
         * @see Gson#fromJson(JsonElement, InterceptPoint)
         */
        gson.fromJson(new StringReader(GsonIT.json), ((Class<?>) (String.class)));
        gson.fromJson(new StringReader(GsonIT.json), ((Type) (String.class)));
        gson.fromJson(jsonElement, String.class);
        gson.fromJson(jsonElement, ((Type) (String.class)));
        Method fromJson3 = Gson.class.getDeclaredMethod("fromJson", Reader.class, Class.class);
        Method fromJson4 = Gson.class.getDeclaredMethod("fromJson", Reader.class, Type.class);
        Method fromJson6 = Gson.class.getDeclaredMethod("fromJson", JsonElement.class, Class.class);
        Method fromJson7 = Gson.class.getDeclaredMethod("fromJson", JsonElement.class, Type.class);
        /**
         *
         *
         * @see Gson#toJson(Object, Appendable)
         * @see Gson#toJson(Object, InterceptPoint, Appendable)
         * @see Gson#toJson(JsonElement)
         * @see Gson#toJson(JsonElement, Appendable)
         */
        gson.toJson(GsonIT.java, new StringWriter());
        gson.toJson(GsonIT.java, String.class, new StringWriter());
        gson.toJson(jsonElement);
        gson.toJson(jsonElement, new StringWriter());
        Method toJson3 = Gson.class.getDeclaredMethod("toJson", Object.class, Appendable.class);
        Method toJson4 = Gson.class.getDeclaredMethod("toJson", Object.class, Type.class, Appendable.class);
        Method toJson6 = Gson.class.getDeclaredMethod("toJson", JsonElement.class);
        Method toJson7 = Gson.class.getDeclaredMethod("toJson", JsonElement.class, Appendable.class);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTrace(event(GsonIT.serviceType, fromJson3), event(GsonIT.serviceType, fromJson4), event(GsonIT.serviceType, fromJson6), event(GsonIT.serviceType, fromJson7));
        verifier.verifyTrace(event(GsonIT.serviceType, toJson3), event(GsonIT.serviceType, toJson4), event(GsonIT.serviceType, toJson6, GsonIT.expectedAnnotation), event(GsonIT.serviceType, toJson7));
        // No more traces
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testFromV1_6() throws Exception {
        if (!(GsonIT.v1_6)) {
            return;
        }
        final Gson gson = new Gson();
        final JsonElement jsonElement = getParseElements();
        /**
         *
         *
         * @see Gson#fromJson(JsonReader, InterceptPoint)
         */
        gson.fromJson(new JsonReader(new StringReader(GsonIT.json)), String.class);
        Method fromJson5 = Gson.class.getDeclaredMethod("fromJson", JsonReader.class, Type.class);
        /**
         *
         *
         * @see Gson#toJson(Object, InterceptPoint, JsonWriter)
         * @see Gson#toJson(JsonElement, JsonWriter)
         */
        gson.toJson(GsonIT.java, String.class, new JsonWriter(new StringWriter()));
        gson.toJson(jsonElement, new JsonWriter(new StringWriter()));
        Method toJson5 = Gson.class.getDeclaredMethod("toJson", Object.class, Type.class, JsonWriter.class);
        Method toJson8 = Gson.class.getDeclaredMethod("toJson", JsonElement.class, JsonWriter.class);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTrace(event(GsonIT.serviceType, fromJson5));
        verifier.verifyTrace(event(GsonIT.serviceType, toJson5), event(GsonIT.serviceType, toJson8));
        // No more traces
        verifier.verifyTraceCount(0);
    }
}

