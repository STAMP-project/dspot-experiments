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
package org.apache.geode.internal.logging;


import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link LogWriterLevel}.
 */
@Category(LoggingTest.class)
public class LogWriterLevelTest {
    @Test
    public void isSerializable() {
        assertThat(LogWriterLevel.ALL).isInstanceOf(Serializable.class);
    }

    @Test
    public void serializes() {
        LogWriterLevel logLevel = ((LogWriterLevel) (SerializationUtils.clone(LogWriterLevel.ALL)));
        assertThat(logLevel).isEqualTo(LogWriterLevel.ALL).isSameAs(LogWriterLevel.ALL);
    }

    @Test
    public void findNONE() {
        assertThat(LogWriterLevel.find(LogWriterLevel.NONE.intLevel())).isEqualTo(LogWriterLevel.NONE);
    }

    @Test
    public void findSEVERE() {
        assertThat(LogWriterLevel.find(LogWriterLevel.SEVERE.intLevel())).isEqualTo(LogWriterLevel.SEVERE);
    }

    @Test
    public void findERROR() {
        assertThat(LogWriterLevel.find(LogWriterLevel.ERROR.intLevel())).isEqualTo(LogWriterLevel.ERROR);
    }

    @Test
    public void findWARNING() {
        assertThat(LogWriterLevel.find(LogWriterLevel.WARNING.intLevel())).isEqualTo(LogWriterLevel.WARNING);
    }

    @Test
    public void findINFO() {
        assertThat(LogWriterLevel.find(LogWriterLevel.INFO.intLevel())).isEqualTo(LogWriterLevel.INFO);
    }

    @Test
    public void findCONFIG() {
        assertThat(LogWriterLevel.find(LogWriterLevel.CONFIG.intLevel())).isEqualTo(LogWriterLevel.CONFIG);
    }

    @Test
    public void findFINE() {
        assertThat(LogWriterLevel.find(LogWriterLevel.FINE.intLevel())).isEqualTo(LogWriterLevel.FINE);
    }

    @Test
    public void findFINER() {
        assertThat(LogWriterLevel.find(LogWriterLevel.FINER.intLevel())).isEqualTo(LogWriterLevel.FINER);
    }

    @Test
    public void findFINEST() {
        assertThat(LogWriterLevel.find(LogWriterLevel.FINEST.intLevel())).isEqualTo(LogWriterLevel.FINEST);
    }

    @Test
    public void findALL() {
        assertThat(LogWriterLevel.find(LogWriterLevel.ALL.intLevel())).isEqualTo(LogWriterLevel.ALL);
    }
}

