package it.cosenonjaviste.daggermock.modulemethodsvisibility;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class StaticMethodNotMockedTest {
    @Rule
    public final DaggerMockRule<StaticMethodNotMockedTest.MyComponent> rule = new DaggerMockRule(StaticMethodNotMockedTest.MyComponent.class, new StaticMethodNotMockedTest.MyModule());

    @Mock
    StaticMethodNotMockedTest.MyOtherService myOtherService;

    @Test
    public void testErrorOnStaticNotMockedMethods() throws Throwable {
        Assert.assertNotNull(myOtherService);
    }

    @Module
    public static class MyModule {
        @Provides
        public StaticMethodNotMockedTest.MyOtherService provideMyOtherService(MyService myService) {
            return new StaticMethodNotMockedTest.MyOtherService(myService);
        }

        @Provides
        public static MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = StaticMethodNotMockedTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }

    public static class MyOtherService {
        public MyOtherService(MyService myService) {
        }
    }
}

