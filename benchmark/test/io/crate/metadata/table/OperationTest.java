/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata.table;


import IndexMetaData.SETTING_BLOCKS_METADATA;
import IndexMetaData.SETTING_BLOCKS_READ;
import IndexMetaData.SETTING_BLOCKS_WRITE;
import IndexMetaData.SETTING_READ_ONLY;
import IndexMetaData.State.OPEN;
import Operation.ALL;
import Operation.ALTER;
import Operation.ALTER_BLOCKS;
import Operation.ALTER_OPEN_CLOSE;
import Operation.ALTER_REROUTE;
import Operation.COPY_TO;
import Operation.CREATE_SNAPSHOT;
import Operation.DELETE;
import Operation.DROP;
import Operation.INSERT;
import Operation.OPTIMIZE;
import Operation.READ;
import Operation.READ_ONLY;
import Operation.REFRESH;
import Operation.SHOW_CREATE;
import Operation.UPDATE;
import Settings.EMPTY;
import io.crate.test.integration.CrateUnitTest;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class OperationTest extends CrateUnitTest {
    @Test
    public void testBuildFromEmptyIndexBlocks() throws Exception {
        assertThat(Operation.buildFromIndexSettingsAndState(EMPTY, OPEN), Is.is(ALL));
    }

    @Test
    public void testBuildFromSingleIndexBlocks() throws Exception {
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_READ_ONLY, true).build(), OPEN), Is.is(READ_ONLY));
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_READ, true).build(), OPEN), Matchers.containsInAnyOrder(UPDATE, INSERT, DELETE, DROP, ALTER, ALTER_OPEN_CLOSE, ALTER_BLOCKS, REFRESH, OPTIMIZE, ALTER_REROUTE));
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_WRITE, true).build(), OPEN), Matchers.containsInAnyOrder(READ, ALTER, ALTER_OPEN_CLOSE, ALTER_BLOCKS, SHOW_CREATE, REFRESH, OPTIMIZE, COPY_TO, CREATE_SNAPSHOT, ALTER_REROUTE));
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_METADATA, true).build(), OPEN), Matchers.containsInAnyOrder(READ, UPDATE, INSERT, DELETE, ALTER_BLOCKS, ALTER_OPEN_CLOSE, REFRESH, SHOW_CREATE, OPTIMIZE, ALTER_REROUTE));
    }

    @Test
    public void testBuildFromCompoundIndexBlocks() throws Exception {
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_READ, true).put(SETTING_BLOCKS_WRITE, true).build(), OPEN), Matchers.containsInAnyOrder(ALTER, ALTER_OPEN_CLOSE, ALTER_BLOCKS, REFRESH, OPTIMIZE, ALTER_REROUTE));
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_WRITE, true).put(SETTING_BLOCKS_METADATA, true).build(), OPEN), Matchers.containsInAnyOrder(READ, ALTER_OPEN_CLOSE, ALTER_BLOCKS, REFRESH, SHOW_CREATE, OPTIMIZE, ALTER_REROUTE));
        assertThat(Operation.buildFromIndexSettingsAndState(Settings.builder().put(SETTING_BLOCKS_READ, true).put(SETTING_BLOCKS_METADATA, true).build(), OPEN), Matchers.containsInAnyOrder(INSERT, UPDATE, DELETE, ALTER_OPEN_CLOSE, ALTER_BLOCKS, REFRESH, OPTIMIZE, ALTER_REROUTE));
    }
}

