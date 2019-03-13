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


import MagicCommandOutput.Status;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.kernel.BeakerXClasspath;
import com.twosigma.beakerx.kernel.PathToJar;
import com.twosigma.beakerx.kernel.magic.command.functionality.ClasspathAddJarMagic;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import com.twosigma.beakerx.util.BeakerXSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class LoadSparkFrom_SPARK_HOME_CommandTest {
    private LoadSparkFrom_SPARK_HOME_Command sut;

    private KernelTest kernel;

    private LoadSparkFrom_SPARK_HOME_CommandTest.BeakerXClasspathMock classpath;

    private LoadSparkFrom_SPARK_HOME_CommandTest.ClasspathAddJarMagicMock classpathAddJarMagicMock;

    private LoadSparkFrom_SPARK_HOME_CommandTest.BeakerXSystemMock beakerXSystem;

    @Test
    public void shouldNotAddSparkJarWhen_SPARK_HOME_notDefined() {
        // given
        classpath.isJarOnClasspath = false;
        beakerXSystem.env = null;
        // when
        sut.run();
        // then
        assertThat(classpathAddJarMagicMock.jars).isEmpty();
    }

    @Test
    public void shouldNotAddSparkJarWhenSparkOnClasspath() {
        // given
        classpath.isJarOnClasspath = true;
        // when
        sut.run();
        // then
        assertThat(classpathAddJarMagicMock.jars).isEmpty();
    }

    @Test
    public void shouldAddSparkJarWhenIsNotSparkOnClasspath() {
        // given
        classpath.isJarOnClasspath = false;
        String sparkPath = "sparkPath";
        beakerXSystem.env = sparkPath;
        // when
        sut.run();
        // then
        assertThat(classpathAddJarMagicMock.jars).isNotEmpty();
        assertThat(classpathAddJarMagicMock.jars.get(0)).contains(((((sparkPath + (File.separator)) + "jars") + (File.separator)) + "*"));
    }

    class BeakerXSystemMock implements BeakerXSystem {
        private String env = null;

        @Override
        public String getenv(String name) {
            return env;
        }
    }

    class ClasspathAddJarMagicMock implements ClasspathAddJarMagic {
        private List<String> jars = new ArrayList<>();

        @Override
        public MagicCommandOutcomeItem addJar(String path) {
            jars.add(path);
            return new com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutput(Status.OK);
        }
    }

    public static class BeakerXClasspathMock implements BeakerXClasspath {
        public boolean isJarOnClasspath = false;

        @Override
        public boolean isJarOnClasspath(String jarName) {
            return isJarOnClasspath;
        }

        @Override
        public boolean add(PathToJar path) {
            return false;
        }

        @Override
        public List<PathToJar> getPaths() {
            return null;
        }

        @Override
        public List<String> getPathsAsStrings() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String get(int index) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}

