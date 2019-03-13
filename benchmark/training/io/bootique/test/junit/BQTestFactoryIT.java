/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.test.junit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.config.ConfigurationFactory;
import io.bootique.log.BootLogger;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.test.TestIO;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class BQTestFactoryIT {
    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testCreateRuntime_Injection() {
        BQRuntime runtime = testFactory.app("-x").autoLoadModules().createRuntime();
        Assert.assertArrayEquals(new String[]{ "-x" }, runtime.getArgs());
    }

    @Test
    public void testCreateRuntime_Streams_NoTrace() {
        TestIO io = TestIO.noTrace();
        CommandOutcome result = testFactory.app("-x").autoLoadModules().module(( b) -> BQCoreModule.extend(b).addCommand(.class)).bootLogger(io.getBootLogger()).createRuntime().run();
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("--out--", io.getStdout().trim());
        Assert.assertEquals("--err--", io.getStderr().trim());
    }

    @Test
    public void testConfigEnvExcludes_System() {
        System.setProperty("bq.a", "bq_a");
        System.setProperty("bq.c.m.k", "bq_c_m_k");
        System.setProperty("bq.c.m.l", "bq_c_m_l");
        BQRuntime runtime = testFactory.app("--config=src/test/resources/configEnvironment.yml").createRuntime();
        BQTestFactoryIT.Bean1 b1 = runtime.getInstance(ConfigurationFactory.class).config(BQTestFactoryIT.Bean1.class, "");
        Assert.assertEquals("e", b1.a);
        Assert.assertEquals("q", b1.c.m.k);
        Assert.assertEquals("n", b1.c.m.l);
        System.clearProperty("bq.a");
        System.clearProperty("bq.c.m.k");
        System.clearProperty("bq.c.m.l");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Bean1 {
        private String a;

        private BQTestFactoryIT.Bean2 c;

        public void setA(String a) {
            this.a = a;
        }

        public void setC(BQTestFactoryIT.Bean2 c) {
            this.c = c;
        }
    }

    static class Bean2 {
        private BQTestFactoryIT.Bean3 m;

        public void setM(BQTestFactoryIT.Bean3 m) {
            this.m = m;
        }
    }

    static class Bean3 {
        private String k;

        private String f;

        private String l;

        public void setK(String k) {
            this.k = k;
        }

        public void setF(String f) {
            this.f = f;
        }

        public void setL(String l) {
            this.l = l;
        }
    }

    public static class XCommand extends CommandWithMetadata {
        private BootLogger logger;

        @Inject
        public XCommand(BootLogger logger) {
            super(CommandMetadata.builder(BQTestFactoryIT.XCommand.class));
            this.logger = logger;
        }

        @Override
        public CommandOutcome run(Cli cli) {
            logger.stderr("--err--");
            logger.stdout("--out--");
            return CommandOutcome.succeeded();
        }
    }
}

