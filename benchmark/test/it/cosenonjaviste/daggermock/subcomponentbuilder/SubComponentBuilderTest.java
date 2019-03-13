package it.cosenonjaviste.daggermock.subcomponentbuilder;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Rule;
import org.junit.Test;


public class SubComponentBuilderTest {
    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule()).set(new DaggerMockRule.ComponentSetter<MyComponent>() {
        @Override
        public void setComponent(MyComponent component) {
            it.cosenonjaviste.daggermock.subcomponentbuilder.mainService = component.plus().mySubModule(new MySubModule()).build().mainService();
        }
    });

    String s = "BBBB";

    Integer i = 1;

    private MainService mainService;

    @Test
    public void testSubComponentNoDaggerMock() {
        MyComponent component = DaggerMyComponent.builder().build();
        MainService service = component.plus().mySubModule(new MySubModule()).build().mainService();
        assertThat(service.getString()).isEqualTo("AAAA12345");
    }

    @Test
    public void testSubComponentWithDaggerMock() {
        assertThat(mainService.getString()).isEqualTo("BBBB1");
    }
}

