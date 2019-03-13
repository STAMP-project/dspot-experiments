/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.concurrency;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class RowMetaConcurrencyTest {
    private static final int cycles = 50;

    @Test
    public void fiveAddersAgainstTenReaders() throws Exception {
        final int addersAmount = 5;
        final int readersAmount = 10;
        final AtomicBoolean condition = new AtomicBoolean(true);
        final RowMeta rowMeta = new RowMeta();
        List<RowMetaConcurrencyTest.Adder> adders = new ArrayList<RowMetaConcurrencyTest.Adder>(addersAmount);
        for (int i = 0; i < addersAmount; i++) {
            adders.add(new RowMetaConcurrencyTest.Adder(condition, rowMeta, RowMetaConcurrencyTest.cycles, ("adder" + i)));
        }
        List<RowMetaConcurrencyTest.Getter> getters = new ArrayList<RowMetaConcurrencyTest.Getter>(readersAmount);
        for (int i = 0; i < readersAmount; i++) {
            getters.add(new RowMetaConcurrencyTest.Getter(condition, rowMeta));
        }
        ConcurrencyTestRunner<List<ValueMetaInterface>, ?> runner = new ConcurrencyTestRunner<List<ValueMetaInterface>, Object>(adders, getters, condition);
        runner.runConcurrentTest();
        runner.checkNoExceptionRaised();
        Set<ValueMetaInterface> results = new HashSet<ValueMetaInterface>(((RowMetaConcurrencyTest.cycles) * addersAmount));
        for (List<ValueMetaInterface> list : runner.getMonitoredTasksResults()) {
            results.addAll(list);
        }
        List<ValueMetaInterface> metas = rowMeta.getValueMetaList();
        Assert.assertEquals(((RowMetaConcurrencyTest.cycles) * addersAmount), metas.size());
        Assert.assertEquals(((RowMetaConcurrencyTest.cycles) * addersAmount), results.size());
        for (ValueMetaInterface meta : metas) {
            Assert.assertTrue(meta.getName(), results.remove(meta));
        }
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void fiveShufflersAgainstTenSearchers() throws Exception {
        final int elementsAmount = 100;
        final int shufflersAmount = 5;
        final int searchersAmount = 10;
        final RowMeta rowMeta = new RowMeta();
        for (int i = 0; i < elementsAmount; i++) {
            rowMeta.addValueMeta(new ValueMetaString(("meta_" + i)));
        }
        final AtomicBoolean condition = new AtomicBoolean(true);
        List<RowMetaConcurrencyTest.Shuffler> shufflers = new ArrayList<RowMetaConcurrencyTest.Shuffler>(shufflersAmount);
        for (int i = 0; i < shufflersAmount; i++) {
            shufflers.add(new RowMetaConcurrencyTest.Shuffler(condition, rowMeta, RowMetaConcurrencyTest.cycles));
        }
        List<RowMetaConcurrencyTest.Searcher> searchers = new ArrayList<RowMetaConcurrencyTest.Searcher>(searchersAmount);
        for (int i = 0; i < searchersAmount; i++) {
            String name = "meta_" + (new Random().nextInt(elementsAmount));
            Assert.assertTrue(((rowMeta.indexOfValue(name)) >= 0));
            searchers.add(new RowMetaConcurrencyTest.Searcher(condition, rowMeta, name));
        }
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(shufflers, searchers, condition);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addRemoveSearch() throws Exception {
        final int addersAmount = 5;
        final int removeAmount = 10;
        final int searchersAmount = 10;
        final RowMeta rowMeta = new RowMeta();
        List<String> toRemove = new ArrayList<String>(removeAmount);
        for (int i = 0; i < removeAmount; i++) {
            String name = "toBeRemoved_" + i;
            toRemove.add(name);
            rowMeta.addValueMeta(new ValueMetaString(name));
        }
        final AtomicBoolean condition = new AtomicBoolean(true);
        List<RowMetaConcurrencyTest.Searcher> searchers = new ArrayList<RowMetaConcurrencyTest.Searcher>(searchersAmount);
        for (int i = 0; i < searchersAmount; i++) {
            String name = "kept_" + i;
            rowMeta.addValueMeta(new ValueMetaString(name));
            searchers.add(new RowMetaConcurrencyTest.Searcher(condition, rowMeta, name));
        }
        List<RowMetaConcurrencyTest.Remover> removers = Collections.singletonList(new RowMetaConcurrencyTest.Remover(condition, rowMeta, toRemove));
        List<RowMetaConcurrencyTest.Adder> adders = new ArrayList<RowMetaConcurrencyTest.Adder>(addersAmount);
        for (int i = 0; i < addersAmount; i++) {
            adders.add(new RowMetaConcurrencyTest.Adder(condition, rowMeta, RowMetaConcurrencyTest.cycles, ("adder" + i)));
        }
        List<Callable<?>> monitored = new ArrayList<Callable<?>>();
        monitored.addAll(adders);
        monitored.addAll(removers);
        ConcurrencyTestRunner<?, ?> runner = new ConcurrencyTestRunner<Object, Object>(monitored, searchers, condition);
        runner.runConcurrentTest();
        runner.checkNoExceptionRaised();
        Map<? extends Callable<?>, ? extends ExecutionResult<?>> results = runner.getMonitoredResults();
        // removers should remove all elements
        for (RowMetaConcurrencyTest.Remover remover : removers) {
            ExecutionResult<List<String>> result = ((ExecutionResult<List<String>>) (results.get(remover)));
            Assert.assertTrue(result.getResult().isEmpty());
            for (String name : remover.getToRemove()) {
                Assert.assertEquals(name, (-1), rowMeta.indexOfValue(name));
            }
        }
        // adders should add all elements
        Set<ValueMetaInterface> metas = new HashSet<ValueMetaInterface>(rowMeta.getValueMetaList());
        for (RowMetaConcurrencyTest.Adder adder : adders) {
            ExecutionResult<List<ValueMetaInterface>> result = ((ExecutionResult<List<ValueMetaInterface>>) (results.get(adder)));
            for (ValueMetaInterface meta : result.getResult()) {
                Assert.assertTrue(meta.getName(), metas.remove(meta));
            }
        }
        Assert.assertEquals(searchersAmount, metas.size());
    }

    private static class Getter extends StopOnErrorCallable<Object> {
        private final RowMeta rowMeta;

        public Getter(AtomicBoolean condition, RowMeta rowMeta) {
            super(condition);
            this.rowMeta = rowMeta;
        }

        @Override
        Object doCall() throws Exception {
            Random random = new Random();
            while (condition.get()) {
                int acc = 0;
                for (ValueMetaInterface meta : rowMeta.getValueMetaList()) {
                    // fake cycle to from eliminating this snippet by JIT
                    acc += (meta.getType()) / 10;
                }
                Thread.sleep(random.nextInt(Math.max(100, acc)));
            } 
            return null;
        }
    }

    private static class Adder extends StopOnErrorCallable<List<ValueMetaInterface>> {
        private final RowMeta rowMeta;

        private final int cycles;

        private final String nameSeed;

        public Adder(AtomicBoolean condition, RowMeta rowMeta, int cycles, String nameSeed) {
            super(condition);
            this.rowMeta = rowMeta;
            this.cycles = cycles;
            this.nameSeed = nameSeed;
        }

        @Override
        List<ValueMetaInterface> doCall() throws Exception {
            Random random = new Random();
            List<ValueMetaInterface> result = new ArrayList<ValueMetaInterface>(cycles);
            for (int i = 0; (i < (cycles)) && (condition.get()); i++) {
                ValueMetaInterface added = new ValueMetaString((((nameSeed) + '_') + i));
                rowMeta.addValueMeta(added);
                result.add(added);
                Thread.sleep(random.nextInt(100));
            }
            return result;
        }
    }

    private static class Searcher extends StopOnErrorCallable<Object> {
        private final RowMeta rowMeta;

        private final String name;

        public Searcher(AtomicBoolean condition, RowMeta rowMeta, String name) {
            super(condition);
            this.rowMeta = rowMeta;
            this.name = name;
        }

        @Override
        Object doCall() throws Exception {
            Random random = new Random();
            while (condition.get()) {
                int index = rowMeta.indexOfValue(name);
                if (index < 0) {
                    throw new IllegalStateException((((name) + " was not found among ") + (rowMeta.getValueMetaList())));
                }
                Thread.sleep(random.nextInt(100));
            } 
            return null;
        }
    }

    private static class Shuffler extends StopOnErrorCallable<Object> {
        private final RowMeta rowMeta;

        private final int cycles;

        public Shuffler(AtomicBoolean condition, RowMeta rowMeta, int cycles) {
            super(condition);
            this.rowMeta = rowMeta;
            this.cycles = cycles;
        }

        @Override
        Object doCall() throws Exception {
            Random random = new Random();
            for (int i = 0; (i < (cycles)) && (condition.get()); i++) {
                List<ValueMetaInterface> list = new ArrayList<ValueMetaInterface>(rowMeta.getValueMetaList());
                Collections.shuffle(list);
                rowMeta.setValueMetaList(list);
                Thread.sleep(random.nextInt(100));
            }
            return null;
        }
    }

    private static class Remover extends StopOnErrorCallable<List<String>> {
        private final RowMeta rowMeta;

        private final List<String> toRemove;

        public Remover(AtomicBoolean condition, RowMeta rowMeta, List<String> toRemove) {
            super(condition);
            this.rowMeta = rowMeta;
            this.toRemove = toRemove;
        }

        @Override
        List<String> doCall() throws Exception {
            Random random = new Random();
            List<String> result = new LinkedList<String>(toRemove);
            for (Iterator<String> it = result.iterator(); (it.hasNext()) && (condition.get());) {
                String name = it.next();
                rowMeta.removeValueMeta(name);
                it.remove();
                Thread.sleep(random.nextInt(100));
            }
            return result;
        }

        public List<String> getToRemove() {
            return toRemove;
        }
    }
}

