package it.cosenonjaviste.daggermock.subcomponent;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Rule;
import org.junit.Test;


public class SubComponentTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            it.cosenonjaviste.daggermock.subcomponent.mainService = component.mySubComponent(new MySubModule()).mainService();
        }
    });

    String s = "BBBB";

    Integer i = 1;

    private MainService mainService;

    @Test
    public void testSubComponentNoDaggerMock() {
        MyComponent component = DaggerMyComponent.builder().build();
        MainService service = component.mySubComponent(new MySubModule()).mainService();
        assertThat(service.getString()).isEqualTo("AAAA12345");
    }

    @Test
    public void testSubComponentWithDaggerMock() {
        assertThat(mainService.getString()).isEqualTo("BBBB1");
    }
}

