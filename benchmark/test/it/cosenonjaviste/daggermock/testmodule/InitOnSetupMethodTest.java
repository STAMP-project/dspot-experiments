package it.cosenonjaviste.daggermock.testmodule;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Copyright ? 2017 Orion Health. All rights reserved.
 */
@RunWith(JUnit4.class)
public class InitOnSetupMethodTest {
    private final DaggerMockRule<MyComponent> daggerMock = new DaggerMockRule(MyComponent.class, new TestModule()).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            it.cosenonjaviste.daggermock.testmodule.mainService = component.mainService();
        }
    });

    @Mock
    MyService myService;

    private MainService mainService;

    @Test
    public void initMocksOnSetupTest() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
    }
}

