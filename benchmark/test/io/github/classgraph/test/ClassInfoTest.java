package io.github.classgraph.test;


import io.github.classgraph.ScanResult;
import io.github.classgraph.test.whitelisted.ClsSub;
import io.github.classgraph.test.whitelisted.ClsSubSub;
import io.github.classgraph.test.whitelisted.Iface;
import io.github.classgraph.test.whitelisted.IfaceSub;
import io.github.classgraph.test.whitelisted.IfaceSubSub;
import io.github.classgraph.test.whitelisted.Impl1;
import io.github.classgraph.test.whitelisted.Impl1Sub;
import io.github.classgraph.test.whitelisted.Impl1SubSub;
import io.github.classgraph.test.whitelisted.Impl2;
import io.github.classgraph.test.whitelisted.Impl2Sub;
import io.github.classgraph.test.whitelisted.Impl2SubSub;
import java.util.List;
import org.junit.Test;


/**
 * The Class ClassInfoTest.
 */
public class ClassInfoTest {
    /**
     * The scan result.
     */
    private static ScanResult scanResult;

    /**
     * Use class name to class info.
     */
    @Test
    public void useClassNameToClassInfo() {
        final List<String> impls = ClassInfoTest.scanResult.getClassInfo(Iface.class.getName()).getClassesImplementing().getNames();
        assertThat(impls.contains(Impl1.class.getName())).isTrue();
    }

    /**
     * Filter.
     */
    @Test
    public void filter() {
        assertThat(ClassInfoTest.scanResult.getAllClasses().filter(new io.github.classgraph.ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(final io.github.classgraph.ClassInfo ci) {
                return ci.getName().contains("ClsSub");
            }
        }).getNames()).containsExactlyInAnyOrder(ClsSub.class.getName(), ClsSubSub.class.getName());
    }

    /**
     * Stream has super interface direct.
     */
    @Test
    public void streamHasSuperInterfaceDirect() {
        assertThat(ClassInfoTest.scanResult.getAllClasses().filter(new io.github.classgraph.ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(final io.github.classgraph.ClassInfo ci) {
                return ci.getInterfaces().directOnly().getNames().contains(Iface.class.getName());
            }
        }).getNames()).containsExactlyInAnyOrder(IfaceSub.class.getName(), Impl2.class.getName());
    }

    /**
     * Stream has super interface.
     */
    @Test
    public void streamHasSuperInterface() {
        assertThat(ClassInfoTest.scanResult.getAllClasses().filter(new io.github.classgraph.ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(final io.github.classgraph.ClassInfo ci) {
                return ci.getInterfaces().getNames().contains(Iface.class.getName());
            }
        }).getNames()).containsExactlyInAnyOrder(IfaceSub.class.getName(), IfaceSubSub.class.getName(), Impl2.class.getName(), Impl2Sub.class.getName(), Impl2SubSub.class.getName(), Impl1.class.getName(), Impl1Sub.class.getName(), Impl1SubSub.class.getName());
    }

    /**
     * Implements interface direct.
     */
    @Test
    public void implementsInterfaceDirect() {
        assertThat(ClassInfoTest.scanResult.getClassesImplementing(Iface.class.getName()).directOnly().getNames()).containsExactlyInAnyOrder(IfaceSub.class.getName(), Impl2.class.getName());
    }

    /**
     * Implements interface.
     */
    @Test
    public void implementsInterface() {
        assertThat(ClassInfoTest.scanResult.getClassesImplementing(Iface.class.getName()).getNames()).containsExactlyInAnyOrder(Impl1.class.getName(), Impl1Sub.class.getName(), Impl1SubSub.class.getName(), Impl2.class.getName(), Impl2Sub.class.getName(), Impl2SubSub.class.getName(), IfaceSub.class.getName(), IfaceSubSub.class.getName());
    }

    /**
     * Multi criteria.
     */
    @Test
    public void multiCriteria() {
        assertThat(ClassInfoTest.scanResult.getAllClasses().filter(new io.github.classgraph.ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(final io.github.classgraph.ClassInfo ci) {
                return (ci.getInterfaces().getNames().contains(Iface.class.getName())) && (ci.getSuperclasses().getNames().contains(Impl1.class.getName()));
            }
        }).getNames()).containsExactlyInAnyOrder(Impl1Sub.class.getName(), Impl1SubSub.class.getName());
    }
}

