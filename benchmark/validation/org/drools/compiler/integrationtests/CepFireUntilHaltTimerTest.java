/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;


/**
 * Tests proper timer firing using accumulate and fireUntilHalt() mode.
 * BZ-981270
 */
@Ignore
public class CepFireUntilHaltTimerTest {
    private KieSession ksession;

    private List<Long> result;

    private SessionPseudoClock clock;

    @Test
    public void testTwoRunsTimerAccumulateFireUntilHalt() throws Exception {
        init();
        performTest();
        cleanup();
        init();
        performTest();
        cleanup();
    }

    public static class MetadataEvent implements Serializable {
        private static final long serialVersionUID = 6827172457832354239L;

        private Date metadataTimestamp;

        private Long metadataDuration;

        private String name;

        public MetadataEvent() {
        }

        public MetadataEvent(final Date timestamp, final Long duration) {
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public MetadataEvent(final String name, final Date timestamp, final Long duration) {
            this.name = name;
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public Date getMetadataTimestamp() {
            return (metadataTimestamp) != null ? ((Date) (metadataTimestamp.clone())) : null;
        }

        public void setMetadataTimestamp(final Date metadataTimestamp) {
            this.metadataTimestamp = (metadataTimestamp != null) ? ((Date) (metadataTimestamp.clone())) : null;
        }

        public Long getMetadataDuration() {
            return metadataDuration;
        }

        public void setMetadataDuration(final Long metadataDuration) {
            this.metadataDuration = metadataDuration;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("MetadataEvent[name='%s' timestamp='%s', duration='%s']", name, metadataTimestamp, metadataDuration);
        }
    }
}

