/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.provider.stat.filedescriptor;


import JvmType.IBM;
import JvmType.ORACLE;
import JvmVersion.JAVA_6;
import JvmVersion.JAVA_9;
import OsType.AIX;
import OsType.BSD;
import OsType.LINUX;
import OsType.SOLARIS;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class FileDescriptorMetricProviderTest {
    private final String ORACLE_FILE_DESCRIPTOR_METRIC = "com.navercorp.pinpoint.profiler.monitor.metric.filedescriptor.oracle.DefaultFileDescriptorMetric";

    private final String IBM_FILE_DESCRIPTOR_METRIC = "com.navercorp.pinpoint.profiler.monitor.metric.filedescriptor.ibm.DefaultFileDescriptorMetric";

    @Test
    public void testOracle_LINUX() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        FileDescriptorMetricProvider fileDescriptorMetricProvider = new FileDescriptorMetricProvider(config);
        String metricClassName = fileDescriptorMetricProvider.getMetricClassName(LINUX, JAVA_6, ORACLE);
        Assert.assertEquals(ORACLE_FILE_DESCRIPTOR_METRIC, metricClassName);
        String metricClassName2 = fileDescriptorMetricProvider.getMetricClassName(AIX, JAVA_6, ORACLE);
        Assert.assertEquals(ORACLE_FILE_DESCRIPTOR_METRIC, metricClassName2);
        String metricClassName3 = fileDescriptorMetricProvider.getMetricClassName(BSD, JAVA_6, ORACLE);
        Assert.assertEquals(ORACLE_FILE_DESCRIPTOR_METRIC, metricClassName3);
    }

    @Test
    public void testIBM_SOLARIS() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        FileDescriptorMetricProvider fileDescriptorMetricProvider = new FileDescriptorMetricProvider(config);
        String metricClassName = fileDescriptorMetricProvider.getMetricClassName(SOLARIS, JAVA_9, IBM);
        Assert.assertEquals(IBM_FILE_DESCRIPTOR_METRIC, metricClassName);
    }
}

