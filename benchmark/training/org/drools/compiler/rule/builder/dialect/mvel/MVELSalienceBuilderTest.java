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
package org.drools.compiler.rule.builder.dialect.mvel;


import org.drools.compiler.Person;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.WorkingMemory;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Salience;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;


public class MVELSalienceBuilderTest {
    private RuleBuildContext context;

    private InternalKnowledgeBase kBase;

    @Test
    public void testSimpleExpression() {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        final Person p = new Person("mark", "", 31);
        final InternalFactHandle f0 = ((InternalFactHandle) (ksession.insert(p)));
        final LeftTupleImpl tuple = new LeftTupleImpl(f0, null, true);
        RuleTerminalNode rtn = new RuleTerminalNode();
        rtn.setSalienceDeclarations(context.getDeclarationResolver().getDeclarations(context.getRule()).values().toArray(new Declaration[1]));
        AgendaItem item = new org.drools.core.common.AgendaItemImpl(0, tuple, 0, null, rtn, null);
        Assert.assertEquals(25, context.getRule().getSalience().getValue(new org.drools.core.base.DefaultKnowledgeHelper(item, ksession), context.getRule(), ksession));
    }

    @Test
    public void testMultithreadSalienceExpression() {
        final int tcount = 10;
        final MVELSalienceBuilderTest.SalienceEvaluator[] evals = new MVELSalienceBuilderTest.SalienceEvaluator[tcount];
        final Thread[] threads = new Thread[tcount];
        for (int i = 0; i < (evals.length); i++) {
            evals[i] = new MVELSalienceBuilderTest.SalienceEvaluator(kBase, context, context.getRule(), context.getRule().getSalience(), new Person(("bob" + i), (30 + (i * 3))));
            threads[i] = new Thread(evals[i]);
        }
        for (int i = 0; i < (threads.length); i++) {
            threads[i].start();
        }
        for (int i = 0; i < (threads.length); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int errors = 0;
        for (int i = 0; i < (evals.length); i++) {
            if (evals[i].isError()) {
                errors++;
            }
        }
        Assert.assertEquals("There shouldn't be any threads in error: ", 0, errors);
    }

    public static class SalienceEvaluator implements Runnable {
        public static final int iterations = 1000;

        private Salience salience;

        private Rule rule;

        private LeftTupleImpl tuple;

        private WorkingMemory wm;

        private final int result;

        private transient boolean halt;

        private RuleBuildContext context;

        private AgendaItem item;

        private boolean error;

        public SalienceEvaluator(InternalKnowledgeBase kBase, RuleBuildContext context, Rule rule, Salience salience, Person person) {
            wm = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
            this.context = context;
            final InternalFactHandle f0 = ((InternalFactHandle) (wm.insert(person)));
            tuple = new LeftTupleImpl(f0, null, true);
            this.salience = salience;
            this.halt = false;
            this.error = false;
            this.result = ((person.getAge()) + 20) / 2;
            RuleTerminalNode rtn = new RuleTerminalNode();
            rtn.setSalienceDeclarations(context.getDeclarationResolver().getDeclarations(context.getRule()).values().toArray(new Declaration[1]));
            item = new org.drools.core.common.AgendaItemImpl(0, tuple, 0, null, rtn, null);
        }

        public void run() {
            try {
                Thread.sleep(1000);
                for (int i = 0; (i < (MVELSalienceBuilderTest.SalienceEvaluator.iterations)) && (!(halt)); i++) {
                    Assert.assertEquals(result, salience.getValue(new org.drools.core.base.DefaultKnowledgeHelper(item, wm), rule, wm));
                    Thread.currentThread().yield();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                this.error = true;
            }
        }

        public void halt() {
            this.halt = true;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }
    }
}

