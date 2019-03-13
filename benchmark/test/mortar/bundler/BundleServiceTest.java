package mortar.bundler;


import android.content.Context;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import mortar.MortarScope;
import mortar.Scoped;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Robolectric allows us to use Bundles.
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BundleServiceTest extends TestCase {
    @Mock
    Scoped scoped;

    private MortarScope activityScope;

    @Test(expected = IllegalArgumentException.class)
    public void nonNullKeyRequired() {
        BundleService.getBundleService(activityScope).register(Mockito.mock(Bundler.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyKeyRequired() {
        Bundler mock = Mockito.mock(Bundler.class);
        Mockito.when(mock.getMortarBundleKey()).thenReturn("");
        BundleService.getBundleService(activityScope).register(mock);
    }

    @Test
    public void lifeCycle() {
        doLifecycleTest(activityScope);
    }

    @Test
    public void childLifeCycle() {
        doLifecycleTest(activityScope.buildChild().build("child"));
    }

    @Test
    public void cannotGetBundleServiceRunnerFromDestroyed() {
        activityScope.destroy();
        IllegalStateException caught = null;
        try {
            BundleServiceRunner.getBundleServiceRunner(activityScope);
        } catch (IllegalStateException e) {
            caught = e;
        }
        assertThat(caught).isNotNull();
    }

    @Test
    public void cannotGetBundleServiceRunnerFromContextOfDestroyed() {
        Context activity = BundleServiceTest.mockContext(activityScope);
        activityScope.destroy();
        IllegalStateException caught = null;
        try {
            BundleServiceRunner.getBundleServiceRunner(activity);
        } catch (IllegalStateException e) {
            caught = e;
        }
        assertThat(caught).isNotNull();
    }

    @Test
    public void cannotGetBundleServiceForDestroyed() {
        MortarScope child = activityScope.buildChild().build("child");
        child.destroy();
        IllegalStateException caught = null;
        try {
            BundleService.getBundleService(child);
        } catch (IllegalStateException e) {
            caught = e;
        }
        assertThat(caught).isNotNull();
    }

    @Test
    public void cannotGetBundleServiceFromContextOfDestroyed() {
        MortarScope child = activityScope.buildChild().build("child");
        Context context = BundleServiceTest.mockContext(child);
        child.destroy();
        IllegalStateException caught = null;
        try {
            BundleService.getBundleService(context);
        } catch (IllegalStateException e) {
            caught = e;
        }
        assertThat(caught).isNotNull();
    }

    @Test
    public void onRegisteredIsDebounced() {
        activityScope.register(scoped);
        activityScope.register(scoped);
        Mockito.verify(scoped, Mockito.times(1)).onEnterScope(activityScope);
    }

    @Test
    public void childInfoSurvivesProcessDeath() {
        BundleServiceTest.FauxActivity activity = new BundleServiceTest.FauxActivity();
        activity.create(null);
        Bundle bundle = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(bundle);
        // Process death: new copy of the bundle, new scope and activity instances
        bundle = new Bundle(bundle);
        // Activity scopes often include transient values like task id. Make sure
        // BundlerServiceRunner isn't stymied by that.
        newProcess("anotherActivity");
        activity = new BundleServiceTest.FauxActivity();
        activity.create(bundle);
        assertThat(activity.rootBundler.lastLoaded).isNotNull();
        assertThat(activity.childBundler.lastLoaded).isNotNull();
    }

    @Test
    public void handlesRegisterFromOnLoadBeforeCreate() {
        final BundleServiceTest.MyBundler bundler = new BundleServiceTest.MyBundler("inner");
        BundleService.getBundleService(activityScope).register(new BundleServiceTest.MyBundler("outer") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                BundleService.getBundleService(activityScope).register(bundler);
            }
        });
        // The recursive register call loaded immediately.
        assertThat(bundler.loaded).isTrue();
        // And it was registered: a create call reloads it.
        bundler.reset();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(null);
        assertThat(bundler.loaded).isTrue();
    }

    @Test
    public void handlesRegisterFromOnLoadAfterCreate() {
        final BundleServiceTest.MyBundler bundler = new BundleServiceTest.MyBundler("inner");
        BundleServiceRunner bundleServiceRunner = BundleServiceRunner.getBundleServiceRunner(activityScope);
        bundleServiceRunner.onCreate(null);
        final BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new BundleServiceTest.MyBundler("outer") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                bundleService.register(bundler);
            }
        });
        // The recursive register call loaded immediately.
        assertThat(bundler.loaded).isTrue();
        // And it was registered: the next create call reloads it.
        bundler.reset();
        Bundle b = new Bundle();
        bundleServiceRunner.onSaveInstanceState(b);
        bundleServiceRunner.onCreate(b);
        assertThat(bundler.loaded).isNotNull();
    }

    @Test
    public void cannotRegisterDuringOnSave() {
        final BundleServiceTest.MyBundler bundler = new BundleServiceTest.MyBundler("inner");
        final AtomicBoolean caught = new AtomicBoolean(false);
        BundleServiceRunner bundleServiceRunner = BundleServiceRunner.getBundleServiceRunner(activityScope);
        bundleServiceRunner.onCreate(null);
        final BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new BundleServiceTest.MyBundler("outer") {
            @Override
            public void onSave(Bundle outState) {
                super.onSave(outState);
                try {
                    bundleService.register(bundler);
                } catch (IllegalStateException e) {
                    caught.set(true);
                }
            }
        });
        assertThat(bundler.loaded).isFalse();
        Bundle bundle = new Bundle();
        bundleServiceRunner.onSaveInstanceState(bundle);
        assertThat(caught.get()).isTrue();
    }

    @Test
    public void handlesReregistrationBeforeCreate() {
        final AtomicInteger i = new AtomicInteger(0);
        final BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new Bundler() {
            @Override
            public String getMortarBundleKey() {
                return "key";
            }

            @Override
            public void onEnterScope(MortarScope scope) {
            }

            @Override
            public void onLoad(Bundle savedInstanceState) {
                if ((i.incrementAndGet()) < 1)
                    bundleService.register(this);

            }

            @Override
            public void onSave(Bundle outState) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onExitScope() {
                throw new UnsupportedOperationException();
            }
        });
        Bundle b = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(b);
        assertThat(i.get()).isEqualTo(2);
    }

    @Test
    public void handlesReregistrationAfterCreate() {
        Bundle b = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(b);
        final AtomicInteger i = new AtomicInteger(0);
        final BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new Bundler() {
            @Override
            public String getMortarBundleKey() {
                return "key";
            }

            @Override
            public void onEnterScope(MortarScope scope) {
            }

            @Override
            public void onLoad(Bundle savedInstanceState) {
                if ((i.incrementAndGet()) < 1)
                    bundleService.register(this);

            }

            @Override
            public void onSave(Bundle outState) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onExitScope() {
                throw new UnsupportedOperationException();
            }
        });
        assertThat(i.get()).isEqualTo(1);
    }

    @Test
    public void handleDestroyFromEarlyLoad() {
        final AtomicInteger loads = new AtomicInteger(0);
        final AtomicInteger destroys = new AtomicInteger(0);
        class Destroyer implements Bundler {
            @Override
            public String getMortarBundleKey() {
                return "k";
            }

            @Override
            public void onEnterScope(MortarScope scope) {
            }

            @Override
            public void onLoad(Bundle savedInstanceState) {
                if ((loads.incrementAndGet()) > 2) {
                    activityScope.destroy();
                }
            }

            @Override
            public void onSave(Bundle outState) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onExitScope() {
                destroys.incrementAndGet();
            }
        }
        BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new Destroyer());
        bundleService.register(new Destroyer());
        Bundle b = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(b);
        assertThat(loads.get()).isEqualTo(3);
        assertThat(destroys.get()).isEqualTo(2);
    }

    @Test
    public void handlesDestroyFromOnSave() {
        final AtomicInteger saves = new AtomicInteger(0);
        final AtomicInteger destroys = new AtomicInteger(0);
        class Destroyer implements Bundler {
            @Override
            public String getMortarBundleKey() {
                return "k";
            }

            @Override
            public void onEnterScope(MortarScope scope) {
            }

            @Override
            public void onLoad(Bundle savedInstanceState) {
            }

            @Override
            public void onSave(Bundle outState) {
                saves.incrementAndGet();
                activityScope.destroy();
            }

            @Override
            public void onExitScope() {
                destroys.incrementAndGet();
            }
        }
        BundleService bundleService = BundleService.getBundleService(activityScope);
        bundleService.register(new Destroyer());
        bundleService.register(new Destroyer());
        Bundle b = new Bundle();
        BundleServiceRunner bundleServiceRunner = BundleServiceRunner.getBundleServiceRunner(activityScope);
        bundleServiceRunner.onCreate(b);
        bundleServiceRunner.onSaveInstanceState(b);
        assertThat(destroys.get()).isEqualTo(2);
        assertThat(saves.get()).isEqualTo(1);
    }

    @Test
    public void deliversStateToBundlerWhenRegisterAfterOnCreate() {
        class SavesAndRestores extends BundleServiceTest.MyBundler {
            SavesAndRestores() {
                super("sNr");
            }

            boolean restored;

            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                restored = (savedInstanceState != null) && (savedInstanceState.getBoolean("fred"));
            }

            @Override
            public void onSave(Bundle outState) {
                super.onSave(outState);
                outState.putBoolean("fred", true);
            }
        }
        class Top extends BundleServiceTest.MyBundler {
            Top() {
                super("top");
            }

            final SavesAndRestores child = new SavesAndRestores();

            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                MortarScope childScope = activityScope.buildChild().build("child");
                BundleService.getBundleService(childScope).register(child);
            }
        }
        Top originalTop = new Top();
        BundleService.getBundleService(activityScope).register(originalTop);
        assertThat(originalTop.child.restored).isFalse();
        Bundle bundle = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(bundle);
        newProcess();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(bundle);
        Top newTop = new Top();
        BundleService.getBundleService(activityScope).register(newTop);
        assertThat(newTop.child.restored).isTrue();
    }

    /**
     * <a href="https://github.com/square/mortar/issues/46">Issue 46</a>
     */
    @Test
    public void registerWithDescendantScopesCreatedDuringParentOnCreateGetOnlyOneOnLoadCall() {
        final BundleServiceTest.MyBundler childBundler = new BundleServiceTest.MyBundler("child");
        final BundleServiceTest.MyBundler grandChildBundler = new BundleServiceTest.MyBundler("grandChild");
        final AtomicBoolean spawnSubScope = new AtomicBoolean(false);
        BundleService.getBundleService(activityScope).register(new BundleServiceTest.MyBundler("outer") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                if (spawnSubScope.get()) {
                    MortarScope childScope = activityScope.buildChild().build("child scope");
                    BundleService.getBundleService(childScope).register(childBundler);
                    // 1. We're in the middle of loading, so the usual register > load call doesn't happen.
                    assertThat(childBundler.loaded).isFalse();
                    MortarScope grandchildScope = childScope.buildChild().build("grandchild scope");
                    BundleService.getBundleService(grandchildScope).register(grandChildBundler);
                    assertThat(grandChildBundler.loaded).isFalse();
                }
            }
        });
        spawnSubScope.set(true);
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(null);
        // 2. But load is called before the onCreate chain ends.
        assertThat(childBundler.loaded).isTrue();
        assertThat(grandChildBundler.loaded).isTrue();
    }

    /**
     * Happened during first naive fix of
     * <a href="https://github.com/square/mortar/issues/46">Issue 46</a>.
     */
    @Test
    public void descendantScopesCreatedDuringParentOnLoadAreNotStuckInLoadingMode() {
        BundleService.getBundleService(activityScope).register(new BundleServiceTest.MyBundler("outer") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                MortarScope child = activityScope.buildChild().build("subscope");
                child.buildChild().build("subsubscope");
            }
        });
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(new Bundle());
        // No crash? Victoire!
    }

    /**
     * https://github.com/square/mortar/issues/77
     */
    @Test
    public void childCreatedDuringMyLoadDoesLoadingAfterMe() {
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(null);
        final BundleServiceTest.MyBundler childBundler = new BundleServiceTest.MyBundler("childBundler");
        BundleService.getBundleService(activityScope).register(new BundleServiceTest.MyBundler("root") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                MortarScope childScope = activityScope.buildChild().build("childScope");
                BundleService.getBundleService(childScope).register(childBundler);
                assertThat(childBundler.loaded).isFalse();
            }
        });
        assertThat(childBundler.loaded).isTrue();
    }

    /**
     * https://github.com/square/mortar/issues/77
     */
    @Test
    public void bundlersInChildScopesLoadAfterBundlersOnParent() {
        final List<Bundler> loadingOrder = new ArrayList<>();
        // rootBundler#onLoad creates a child scope and registers childBundler on it,
        // and after that registers a serviceBundler on the higher level
        // activity scope. The service must receive onLoad before the child does.
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(null);
        final BundleServiceTest.MyBundler serviceOnActivityScope = new BundleServiceTest.MyBundler("service") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                loadingOrder.add(this);
            }
        };
        final BundleServiceTest.MyBundler childBundler = new BundleServiceTest.MyBundler("childBundler") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                super.onLoad(savedInstanceState);
                loadingOrder.add(this);
            }
        };
        BundleServiceTest.MyBundler rootBundler = new BundleServiceTest.MyBundler("root") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                loadingOrder.add(this);
                MortarScope childScope = activityScope.buildChild().build("childScope");
                BundleService.getBundleService(childScope).register(childBundler);
                BundleService.getBundleService(activityScope).register(serviceOnActivityScope);
            }
        };
        BundleService.getBundleService(activityScope).register(rootBundler);
        assertThat(loadingOrder.size()).isEqualTo(3);
        assertThat(loadingOrder.get(0)).isSameAs(rootBundler);
        assertThat(loadingOrder.get(1)).isSameAs(serviceOnActivityScope);
        assertThat(loadingOrder.get(2)).isSameAs(childBundler);
    }

    /**
     * https://github.com/square/mortar/issues/131
     */
    @Test
    public void destroyingWhileSaving() {
        final MortarScope[] currentScreen = new MortarScope[]{ null };
        MortarScope screenSwapperScope = activityScope.buildChild().build("screenOne");
        BundleService.getBundleService(screenSwapperScope).register(new BundleServiceTest.MyBundler("screenSwapper") {
            @Override
            public void onSave(Bundle outState) {
                currentScreen[0].destroy();
            }
        });
        final MortarScope screenOneScope = screenSwapperScope.buildChild().build("screenOne");
        BundleService.getBundleService(screenOneScope).register(new BundleServiceTest.MyBundler("bundlerOne"));
        currentScreen[0] = screenOneScope;
        final MortarScope screenTwoScope = screenSwapperScope.buildChild().build("screenTwo");
        BundleService.getBundleService(screenTwoScope).register(new BundleServiceTest.MyBundler("bundlerTwo"));
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(new Bundle());
    }

    // Make sure that when a scope dies, a new scope with the same name doesn't
    // accidentally receive the old one's bundle.
    @Test
    public void endScopeEndBundle() {
        BundleServiceTest.MyBundler fooBundler = new BundleServiceTest.MyBundler("fooBundler") {
            @Override
            public void onLoad(Bundle savedInstanceState) {
                assertThat(savedInstanceState).isNull();
            }

            @Override
            public void onSave(Bundle outState) {
                outState.putString("baz", "bang");
            }
        };
        // First visit to the foo screen, bundle will be null.
        MortarScope fooScope = activityScope.buildChild().build("fooScope");
        BundleService.getBundleService(fooScope).register(fooBundler);
        // Android saves state
        Bundle state = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(state);
        // We leave the foo screen.
        fooScope.destroy();
        // And now we come back to it. New instance's onLoad should also get a null bundle.
        fooScope = activityScope.buildChild().build("fooScope");
        BundleService.getBundleService(fooScope).register(fooBundler);
    }

    class FauxActivity {
        final BundleServiceTest.MyBundler rootBundler = new BundleServiceTest.MyBundler("core");

        MortarScope childScope;

        BundleServiceTest.MyBundler childBundler = new BundleServiceTest.MyBundler("child");

        void create(Bundle bundle) {
            BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(bundle);
            BundleService.getBundleService(activityScope).register(rootBundler);
            childScope = activityScope.buildChild().build("child");
            BundleService.getBundleService(childScope).register(childBundler);
        }
    }

    private static class MyBundler implements Bundler {
        final String name;

        MortarScope registered;

        boolean loaded;

        Bundle lastLoaded;

        Bundle lastSaved;

        boolean destroyed;

        public MyBundler(String name) {
            this.name = name;
        }

        void reset() {
            lastSaved = lastLoaded = null;
            loaded = destroyed = false;
        }

        @Override
        public String getMortarBundleKey() {
            return name;
        }

        @Override
        public void onEnterScope(MortarScope scope) {
            this.registered = scope;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            loaded = true;
            lastLoaded = savedInstanceState;
            if (savedInstanceState != null) {
                assertThat(savedInstanceState.get("key")).isEqualTo(name);
            }
        }

        @Override
        public void onSave(Bundle outState) {
            lastSaved = outState;
            outState.putString("key", name);
        }

        @Override
        public void onExitScope() {
            destroyed = true;
        }
    }
}

