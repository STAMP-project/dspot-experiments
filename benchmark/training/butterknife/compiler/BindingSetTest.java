package butterknife.compiler;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;


public class BindingSetTest {
    @Test
    public void humanDescriptionJoinWorks() {
        MemberViewBinding one = new BindingSetTest.TestViewBinding("one");
        MemberViewBinding two = new BindingSetTest.TestViewBinding("two");
        MemberViewBinding three = new BindingSetTest.TestViewBinding("three");
        String result1 = BindingSet.asHumanDescription(Collections.singletonList(one));
        assertThat(result1).isEqualTo("one");
        String result2 = BindingSet.asHumanDescription(Arrays.asList(one, two));
        assertThat(result2).isEqualTo("one and two");
        String result3 = BindingSet.asHumanDescription(Arrays.asList(one, two, three));
        assertThat(result3).isEqualTo("one, two, and three");
    }

    private static class TestViewBinding implements MemberViewBinding {
        private final String description;

        private TestViewBinding(String description) {
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}

