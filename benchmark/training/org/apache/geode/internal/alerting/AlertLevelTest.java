/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.alerting;


import LogWriterLevel.ALL;
import LogWriterLevel.CONFIG;
import LogWriterLevel.ERROR;
import LogWriterLevel.FINE;
import LogWriterLevel.FINER;
import LogWriterLevel.FINEST;
import LogWriterLevel.INFO;
import LogWriterLevel.NONE;
import LogWriterLevel.SEVERE;
import LogWriterLevel.WARNING;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.geode.test.junit.categories.AlertingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link AlertLevel}.
 */
@Category(AlertingTest.class)
public class AlertLevelTest {
    @Test
    public void isSerializable() {
        assertThat(AlertLevel.NONE).isInstanceOf(Serializable.class);
    }

    @Test
    public void serializes() {
        AlertLevel logLevel = ((AlertLevel) (SerializationUtils.clone(AlertLevel.NONE)));
        assertThat(logLevel).isEqualTo(AlertLevel.NONE).isSameAs(AlertLevel.NONE);
    }

    @Test
    public void findWARNING() {
        assertThat(AlertLevel.find(WARNING.intLevel())).isEqualTo(AlertLevel.WARNING);
    }

    @Test
    public void findERROR() {
        assertThat(AlertLevel.find(ERROR.intLevel())).isEqualTo(AlertLevel.ERROR);
    }

    @Test
    public void findSEVERE() {
        assertThat(AlertLevel.find(SEVERE.intLevel())).isEqualTo(AlertLevel.SEVERE);
    }

    @Test
    public void findNONE() {
        assertThat(AlertLevel.find(NONE.intLevel())).isEqualTo(AlertLevel.NONE);
    }

    @Test
    public void findINFO() {
        assertThatThrownBy(() -> find(LogWriterLevel.INFO.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (INFO.intLevel())));
    }

    @Test
    public void findCONFIG() {
        assertThatThrownBy(() -> find(LogWriterLevel.CONFIG.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (CONFIG.intLevel())));
    }

    @Test
    public void findFINE() {
        assertThatThrownBy(() -> find(LogWriterLevel.FINE.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (FINE.intLevel())));
    }

    @Test
    public void findFINER() {
        assertThatThrownBy(() -> find(LogWriterLevel.FINER.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (FINER.intLevel())));
    }

    @Test
    public void findFINEST() {
        assertThatThrownBy(() -> find(LogWriterLevel.FINEST.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (FINEST.intLevel())));
    }

    @Test
    public void findALL() {
        assertThatThrownBy(() -> find(LogWriterLevel.ALL.intLevel())).isInstanceOf(IllegalArgumentException.class).hasMessage(("No AlertLevel found for intLevel " + (ALL.intLevel())));
    }
}

