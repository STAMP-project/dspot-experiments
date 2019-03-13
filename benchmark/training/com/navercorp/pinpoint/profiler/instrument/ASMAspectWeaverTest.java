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
package com.navercorp.pinpoint.profiler.instrument;


import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class ASMAspectWeaverTest {
    private final String ORIGINAL = "com.navercorp.pinpoint.profiler.instrument.mock.AspectOriginalClass";

    private final String ORIGINAL_SUB = "com.navercorp.pinpoint.profiler.instrument.mock.AspectOriginalSubClass";

    private final String ASPECT = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorClass";

    private final String ASPECT_NO_EXTENTS = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorNoExtendClass";

    private final String ASPECT_EXTENTS_SUB = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorExtendSubClass";

    private final String ERROR_ASPECT1 = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorErrorClass";

    private final String ERROR_ASPECT2 = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorError2Class";

    private final String ERROR_ASPECT_INVALID_EXTENTS = "com.navercorp.pinpoint.profiler.instrument.mock.AspectInterceptorInvalidExtendClass";

    private final InstrumentContext pluginContext = Mockito.mock(InstrumentContext.class);

    @Test
    public void weaving() throws Exception {
        weaving(ORIGINAL, ASPECT);
        weaving(ORIGINAL, ASPECT_NO_EXTENTS);
        weaving(ORIGINAL, ASPECT_EXTENTS_SUB);
        weaving(ORIGINAL_SUB, ASPECT_EXTENTS_SUB);
        weaving(ORIGINAL_SUB, ASPECT_NO_EXTENTS);
    }

    @Test(expected = Exception.class)
    public void invalidHierarchy() throws Exception {
        weaving(ORIGINAL_SUB, ASPECT);
    }

    @Test(expected = Exception.class)
    public void signatureMiss() throws Exception {
        // not found method
        getInstnace(ORIGINAL, ERROR_ASPECT1);
    }

    @Test(expected = Exception.class)
    public void internalTypeMiss() throws Exception {
        getInstnace(ORIGINAL, ERROR_ASPECT2);
    }

    @Test(expected = Exception.class)
    public void invalidExtend() throws Exception {
        // invalid class hierarchy.
        getInstnace(ORIGINAL, ERROR_ASPECT_INVALID_EXTENTS);
    }
}

