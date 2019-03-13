/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.ACTUAL_METADATA;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Traces.callSiteSupplierFactory;
import org.junit.Test;
import reactor.core.publisher.FluxOnAssembly.AssemblySnapshot;


public class ConnectableFluxOnAssemblyTest {
    @Test
    public void scanMain() throws Exception {
        ConnectableFlux<String> source = Flux.just("foo").publish();
        AssemblySnapshot stacktrace = new AssemblySnapshot(null, callSiteSupplierFactory.get());
        ConnectableFluxOnAssembly<String> test = new ConnectableFluxOnAssembly(source, stacktrace);
        assertThat(test.scan(PREFETCH)).isEqualTo((-1));
        assertThat(test.scan(PARENT)).isSameAs(source);
    }

    @Test
    public void scanOperator() {
        ConnectableFlux<String> source = Flux.just("foo").publish();
        AssemblySnapshot stacktrace = new AssemblySnapshot(null, callSiteSupplierFactory.get());
        ConnectableFluxOnAssembly<String> test = new ConnectableFluxOnAssembly(source, stacktrace);
        assertThat(test.scan(ACTUAL_METADATA)).as("ACTUAL_METADATA").isTrue();
        assertThat(test.scan(PREFETCH)).as("PREFETCH").isEqualTo((-1));
        assertThat(test.scan(PARENT)).as("PARENT").isSameAs(source);
    }

    @Test
    public void stepNameAndToString() {
        AssemblySnapshot stacktrace = new AssemblySnapshot(null, callSiteSupplierFactory.get());
        ConnectableFluxOnAssembly<?> test = new ConnectableFluxOnAssembly(Flux.empty().publish(), stacktrace);
        assertThat(test.toString()).isEqualTo(test.stepName()).startsWith("reactor.core.publisher.ConnectableFluxOnAssemblyTest.stepNameAndToString(ConnectableFluxOnAssemblyTest.java:");
    }
}

