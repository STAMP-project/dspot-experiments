package it.cosenonjaviste.daggermock.modulemethodsvisibility;


import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class StaticMethodTest {
    @Mock
    MyService myService;

    @Test
    public void testErrorOnStaticMethods() throws Throwable {
        try {
            DaggerMockRule<StaticMethodTest.MyComponent> rule = new DaggerMockRule(StaticMethodTest.MyComponent.class, new StaticMethodTest.MyModule());
            rule.apply(null, null, this).evaluate();
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo(("The following methods must be non static:\n" + "public static it.cosenonjaviste.daggermock.modulemethodsvisibility.MyService it.cosenonjaviste.daggermock.modulemethodsvisibility.StaticMethodTest$MyModule.provideMyService()"));
        }
    }

    @Module
    public static class MyModule {
        @Provides
        public static MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = StaticMethodTest.MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}

