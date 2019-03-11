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
package org.apache.geode.distributed;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.geode.management.internal.cli.json.GfJsonArray;
import org.apache.geode.management.internal.cli.json.GfJsonException;
import org.apache.geode.management.internal.cli.json.GfJsonObject;
import org.junit.Test;

import static ServiceState.TO_STRING_CLASS_PATH;
import static ServiceState.TO_STRING_GEODE_VERSION;
import static ServiceState.TO_STRING_JAVA_VERSION;
import static ServiceState.TO_STRING_JVM_ARGUMENTS;
import static ServiceState.TO_STRING_LOG_FILE;
import static ServiceState.TO_STRING_PROCESS_ID;
import static ServiceState.TO_STRING_UPTIME;
import static Status.valueOfDescription;


/**
 * Unit tests for {@link AbstractLauncher.ServiceState}. Tests marshalling of ServiceState to and
 * from JSON.
 *
 * @since GemFire 7.0
 */
public class AbstractLauncherServiceStateTest {
    private static String serviceName;

    private static String name;

    private static int pid;

    private static long uptime;

    private static String workingDirectory;

    private static List<String> jvmArguments;

    private static String classpath;

    private static String gemfireVersion;

    private static String javaVersion;

    private AbstractLauncherServiceStateTest.TestLauncher launcher;

    @Test
    public void serviceStateMarshalsToAndFromJsonWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String json = toJson();
        validateJson(status, json);
        validateStatus(status, AbstractLauncherServiceStateTest.TestLauncher.TestState.fromJson(json));
    }

    @Test
    public void serviceStateMarshalsToAndFromJsonWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String json = toJson();
        validateJson(status, json);
        validateStatus(status, AbstractLauncherServiceStateTest.TestLauncher.TestState.fromJson(json));
    }

    @Test
    public void serviceStateMarshalsToAndFromJsonWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String json = toJson();
        validateJson(status, json);
        validateStatus(status, AbstractLauncherServiceStateTest.TestLauncher.TestState.fromJson(json));
    }

    @Test
    public void serviceStateMarshalsToAndFromJsonWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String json = toJson();
        validateJson(status, json);
        validateStatus(status, AbstractLauncherServiceStateTest.TestLauncher.TestState.fromJson(json));
    }

    @Test
    public void toStringContainsLineSeparatorsWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(System.lineSeparator());
    }

    @Test
    public void toStringDoesNotContainLineSeparatorsWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(System.lineSeparator());
    }

    @Test
    public void toStringContainsLineSeparatorsWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(System.lineSeparator());
    }

    @Test
    public void toStringDoesNotContainLineSeparatorsWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(System.lineSeparator());
    }

    @Test
    public void toStringContainsProcessIdWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(((TO_STRING_PROCESS_ID) + (AbstractLauncherServiceStateTest.pid)));
    }

    @Test
    public void toStringDoesNotContainProcessIdWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsProcessIdWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(((TO_STRING_PROCESS_ID) + (AbstractLauncherServiceStateTest.pid)));
    }

    @Test
    public void toStringDoesNotContainProcessIdWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsJavaVersionWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringDoesNotContainJavaVersionWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsJavaVersionWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringDoesNotContainJavaVersionWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsLogFileWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringDoesNotContainLogFileWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsLogFileWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringDoesNotContainLogFileWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void toStringContainsJvmArgumentsWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void toStringDoesNotContainJvmArgumentsWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void toStringContainsJvmArgumentsWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void toStringDoesNotContainJvmArgumentsWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void toStringContainsClassPathWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_CLASS_PATH);
    }

    @Test
    public void toStringDoesNotContainClassPathWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_CLASS_PATH);
    }

    @Test
    public void toStringContainsClassPathWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_CLASS_PATH);
    }

    @Test
    public void toStringDoesNotContainClassPathWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_CLASS_PATH);
    }

    @Test
    public void toStringDoesNotContainUptimeWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_UPTIME);
    }

    @Test
    public void toStringDoesNotContainUptimeWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_UPTIME);
    }

    @Test
    public void toStringContainsUptimeWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_UPTIME);
    }

    @Test
    public void toStringDoesNotContainUptimeWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_UPTIME);
    }

    @Test
    public void toStringDoesNotContainGeodeVersionWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_GEODE_VERSION);
    }

    @Test
    public void toStringDoesNotContainGeodeVersionWhenNotResponding() {
        launcher.setStatus(Status.NOT_RESPONDING);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_GEODE_VERSION);
    }

    @Test
    public void toStringContainsGeodeVersionWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).contains(ServiceState.TO_STRING_GEODE_VERSION);
    }

    @Test
    public void toStringDoesNotContainGeodeVersionWhenStopped() {
        launcher.setStatus(Status.STOPPED);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        assertThat(result).doesNotContain(ServiceState.TO_STRING_GEODE_VERSION);
    }

    @Test
    public void processIdStartsOnNewLineInToStringWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String processId = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_PROCESS_ID)) {
                processId = line;
                break;
            }
        }
        assertThat(processId).as((((TO_STRING_PROCESS_ID) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void processIdStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String processId = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_PROCESS_ID)) {
                processId = line;
                break;
            }
        }
        assertThat(processId).as((((TO_STRING_PROCESS_ID) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_PROCESS_ID);
    }

    @Test
    public void uptimeStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String uptime = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_UPTIME)) {
                uptime = line;
                break;
            }
        }
        assertThat(uptime).as((((TO_STRING_UPTIME) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_UPTIME);
    }

    @Test
    public void geodeVersionStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String geodeVersion = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_GEODE_VERSION)) {
                geodeVersion = line;
                break;
            }
        }
        assertThat(geodeVersion).as((((TO_STRING_GEODE_VERSION) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_GEODE_VERSION);
    }

    @Test
    public void javaVersionStartsOnNewLineInToStringWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String javaVersion = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_JAVA_VERSION)) {
                javaVersion = line;
                break;
            }
        }
        assertThat(javaVersion).as((((TO_STRING_JAVA_VERSION) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_JAVA_VERSION);
    }

    @Test
    public void javaVersionStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String javaVersion = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_JAVA_VERSION)) {
                javaVersion = line;
                break;
            }
        }
        assertThat(javaVersion).as((((TO_STRING_JAVA_VERSION) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_JAVA_VERSION);
    }

    @Test
    public void logFileStartsOnNewLineInToStringWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String logFile = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_LOG_FILE)) {
                logFile = line;
                break;
            }
        }
        assertThat(logFile).as((((TO_STRING_LOG_FILE) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_LOG_FILE);
    }

    @Test
    public void logFileStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String logFile = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_LOG_FILE)) {
                logFile = line;
                break;
            }
        }
        assertThat(logFile).as((((TO_STRING_LOG_FILE) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_LOG_FILE);
    }

    @Test
    public void jvmArgumentsStartsOnNewLineInToStringWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String jvmArguments = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_JVM_ARGUMENTS)) {
                jvmArguments = line;
                break;
            }
        }
        assertThat(jvmArguments).as((((TO_STRING_JVM_ARGUMENTS) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void jvmArgumentsStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String jvmArguments = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_JVM_ARGUMENTS)) {
                jvmArguments = line;
                break;
            }
        }
        assertThat(jvmArguments).as((((TO_STRING_JVM_ARGUMENTS) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_JVM_ARGUMENTS);
    }

    @Test
    public void classPathStartsOnNewLineInToStringWhenStarting() {
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String classPath = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_CLASS_PATH)) {
                classPath = line;
                break;
            }
        }
        assertThat(classPath).as((((TO_STRING_CLASS_PATH) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_CLASS_PATH);
    }

    @Test
    public void classPathStartsOnNewLineInToStringWhenOnline() {
        launcher.setStatus(Status.ONLINE);
        AbstractLauncherServiceStateTest.TestLauncher.TestState status = launcher.status();
        String result = status.toString();
        List<String> lines = Arrays.asList(StringUtils.split(result, System.lineSeparator()));
        String classPath = null;
        for (String line : lines) {
            if (line.contains(ServiceState.TO_STRING_CLASS_PATH)) {
                classPath = line;
                break;
            }
        }
        assertThat(classPath).as((((TO_STRING_CLASS_PATH) + " line in ") + lines)).isNotNull().startsWith(ServiceState.TO_STRING_CLASS_PATH);
    }

    private static class TestLauncher extends AbstractLauncher<String> {
        private final InetAddress bindAddress;

        private final int port;

        private final String memberName;

        private final File logFile;

        private Status status;

        TestLauncher(final InetAddress bindAddress, final int port, final String memberName) {
            this.bindAddress = bindAddress;
            this.port = port;
            this.memberName = memberName;
            logFile = new File((memberName + ".log"));
        }

        public AbstractLauncherServiceStateTest.TestLauncher.TestState status() {
            return new AbstractLauncherServiceStateTest.TestLauncher.TestState(status, null, System.currentTimeMillis(), getId(), AbstractLauncherServiceStateTest.pid, AbstractLauncherServiceStateTest.uptime, AbstractLauncherServiceStateTest.workingDirectory, AbstractLauncherServiceStateTest.jvmArguments, AbstractLauncherServiceStateTest.classpath, AbstractLauncherServiceStateTest.gemfireVersion, AbstractLauncherServiceStateTest.javaVersion, getLogFileName(), getBindAddressAsString(), getPortAsString(), AbstractLauncherServiceStateTest.name);
        }

        @Override
        public void run() {
            // nothing
        }

        public String getId() {
            return (((((getServiceName()) + "@") + (getBindAddress())) + "[") + (getPort())) + "]";
        }

        @Override
        public String getLogFileName() {
            try {
                return logFile.getCanonicalPath();
            } catch (IOException e) {
                return logFile.getAbsolutePath();
            }
        }

        @Override
        public String getMemberName() {
            return memberName;
        }

        @Override
        public Integer getPid() {
            return null;
        }

        @Override
        public String getServiceName() {
            return AbstractLauncherServiceStateTest.serviceName;
        }

        InetAddress getBindAddress() {
            return bindAddress;
        }

        String getBindAddressAsString() {
            return bindAddress.getCanonicalHostName();
        }

        int getPort() {
            return port;
        }

        String getPortAsString() {
            return String.valueOf(getPort());
        }

        void setStatus(Status status) {
            this.status = status;
        }

        private static class TestState extends ServiceState<String> {
            protected static AbstractLauncherServiceStateTest.TestLauncher.TestState fromJson(final String json) {
                try {
                    GfJsonObject gfJsonObject = new GfJsonObject(json);
                    Status status = valueOfDescription(gfJsonObject.getString(JSON_STATUS));
                    List<String> jvmArguments = Arrays.asList(GfJsonArray.toStringArray(gfJsonObject.getJSONArray(JSON_JVMARGUMENTS)));
                    return new AbstractLauncherServiceStateTest.TestLauncher.TestState(status, gfJsonObject.getString(JSON_STATUSMESSAGE), gfJsonObject.getLong(JSON_TIMESTAMP), gfJsonObject.getString(JSON_LOCATION), gfJsonObject.getInt(JSON_PID), gfJsonObject.getLong(JSON_UPTIME), gfJsonObject.getString(JSON_WORKINGDIRECTORY), jvmArguments, gfJsonObject.getString(JSON_CLASSPATH), gfJsonObject.getString(JSON_GEMFIREVERSION), gfJsonObject.getString(JSON_JAVAVERSION), gfJsonObject.getString(JSON_LOGFILE), gfJsonObject.getString(JSON_HOST), gfJsonObject.getString(JSON_PORT), gfJsonObject.getString(JSON_MEMBERNAME));
                } catch (GfJsonException e) {
                    throw new IllegalArgumentException(("Unable to create TestState from JSON: " + json));
                }
            }

            protected TestState(final Status status, final String statusMessage, final long timestamp, final String location, final Integer pid, final Long uptime, final String workingDirectory, final List<String> jvmArguments, final String classpath, final String gemfireVersion, final String javaVersion, final String logFile, final String host, final String port, final String name) {
                super(status, statusMessage, timestamp, location, pid, uptime, workingDirectory, jvmArguments, classpath, gemfireVersion, javaVersion, logFile, host, port, name);
            }

            @Override
            protected String getServiceName() {
                return AbstractLauncherServiceStateTest.serviceName;
            }
        }
    }
}

