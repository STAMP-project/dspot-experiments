/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import ResourceType.DRL;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class ParallelCompilationTest {
    private static final int PARALLEL_THREADS = 5;

    private static final String DRL_FILE = "parallel_compilation.drl";

    private ExecutorService executor;

    @Test(timeout = 10000)
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(ParallelCompilationTest.BuildExecutor.getSolvers());
    }

    public static class BuildExecutor implements Callable<KieBase> {
        public KieBase call() throws Exception {
            final Reader source = new InputStreamReader(ParallelCompilationTest.class.getResourceAsStream(ParallelCompilationTest.DRL_FILE));
            final Properties props = new Properties();
            props.setProperty("drools.dialect.java.compiler", "JANINO");
            props.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
            KieBase result;
            final KnowledgeBuilderConfiguration configuration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(props, ParallelCompilationTest.class.getClass().getClassLoader());
            final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(configuration);
            Thread.sleep(Math.round(((Math.random()) * 250)));
            Resource newReaderResource = ResourceFactory.newReaderResource(source);
            // synchronized (RuleUtil.class)
            {
                builder.add(newReaderResource, DRL);
            }
            result = builder.newKieBase();
            return result;
        }

        public static Collection<Callable<KieBase>> getSolvers() {
            Collection<Callable<KieBase>> solvers = new ArrayList<Callable<KieBase>>();
            for (int i = 0; i < (ParallelCompilationTest.PARALLEL_THREADS); ++i) {
                solvers.add(new ParallelCompilationTest.BuildExecutor());
            }
            return solvers;
        }
    }

    public static class User {
        private int age;

        private boolean risky;

        private ParallelCompilationTest.User.Gender gender;

        private String name;

        public enum Gender {

            MALE,
            FEMALE,
            OTHER;}

        public User(int age, ParallelCompilationTest.User.Gender gender, String name) {
            this.age = age;
            this.gender = gender;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public ParallelCompilationTest.User.Gender getGender() {
            return gender;
        }

        public void setGender(ParallelCompilationTest.User.Gender gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRisky() {
            return risky;
        }

        public void setRisky(boolean risky) {
            this.risky = risky;
        }
    }
}

