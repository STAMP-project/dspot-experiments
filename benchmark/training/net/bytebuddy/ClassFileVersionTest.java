package net.bytebuddy;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.ClassFileVersion.VersionLocator.ForLegacyVm.INSTANCE;


public class ClassFileVersionTest {
    @Test
    public void testExplicitConstructionOfUnknownVersion() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofMinorMajor(((V13) + 1)).getMinorMajorVersion(), CoreMatchers.is(((V13) + 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalVersion() throws Exception {
        ClassFileVersion.ofMinorMajor(ClassFileVersion.BASE_VERSION);
    }

    @Test
    public void testComparison() throws Exception {
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_1).compareTo(new ClassFileVersion(Opcodes.V1_1)), CoreMatchers.is(0));
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_1).compareTo(new ClassFileVersion(Opcodes.V1_2)), CoreMatchers.is((-1)));
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_1)), CoreMatchers.is(1));
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_2)), CoreMatchers.is(0));
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_3).compareTo(new ClassFileVersion(Opcodes.V1_2)), CoreMatchers.is(1));
        MatcherAssert.assertThat(new ClassFileVersion(Opcodes.V1_2).compareTo(new ClassFileVersion(Opcodes.V1_3)), CoreMatchers.is((-1)));
    }

    @Test
    public void testVersionPropertyAction() throws Exception {
        MatcherAssert.assertThat(INSTANCE.run(), CoreMatchers.is(System.getProperty("java.version")));
    }

    @Test
    public void testVersionOfClass() throws Exception {
        MatcherAssert.assertThat(((ClassFileVersion.of(ClassFileVersionTest.Foo.class).compareTo(ClassFileVersion.ofThisVm())) < 1), CoreMatchers.is(true));
    }

    /* empty */
    private static class Foo {}
}

