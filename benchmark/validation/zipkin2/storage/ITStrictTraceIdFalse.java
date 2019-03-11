/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage;


import java.io.IOException;
import java.util.List;
import org.junit.Test;
import zipkin2.Span;


/**
 * Base test for when {@link StorageComponent.Builder#strictTraceId(boolean) strictTraceId ==
 * false}.
 *
 * <p>Subtypes should create a connection to a real backend, even if that backend is in-process.
 *
 * <p>This is a replacement for {@code zipkin.storage.StrictTraceIdFalseTest}.
 */
public abstract class ITStrictTraceIdFalse {
    /**
     * Ensures we can still lookup fully 128-bit traces when strict trace ID id disabled
     */
    @Test
    public void getTraces_128BitTraceId() throws IOException {
        getTraces_128BitTraceId(accept128BitTrace(storage()));
    }

    @Test
    public void getTraces_128BitTraceId_mixed() throws IOException {
        getTraces_128BitTraceId(acceptMixedTrace());
    }

    @Test
    public void getTrace_retrievesBy64Or128BitTraceId() throws IOException {
        List<Span> trace = accept128BitTrace(storage());
        retrievesBy64Or128BitTraceId(trace);
    }

    @Test
    public void getTrace_retrievesBy64Or128BitTraceId_mixed() throws IOException {
        List<Span> trace = acceptMixedTrace();
        retrievesBy64Or128BitTraceId(trace);
    }
}

