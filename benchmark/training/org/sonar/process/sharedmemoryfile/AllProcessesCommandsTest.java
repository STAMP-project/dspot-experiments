/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.process.sharedmemoryfile;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class AllProcessesCommandsTest {
    private static final int PROCESS_NUMBER = 1;

    private static final byte STOP = ((byte) (255));

    private static final byte RESTART = ((byte) (170));

    private static final byte UP = ((byte) (1));

    private static final byte OPERATIONAL = ((byte) (89));

    private static final byte EMPTY = ((byte) (0));

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fail_to_init_if_dir_does_not_exist() throws Exception {
        File dir = temp.newFolder();
        FileUtils.deleteQuietly(dir);
        try {
            new AllProcessesCommands(dir);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(("Not a valid directory: " + (dir.getAbsolutePath())));
        }
    }

    @Test
    public void write_and_read_up() throws IOException {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 0;
            assertThat(commands.isUp(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.EMPTY);
            commands.setUp(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(commands.isUp(AllProcessesCommandsTest.PROCESS_NUMBER)).isTrue();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.UP);
        }
    }

    @Test
    public void write_and_read_operational() throws IOException {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 3;
            assertThat(commands.isOperational(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.EMPTY);
            commands.setOperational(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(commands.isOperational(AllProcessesCommandsTest.PROCESS_NUMBER)).isTrue();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.OPERATIONAL);
        }
    }

    @Test
    public void write_and_read_ping() throws IOException {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 4;
            assertThat(readLong(commands, offset)).isEqualTo(0L);
            long currentTime = System.currentTimeMillis();
            commands.ping(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(readLong(commands, offset)).isGreaterThanOrEqualTo(currentTime);
        }
    }

    @Test
    public void write_and_read_jmx_url() throws IOException {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 12;
            for (int i = 0; i < 500; i++) {
                assertThat(readByte(commands, (offset + i))).isEqualTo(AllProcessesCommandsTest.EMPTY);
            }
            commands.setSystemInfoUrl(AllProcessesCommandsTest.PROCESS_NUMBER, "jmx:foo");
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.EMPTY);
            assertThat(commands.getSystemInfoUrl(AllProcessesCommandsTest.PROCESS_NUMBER)).isEqualTo("jmx:foo");
        }
    }

    @Test
    public void ask_for_stop() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 1;
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.STOP);
            assertThat(commands.askedForStop(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
            commands.askForStop(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(commands.askedForStop(AllProcessesCommandsTest.PROCESS_NUMBER)).isTrue();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.STOP);
        }
    }

    @Test
    public void ask_for_restart() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 2;
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.RESTART);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
            commands.askForRestart(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isTrue();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.RESTART);
        }
    }

    @Test
    public void acknowledgeAskForRestart_has_no_effect_when_no_restart_asked() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 2;
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.RESTART);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
            commands.acknowledgeAskForRestart(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.RESTART);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
        }
    }

    @Test
    public void acknowledgeAskForRestart_resets_askForRestart_has_no_effect_when_no_restart_asked() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int offset = 2;
            commands.askForRestart(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isTrue();
            assertThat(readByte(commands, offset)).isEqualTo(AllProcessesCommandsTest.RESTART);
            commands.acknowledgeAskForRestart(AllProcessesCommandsTest.PROCESS_NUMBER);
            assertThat(readByte(commands, offset)).isNotEqualTo(AllProcessesCommandsTest.RESTART);
            assertThat(commands.askedForRestart(AllProcessesCommandsTest.PROCESS_NUMBER)).isFalse();
        }
    }

    @Test
    public void getProcessCommands_fails_if_processNumber_is_less_than_0() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int processNumber = -2;
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage((("Process number " + processNumber) + " is not valid"));
            commands.createAfterClean(processNumber);
        }
    }

    @Test
    public void getProcessCommands_fails_if_processNumber_is_higher_than_MAX_PROCESSES() throws Exception {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            int processNumber = (ProcessCommands.MAX_PROCESSES) + 1;
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage((("Process number " + processNumber) + " is not valid"));
            commands.createAfterClean(processNumber);
        }
    }

    @Test
    public void clean_cleans_sharedMemory_of_any_process_less_than_MAX_PROCESSES() throws IOException {
        try (AllProcessesCommands commands = new AllProcessesCommands(temp.newFolder())) {
            for (int i = 0; i < (ProcessCommands.MAX_PROCESSES); i++) {
                commands.create(i).setUp();
            }
            commands.clean();
            for (int i = 0; i < (ProcessCommands.MAX_PROCESSES); i++) {
                assertThat(commands.create(i).isUp()).isFalse();
            }
        }
    }
}

