package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ResourceIdGeneratorTest {
    @Test
    public void shouldGenerateUniqueId() {
        ResourceIdGenerator generator = new ResourceIdGenerator(127);
        generator.record(2130771969, "string", "some_name");
        generator.record(2130771970, "string", "another_name");
        assertThat(generator.generate("string", "next_name")).isEqualTo(2130771971);
    }

    @Test
    public void shouldIdForUnseenType() {
        ResourceIdGenerator generator = new ResourceIdGenerator(127);
        generator.record(2130771969, "string", "some_name");
        generator.record(2130771970, "string", "another_name");
        assertThat(generator.generate("int", "int_name")).isEqualTo(2130837505);
    }
}

