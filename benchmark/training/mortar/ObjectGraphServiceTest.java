/**
 * Copyright 2013 Square Inc.
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
package mortar;


import android.content.Context;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.inject.Qualifier;
import mortar.dagger1support.ObjectGraphService;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("InnerClassMayBeStatic")
public class ObjectGraphServiceTest {
    @Mock
    Context context;

    @Mock
    Scoped scoped;

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Apple {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Bagel {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Carrot {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Dogfood {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Eggplant {}

    @Module(injects = ObjectGraphServiceTest.HasApple.class)
    class AppleModule {
        @Provides
        @ObjectGraphServiceTest.Apple
        String provideApple() {
            return ObjectGraphServiceTest.Apple.class.getName();
        }
    }

    @Module(injects = ObjectGraphServiceTest.HasBagel.class)
    class BagelModule {
        @Provides
        @ObjectGraphServiceTest.Bagel
        String provideBagel() {
            return ObjectGraphServiceTest.Bagel.class.getName();
        }
    }

    @Module(injects = ObjectGraphServiceTest.HasCarrot.class)
    class CarrotModule {
        @Provides
        @ObjectGraphServiceTest.Carrot
        String provideCharlie() {
            return ObjectGraphServiceTest.Carrot.class.getName();
        }
    }

    @Module(injects = ObjectGraphServiceTest.HasDogfood.class)
    class DogfoodModule {
        @Provides
        @ObjectGraphServiceTest.Dogfood
        String provideDogfood() {
            return ObjectGraphServiceTest.Dogfood.class.getName();
        }
    }

    @Module(injects = ObjectGraphServiceTest.HasEggplant.class)
    class EggplanModule {
        @Provides
        @ObjectGraphServiceTest.Eggplant
        String provideEggplant() {
            return ObjectGraphServiceTest.Eggplant.class.getName();
        }
    }

    static class HasApple {
        @Inject
        @ObjectGraphServiceTest.Apple
        String string;
    }

    static class HasBagel {
        @Inject
        @ObjectGraphServiceTest.Bagel
        String string;
    }

    static class HasCarrot {
        @Inject
        @ObjectGraphServiceTest.Carrot
        String string;
    }

    static class HasDogfood {
        @Inject
        @ObjectGraphServiceTest.Dogfood
        String string;
    }

    static class HasEggplant {
        @Inject
        @ObjectGraphServiceTest.Eggplant
        String string;
    }

    @Test
    public void createMortarScopeUsesModules() {
        MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule(), new ObjectGraphServiceTest.BagelModule()));
        ObjectGraph objectGraph = getObjectGraph(scope);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasBagel.class).string).isEqualTo(ObjectGraphServiceTest.Bagel.class.getName());
        try {
            objectGraph.get(ObjectGraphServiceTest.HasCarrot.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void destroyRoot() {
        MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        scope.register(scoped);
        scope.destroy();
        Mockito.verify(scoped).onExitScope();
    }

    @Test
    public void activityScopeName() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        String name = ObjectGraphServiceTest.Bagel.class.getName();
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build(name);
        assertThat(activityScope.getName()).isEqualTo(name);
        assertThat(root.findChild(name)).isSameAs(activityScope);
        assertThat(root.findChild("herman")).isNull();
    }

    @Test
    public void getActivityScopeWithOneModule() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        String name = ObjectGraphServiceTest.Bagel.class.getName();
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build(name);
        ObjectGraph objectGraph = getObjectGraph(activityScope);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasBagel.class).string).isEqualTo(ObjectGraphServiceTest.Bagel.class.getName());
        try {
            objectGraph.get(ObjectGraphServiceTest.HasCarrot.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void getActivityScopeWithMoreModules() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.DogfoodModule(), new ObjectGraphServiceTest.EggplanModule())).build("moar");
        ObjectGraph objectGraph = getObjectGraph(activityScope);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasDogfood.class).string).isEqualTo(ObjectGraphServiceTest.Dogfood.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasEggplant.class).string).isEqualTo(ObjectGraphServiceTest.Eggplant.class.getName());
        try {
            objectGraph.get(ObjectGraphServiceTest.HasCarrot.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void destroyActivityScopeDirect() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        String name = ObjectGraphServiceTest.Bagel.class.getName();
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build(name);
        activityScope.register(scoped);
        activityScope.destroy();
        Mockito.verify(scoped).onExitScope();
        assertThat(root.findChild(name)).isNull();
    }

    @Test
    public void destroyActivityScopeRecursive() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        activityScope.register(scoped);
        root.destroy();
        Mockito.verify(scoped).onExitScope();
        try {
            getObjectGraph(activityScope);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void activityChildScopeName() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        String childScopeName = ObjectGraphServiceTest.Carrot.class.getName();
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build(childScopeName);
        assertThat(child.getName()).isEqualTo(childScopeName);
        assertThat(activityScope.findChild(childScopeName)).isSameAs(child);
        assertThat(activityScope.findChild("herman")).isNull();
    }

    @Test
    public void requireGrandchildWithOneModule() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        MortarScope grandchild = child.buildChild().withService(SERVICE_NAME, create(child, new ObjectGraphServiceTest.DogfoodModule())).build("grandchild");
        ObjectGraph objectGraph = getObjectGraph(grandchild);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasBagel.class).string).isEqualTo(ObjectGraphServiceTest.Bagel.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasCarrot.class).string).isEqualTo(ObjectGraphServiceTest.Carrot.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasDogfood.class).string).isEqualTo(ObjectGraphServiceTest.Dogfood.class.getName());
        try {
            objectGraph.get(ObjectGraphServiceTest.HasEggplant.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void requireGrandchildWithMoreModules() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        MortarScope grandchild = child.buildChild().withService(SERVICE_NAME, create(child, new ObjectGraphServiceTest.DogfoodModule(), new ObjectGraphServiceTest.EggplanModule())).build("grandchild");
        ObjectGraph objectGraph = getObjectGraph(grandchild);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasBagel.class).string).isEqualTo(ObjectGraphServiceTest.Bagel.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasCarrot.class).string).isEqualTo(ObjectGraphServiceTest.Carrot.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasDogfood.class).string).isEqualTo(ObjectGraphServiceTest.Dogfood.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasEggplant.class).string).isEqualTo(ObjectGraphServiceTest.Eggplant.class.getName());
        try {
            objectGraph.get(String.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void requireGrandchildWithNoModules() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        MortarScope grandchild = child.buildChild().build("grandchild");
        ObjectGraph objectGraph = getObjectGraph(grandchild);
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasApple.class).string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasBagel.class).string).isEqualTo(ObjectGraphServiceTest.Bagel.class.getName());
        assertThat(objectGraph.get(ObjectGraphServiceTest.HasCarrot.class).string).isEqualTo(ObjectGraphServiceTest.Carrot.class.getName());
        try {
            objectGraph.get(String.class);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void destroyActivityChildScopeDirect() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        assertThat(activityScope.findChild("child")).isSameAs(child);
        child.register(scoped);
        child.destroy();
        Mockito.verify(scoped).onExitScope();
        assertThat(activityScope.findChild("child")).isNull();
    }

    @Test
    public void destroyActivityChildScopeRecursive() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        assertThat(activityScope.findChild("child")).isSameAs(child);
        child.register(scoped);
        root.destroy();
        Mockito.verify(scoped).onExitScope();
        try {
            getObjectGraph(child);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void activityGrandchildScopeName() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        MortarScope activityScope = root.buildChild().withService(SERVICE_NAME, create(root, new ObjectGraphServiceTest.BagelModule())).build("activity");
        MortarScope child = activityScope.buildChild().withService(SERVICE_NAME, create(activityScope, new ObjectGraphServiceTest.CarrotModule())).build("child");
        MortarScope grandchild = child.buildChild().withService(SERVICE_NAME, create(child, new ObjectGraphServiceTest.DogfoodModule())).build("grandchild");
        assertThat(grandchild.getName()).isEqualTo("grandchild");
        assertThat(child.findChild("grandchild")).isSameAs(grandchild);
        assertThat(child.findChild("herman")).isNull();
    }

    @Test
    public void handlesRecursiveDestroy() {
        final AtomicInteger i = new AtomicInteger(0);
        final MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        scope.register(new Scoped() {
            @Override
            public void onEnterScope(MortarScope scope) {
            }

            @Override
            public void onExitScope() {
                i.incrementAndGet();
                scope.destroy();
            }
        });
        scope.destroy();
        assertThat(i.get()).isEqualTo(1);
    }

    @Test
    public void inject() {
        final MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        Mockito.when(context.getSystemService(ArgumentMatchers.any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return root.getService(((String) (invocation.getArguments()[0])));
            }
        });
        ObjectGraphServiceTest.HasApple apple = new ObjectGraphServiceTest.HasApple();
        ObjectGraphService.inject(context, apple);
        assertThat(apple.string).isEqualTo(ObjectGraphServiceTest.Apple.class.getName());
    }

    @Test
    public void getScope() {
        MortarScope root = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        ObjectGraphServiceTest.wireScopeToMockContext(root, context);
        assertThat(MortarScope.getScope(context)).isSameAs(root);
    }

    @Test
    public void canGetNameFromDestroyed() {
        MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        String name = scope.getName();
        assertThat(name).isNotNull();
        scope.destroy();
        assertThat(scope.getName()).isEqualTo(name);
    }

    @Test
    public void cannotGetObjectGraphFromDestroyed() {
        MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        scope.destroy();
        try {
            getObjectGraph(scope);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void cannotGetObjectGraphFromContextOfDestroyed() {
        MortarScope scope = ObjectGraphServiceTest.createRootScope(ObjectGraph.create(new ObjectGraphServiceTest.AppleModule()));
        Context context = ObjectGraphServiceTest.mockContext(scope);
        scope.destroy();
        try {
            getObjectGraph(context);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }
    }
}

