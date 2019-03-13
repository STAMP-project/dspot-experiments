/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.scala.magic.command;


import MagicCommandOutcomeItem.Status.OK;
import MagicCommandOutput.Status;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MagicCommandConfigurationMock;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.MagicCommand;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandExecutionParam;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;


public class EnableSparkSupportMagicInitConfigurationTest {
    private EnableSparkSupportMagicInitConfiguration sut;

    private KernelTest kernel;

    private EnableSparkSupportMagicInitConfigurationTest.SparkInitCommandFactoryMock commandFactoryMock;

    public static final ArrayList<MagicCommandOutcomeItem> NO_ERRORS = new ArrayList<>();

    private MagicCommandConfigurationMock configurationMock = new MagicCommandConfigurationMock();

    @Test
    public void runSparkMagicCommand() {
        // given
        String allCode = EnableSparkSupportMagicCommand.ENABLE_SPARK_SUPPORT;
        MagicCommand command = new MagicCommand(new com.twosigma.beakerx.kernel.magic.command.functionality.ClassPathAddMvnCellMagicCommand(configurationMock.mavenResolverParam(kernel), kernel), allCode);
        Code code = Code.createCode(allCode, Collections.singletonList(command), EnableSparkSupportMagicInitConfigurationTest.NO_ERRORS, MessageFactorTest.commMsg());
        // when
        MagicCommandOutcomeItem run = sut.run(new MagicCommandExecutionParam(allCode, "", 1, code, true));
        // then
        assertThat(commandFactoryMock.isJarAdded).isTrue();
        assertThat(commandFactoryMock.isLoadSparkSupportMagicClass).isTrue();
        assertThat(commandFactoryMock.isRunOptions).isTrue();
        assertThat(commandFactoryMock.isLoadSparkFrom_SPARK_HOME_IfIsNotOnClasspath).isTrue();
        assertThat(commandFactoryMock.isLoadLatestVersionOfSparkIfIsNotOnClasspath).isTrue();
        assertThat(commandFactoryMock.isLoadSparkSupportMagic).isTrue();
        assertThat(run.getStatus()).isEqualTo(OK);
    }

    public static class SparkInitCommandFactoryMock implements SparkInitCommandFactory {
        boolean isJarAdded = false;

        boolean isLoadSparkSupportMagicClass = false;

        boolean isRunOptions = false;

        boolean isLoadSparkFrom_SPARK_HOME_IfIsNotOnClasspath = false;

        boolean isLoadLatestVersionOfSparkIfIsNotOnClasspath = false;

        boolean isLoadSparkSupportMagic = false;

        @Override
        public Command addSparkexJar() {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isJarAdded = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "addSparkexJarError";
                }
            };
        }

        @Override
        public Command loadSparkSupportMagicClass() {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isLoadSparkSupportMagicClass = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "loadSparkSupportMagicClassError";
                }
            };
        }

        @Override
        public Command runOptions(MagicCommandExecutionParam param) {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isRunOptions = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "runOptionsError";
                }
            };
        }

        @Override
        public Command loadSparkFrom_SPARK_HOME_IfIsNotOnClasspath() {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isLoadSparkFrom_SPARK_HOME_IfIsNotOnClasspath = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "loadSparkFrom_SPARK_HOME_IfIsNotOnClasspathError";
                }
            };
        }

        @Override
        public Command loadLatestVersionOfSparkIfIsNotOnClasspath(MagicCommandExecutionParam param) {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isLoadLatestVersionOfSparkIfIsNotOnClasspath = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "loadLatestVersionOfSparkIfIsNotOnClasspathError";
                }
            };
        }

        @Override
        public Command loadSparkSupportMagic(MagicCommandExecutionParam param) {
            return new Command() {
                @Override
                public MagicCommandOutcomeItem run() {
                    isLoadSparkSupportMagic = true;
                    return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
                }

                @Override
                public String getErrorMessage() {
                    return "loadSparkSupportMagicError";
                }
            };
        }
    }
}

