package jadx.gui.treemodel;


import jadx.api.JadxDecompiler;
import jadx.api.JavaPackage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JSourcesTest {
    private JSources sources;

    private JadxDecompiler decompiler;

    @Test
    public void testHierarchyPackages() {
        String pkgName = "a.b.c.d.e";
        List<JavaPackage> packages = Collections.singletonList(newPkg(pkgName));
        List<JPackage> out = sources.getHierarchyPackages(packages);
        Assert.assertThat(out, Matchers.hasSize(1));
        JPackage jPkg = out.get(0);
        Assert.assertThat(jPkg.getName(), Matchers.is(pkgName));
        Assert.assertThat(jPkg.getClasses(), Matchers.hasSize(1));
    }

    @Test
    public void testHierarchyPackages2() {
        List<JavaPackage> packages = Arrays.asList(newPkg("a.b"), newPkg("a.c"), newPkg("a.d"));
        List<JPackage> out = sources.getHierarchyPackages(packages);
        Assert.assertThat(out, Matchers.hasSize(1));
        JPackage jPkg = out.get(0);
        Assert.assertThat(jPkg.getName(), Matchers.is("a"));
        Assert.assertThat(jPkg.getClasses(), Matchers.hasSize(0));
        Assert.assertThat(jPkg.getInnerPackages(), Matchers.hasSize(3));
    }

    @Test
    public void testHierarchyPackages3() {
        List<JavaPackage> packages = Arrays.asList(newPkg("a.b.p1"), newPkg("a.b.p2"), newPkg("a.b.p3"));
        List<JPackage> out = sources.getHierarchyPackages(packages);
        Assert.assertThat(out, Matchers.hasSize(1));
        JPackage jPkg = out.get(0);
        Assert.assertThat(jPkg.getName(), Matchers.is("a.b"));
        Assert.assertThat(jPkg.getClasses(), Matchers.hasSize(0));
        Assert.assertThat(jPkg.getInnerPackages(), Matchers.hasSize(3));
    }

    @Test
    public void testHierarchyPackages4() {
        List<JavaPackage> packages = Arrays.asList(newPkg("a.p1"), newPkg("a.b.c.p2"), newPkg("a.b.c.p3"), newPkg("d.e"), newPkg("d.f.a"));
        List<JPackage> out = sources.getHierarchyPackages(packages);
        Assert.assertThat(out, Matchers.hasSize(2));
        Assert.assertThat(out.get(0).getName(), Matchers.is("a"));
        Assert.assertThat(out.get(0).getInnerPackages(), Matchers.hasSize(2));
        Assert.assertThat(out.get(1).getName(), Matchers.is("d"));
        Assert.assertThat(out.get(1).getInnerPackages(), Matchers.hasSize(2));
    }
}

