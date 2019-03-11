package org.robolectric.res;


import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ResourceRemapperTest {
    @Test(expected = IllegalArgumentException.class)
    public void forbidFinalRClasses() {
        ResourceRemapper remapper = new ResourceRemapper(null);
        remapper.remapRClass(ResourceRemapperTest.FinalRClass.class);
    }

    @SuppressWarnings("TruthConstantAsserts")
    @Test
    public void testRemap() {
        ResourceRemapper remapper = new ResourceRemapper(ResourceRemapperTest.ApplicationRClass.class);
        remapper.remapRClass(ResourceRemapperTest.SecondClass.class);
        remapper.remapRClass(ResourceRemapperTest.ThirdClass.class);
        // Resource identifiers that are common across libraries should be remapped to the same value.
        assertThat(ResourceRemapperTest.ApplicationRClass.string.string_one).isEqualTo(ResourceRemapperTest.SecondClass.string.string_one);
        assertThat(ResourceRemapperTest.ApplicationRClass.string.string_one).isEqualTo(ResourceRemapperTest.ThirdClass.string.string_one);
        // Resource identifiers that clash across two libraries should be remapped to different values.
        assertThat(ResourceRemapperTest.SecondClass.id.id_clash).isNotEqualTo(ResourceRemapperTest.ThirdClass.id.another_id_clash);
        // Styleable arrays of values should be updated to match the remapped values.
        assertThat(ResourceRemapperTest.ThirdClass.styleable.SomeStyleable).isEqualTo(ResourceRemapperTest.ApplicationRClass.styleable.SomeStyleable);
        assertThat(ResourceRemapperTest.SecondClass.styleable.SomeStyleable).isEqualTo(ResourceRemapperTest.ApplicationRClass.styleable.SomeStyleable);
        assertThat(ResourceRemapperTest.ApplicationRClass.styleable.SomeStyleable).asList().containsExactly(ResourceRemapperTest.ApplicationRClass.attr.attr_one, ResourceRemapperTest.ApplicationRClass.attr.attr_two);
    }

    @Test
    public void resourcesOfDifferentTypes_shouldHaveDifferentTypeSpaces() {
        ResourceRemapper remapper = new ResourceRemapper(ResourceRemapperTest.ApplicationRClass.class);
        remapper.remapRClass(ResourceRemapperTest.SecondClass.class);
        remapper.remapRClass(ResourceRemapperTest.ThirdClass.class);
        Set<Integer> allIds = new HashSet<>();
        assertThat(allIds.add(ResourceRemapperTest.ApplicationRClass.string.string_one)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.ApplicationRClass.string.string_two)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.SecondClass.integer.integer_one)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.SecondClass.integer.integer_two)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.SecondClass.string.string_one)).isFalse();
        assertThat(allIds.add(ResourceRemapperTest.SecondClass.string.string_three)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.ThirdClass.raw.raw_one)).isTrue();
        assertThat(allIds.add(ResourceRemapperTest.ThirdClass.raw.raw_two)).isTrue();
        assertThat(ResourceIds.getTypeIdentifier(ResourceRemapperTest.ApplicationRClass.string.string_one)).isEqualTo(ResourceIds.getTypeIdentifier(ResourceRemapperTest.ApplicationRClass.string.string_two));
        assertThat(ResourceIds.getTypeIdentifier(ResourceRemapperTest.ApplicationRClass.string.string_one)).isEqualTo(ResourceIds.getTypeIdentifier(ResourceRemapperTest.SecondClass.string.string_three));
        assertThat(ResourceIds.getTypeIdentifier(ResourceRemapperTest.ApplicationRClass.string.string_two)).isNotEqualTo(ResourceIds.getTypeIdentifier(ResourceRemapperTest.SecondClass.integer.integer_two));
        assertThat(ResourceIds.getTypeIdentifier(ResourceRemapperTest.ThirdClass.raw.raw_two)).isNotEqualTo(ResourceIds.getTypeIdentifier(ResourceRemapperTest.SecondClass.integer.integer_two));
    }

    public static final class FinalRClass {
        public static final class string {
            public static final int a_final_value = 2130837505;

            public static final int another_final_value = 2130837506;
        }
    }

    public static final class ApplicationRClass {
        public static final class string {
            public static final int string_one = 2130771969;

            public static final int string_two = 2130771970;
        }

        public static final class attr {
            public static int attr_one = 2130771976;

            public static int attr_two = 2130771977;
        }

        public static final class styleable {
            public static final int[] SomeStyleable = new int[]{ ResourceRemapperTest.ApplicationRClass.attr.attr_one, ResourceRemapperTest.ApplicationRClass.attr.attr_two };

            public static final int SomeStyleable_offsetX = 0;

            public static final int SomeStyleable_offsetY = 1;
        }
    }

    public static final class SecondClass {
        public static final class id {
            public static int id_clash = 2130771969;
        }

        public static final class integer {
            public static int integer_one = 2130771969;

            public static int integer_two = 2130771970;
        }

        public static final class string {
            public static int string_one = 2130837505;

            public static int string_three = 2130837506;
        }

        public static final class attr {
            public static int attr_one = 2130771969;

            public static int attr_two = 2130771970;
        }

        public static final class styleable {
            public static final int[] SomeStyleable = new int[]{ ResourceRemapperTest.SecondClass.attr.attr_one, ResourceRemapperTest.SecondClass.attr.attr_two };

            public static final int SomeStyleable_offsetX = 0;

            public static final int SomeStyleable_offsetY = 1;
        }
    }

    public static final class ThirdClass {
        public static final class id {
            public static int another_id_clash = 2130771969;
        }

        public static final class raw {
            public static int raw_one = 2130771969;

            public static int raw_two = 2130771970;
        }

        public static final class string {
            public static int string_one = 2130837513;
        }

        public static final class attr {
            public static int attr_one = 2130771971;

            public static int attr_two = 2130771972;
        }

        public static final class styleable {
            public static final int[] SomeStyleable = new int[]{ ResourceRemapperTest.ThirdClass.attr.attr_one, ResourceRemapperTest.ThirdClass.attr.attr_two };

            public static final int SomeStyleable_offsetX = 0;

            public static final int SomeStyleable_offsetY = 1;
        }
    }
}

