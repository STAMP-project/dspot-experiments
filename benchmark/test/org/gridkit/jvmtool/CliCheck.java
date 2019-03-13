/**
 * Copyright 2013 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridkit.jvmtool;


import java.lang.management.ManagementFactory;
import org.junit.Test;


/**
 * JUnit command runner.
 *
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class CliCheck {
    private static String PID;

    static {
        CliCheck.PID = ManagementFactory.getRuntimeMXBean().getName();
        CliCheck.PID = CliCheck.PID.substring(0, CliCheck.PID.indexOf('@'));
    }

    @Test
    public void mxdump_self() {
        exec("mxdump", "-p", CliCheck.PID);
    }

    @Test
    public void mxdump_by_query() {
        exec("mxdump", "-p", CliCheck.PID, "-q", "java.lang:type=GarbageCollector,name=*");
    }
}

