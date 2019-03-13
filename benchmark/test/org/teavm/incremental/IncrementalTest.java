/**
 * Copyright 2018 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.incremental;


import TeaVMOptimizationLevel.SIMPLE;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.teavm.ast.AsyncMethodNode;
import org.teavm.backend.javascript.JavaScriptTarget;
import org.teavm.cache.AstCacheEntry;
import org.teavm.cache.CacheStatus;
import org.teavm.cache.InMemoryMethodNodeCache;
import org.teavm.cache.InMemoryProgramCache;
import org.teavm.cache.InMemorySymbolTable;
import org.teavm.callgraph.CallGraph;
import org.teavm.diagnostics.DefaultProblemTextConsumer;
import org.teavm.diagnostics.Problem;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderSource;
import org.teavm.model.ClassReader;
import org.teavm.model.MethodReference;
import org.teavm.model.Program;
import org.teavm.model.ReferenceCache;
import org.teavm.model.util.ModelUtils;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.TeaVM;


public class IncrementalTest {
    private static final String OLD_FILE = "classes-old.js";

    private static final String NEW_FILE = "classes-new.js";

    private static final String REFRESHED_FILE = "classes-refreshed.js";

    private static ClassHolderSource oldClassSource = new org.teavm.parsing.ClasspathClassHolderSource(IncrementalTest.class.getClassLoader(), new ReferenceCache());

    private static Context rhinoContext;

    private static ScriptableObject rhinoRootScope;

    private String[] updatedMethods;

    private String oldResult;

    private String newResult;

    @Rule
    public TestName name = new TestName();

    @Test
    public void simple() {
        run();
        checkUpdatedMethods("Foo.get", "Main.callFoo");
        Assert.assertEquals("old", oldResult);
        Assert.assertEquals("new", newResult);
    }

    @Test
    public void lambda() {
        run();
        checkUpdatedMethods("Bar.call", "Bar$call$lambda$_1_0.get", "Bar$call$lambda$_1_0.<init>", "BarNew.lambda$call$0", "Main.run");
        Assert.assertEquals("Foo: bar-old", oldResult);
        Assert.assertEquals("Foo: bar-new", newResult);
    }

    @Test
    public void lambdaUnchanged() {
        run();
        checkUpdatedMethods();
        Assert.assertEquals("Foo: main", oldResult);
        Assert.assertEquals("Foo: main", newResult);
    }

    @Test
    public void meta() {
        run();
        checkUpdatedMethods("Main$PROXY$2_0.meta", "Main$StringSupplier$proxy$2_0_0.<init>", "Main.meta", "Main$StringSupplier$proxy$2_0_0.get");
        Assert.assertEquals("meta: ok", oldResult);
        Assert.assertEquals("meta: ok", newResult);
    }

    static class Builder {
        String entryPoint;

        IncrementalTest.CapturingMethodNodeCache astCache = new IncrementalTest.CapturingMethodNodeCache();

        IncrementalTest.CapturingProgramCache programCache = new IncrementalTest.CapturingProgramCache();

        IncrementalTest.BuildTargetImpl buildTarget = new IncrementalTest.BuildTargetImpl();

        Builder(String entryPoint) {
            this.entryPoint = entryPoint;
        }

        void enableCapturing() {
            programCache.capturing = true;
            astCache.capturing = true;
        }

        void build(ClassHolderSource classSource, CacheStatus cacheStatus, String name) {
            JavaScriptTarget target = new JavaScriptTarget();
            TeaVM vm = setClassLoader(IncrementalTest.class.getClassLoader()).setClassSource(classSource).setDependencyAnalyzerFactory(FastDependencyAnalyzer::new).build();
            vm.setCacheStatus(cacheStatus);
            vm.setOptimizationLevel(SIMPLE);
            vm.setProgramCache(programCache);
            target.setAstCache(astCache);
            target.setMinifying(false);
            vm.add(new EntryPointTransformer(entryPoint));
            vm.entryPoint(EntryPoint.class.getName());
            vm.installPlugins();
            vm.build(buildTarget, name);
            List<Problem> problems = vm.getProblemProvider().getSevereProblems();
            if (!(problems.isEmpty())) {
                Assert.fail(((("Compiler error generating file '" + name) + "\'\n") + (buildErrorMessage(vm))));
            }
            commit();
            commit();
        }

        private String buildErrorMessage(TeaVM vm) {
            CallGraph cg = vm.getDependencyInfo().getCallGraph();
            DefaultProblemTextConsumer consumer = new DefaultProblemTextConsumer();
            StringBuilder sb = new StringBuilder();
            for (Problem problem : vm.getProblemProvider().getProblems()) {
                consumer.clear();
                problem.render(consumer);
                sb.append(consumer.getText());
                TeaVMProblemRenderer.renderCallStack(cg, problem.getLocation(), sb);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    static class CapturingMethodNodeCache extends InMemoryMethodNodeCache {
        final Set<MethodReference> updatedMethods = new HashSet<>();

        boolean capturing;

        CapturingMethodNodeCache() {
            super(new ReferenceCache(), new InMemorySymbolTable(), new InMemorySymbolTable(), new InMemorySymbolTable());
        }

        @Override
        public void store(MethodReference methodReference, AstCacheEntry node, Supplier<String[]> dependencies) {
            super.store(methodReference, node, dependencies);
            if (capturing) {
                updatedMethods.add(methodReference);
            }
        }

        @Override
        public void storeAsync(MethodReference methodReference, AsyncMethodNode node, Supplier<String[]> dependencies) {
            super.storeAsync(methodReference, node, dependencies);
            if (capturing) {
                updatedMethods.add(methodReference);
            }
        }
    }

    static class CapturingProgramCache extends InMemoryProgramCache {
        final Set<MethodReference> updatedMethods = new HashSet<>();

        boolean capturing;

        CapturingProgramCache() {
            super(new ReferenceCache(), new InMemorySymbolTable(), new InMemorySymbolTable(), new InMemorySymbolTable());
        }

        @Override
        public void store(MethodReference method, Program program, Supplier<String[]> dependencies) {
            super.store(method, program, dependencies);
            if (capturing) {
                updatedMethods.add(method);
            }
        }
    }

    static class ClassHolderSourceImpl implements CacheStatus , ClassHolderSource {
        private ClassHolderSource underlying;

        private Map<String, ClassHolder> cache = new HashMap<>();

        private boolean replace;

        ClassHolderSourceImpl(ClassHolderSource underlying, boolean replace) {
            this.underlying = underlying;
            this.replace = replace;
        }

        @Override
        public boolean isStaleClass(String className) {
            ClassReader cls = underlying.get(className);
            if (cls == null) {
                return false;
            }
            return (cls.getAnnotations().get(Update.class.getName())) != null;
        }

        @Override
        public boolean isStaleMethod(MethodReference method) {
            return isStaleClass(method.getClassName());
        }

        @Override
        public ClassHolder get(String name) {
            if (!(replace)) {
                return underlying.get(name);
            }
            return cache.computeIfAbsent(name, ( key) -> {
                ClassHolder cls = underlying.get(key);
                if (cls == null) {
                    return cls;
                }
                if ((cls.getAnnotations().get(.class.getName())) != null) {
                    ClassHolder newClass = underlying.get((key + "New"));
                    if (newClass != null) {
                        cls = ModelUtils.copyClass(newClass, new ClassHolder(key));
                    }
                }
                return cls;
            });
        }
    }

    static class BuildTargetImpl implements BuildTarget {
        private Map<String, ByteArrayOutputStream> fs = new HashMap<>();

        public String get(String name) {
            return new String(fs.get(name).toByteArray(), StandardCharsets.UTF_8);
        }

        @Override
        public OutputStream createResource(String fileName) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            fs.put(fileName, out);
            return out;
        }
    }
}

