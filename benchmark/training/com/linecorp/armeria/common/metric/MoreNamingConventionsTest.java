/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.metric;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import org.junit.Test;


public class MoreNamingConventionsTest {
    @Test
    public void configureDropwizard() {
        final MeterRegistry r = MoreNamingConventionsTest.newDropwizardRegistry();
        MoreNamingConventions.configure(r);
        assertThat(r.config().namingConvention()).isSameAs(MoreNamingConventions.dropwizard());
    }

    @Test
    public void configurePrometheus() {
        final MeterRegistry r = MoreNamingConventionsTest.newPrometheusRegistry();
        MoreNamingConventions.configure(r);
        assertThat(r.config().namingConvention()).isSameAs(MoreNamingConventions.prometheus());
    }

    @Test
    public void configureOthers() {
        // Unsupported registry's convention should not be affected.
        final MeterRegistry r = NoopMeterRegistry.get();
        final NamingConvention oldConvention = ( name, type, baseUnit) -> "foo";
        r.config().namingConvention(oldConvention);
        MoreNamingConventions.configure(r);
        assertThat(r.config().namingConvention()).isSameAs(oldConvention);
    }

    @Test
    public void configureComposite() {
        final CompositeMeterRegistry r = new CompositeMeterRegistry();
        final NamingConvention oldConvention = ( name, type, baseUnit) -> "bar";
        r.config().namingConvention(oldConvention);
        final MeterRegistry pr = MoreNamingConventionsTest.newPrometheusRegistry();
        final MeterRegistry dr = MoreNamingConventionsTest.newDropwizardRegistry();
        r.add(pr);
        r.add(dr);
        MoreNamingConventions.configure(r);
        assertThat(r.config().namingConvention()).isSameAs(oldConvention);
        assertThat(dr.config().namingConvention()).isSameAs(MoreNamingConventions.dropwizard());
        assertThat(pr.config().namingConvention()).isSameAs(MoreNamingConventions.prometheus());
    }
}

