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


import Scannable.Attr.RUN_ON;
import java.time.Duration;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


public class MonoElapsedTest {
    @Test
    public void aFluxCanBeBenchmarked() {
        StepVerifier.withVirtualTime(this::scenario_aFluxCanBeBenchmarked, 0).thenAwait(Duration.ofSeconds(2)).thenRequest(1).expectNextMatches(( t) -> ((t.getT1()) == 2000) && (t.getT2().equals("test"))).verifyComplete();
    }

    @Test
    public void scanOperator() {
        MonoElapsed<String> test = new MonoElapsed(Mono.empty(), Schedulers.immediate());
        assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.immediate());
    }
}

