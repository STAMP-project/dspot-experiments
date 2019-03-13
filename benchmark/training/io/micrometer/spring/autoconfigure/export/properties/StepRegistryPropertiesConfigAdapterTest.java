/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure.export.properties;


import java.time.Duration;
import org.junit.Test;


/**
 * Base test for {@link StepRegistryPropertiesConfigAdapter} implementations.
 *
 * @param <P>
 * 		properties used by the tests
 * @param <A>
 * 		adapter used by the tests
 * @author Stephane Nicoll
 */
public abstract class StepRegistryPropertiesConfigAdapterTest<P extends StepRegistryProperties, A extends StepRegistryPropertiesConfigAdapter<P>> {
    @Test
    public void whenPropertiesStepIsSetAdapterStepReturnsIt() {
        P properties = createProperties();
        setStep(Duration.ofSeconds(42));
        assertThat(step()).isEqualTo(Duration.ofSeconds(42));
    }

    @Test
    public void whenPropertiesEnabledIsSetAdapterEnabledReturnsIt() {
        P properties = createProperties();
        setEnabled(false);
        assertThat(enabled()).isFalse();
    }

    @Test
    public void whenPropertiesConnectTimeoutIsSetAdapterConnectTimeoutReturnsIt() {
        P properties = createProperties();
        setConnectTimeout(Duration.ofMinutes(42));
        assertThat(connectTimeout()).isEqualTo(Duration.ofMinutes(42));
    }

    @Test
    public void whenPropertiesReadTimeoutIsSetAdapterReadTimeoutReturnsIt() {
        P properties = createProperties();
        setReadTimeout(Duration.ofMillis(42));
        assertThat(readTimeout()).isEqualTo(Duration.ofMillis(42));
    }

    @Test
    public void whenPropertiesNumThreadsIsSetAdapterNumThreadsReturnsIt() {
        P properties = createProperties();
        setNumThreads(42);
        assertThat(numThreads()).isEqualTo(42);
    }

    @Test
    public void whenPropertiesBatchSizeIsSetAdapterBatchSizeReturnsIt() {
        P properties = createProperties();
        setBatchSize(10042);
        assertThat(batchSize()).isEqualTo(10042);
    }
}

