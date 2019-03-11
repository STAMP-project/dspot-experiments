package net.bytebuddy.dynamic;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;


public class DynamicTypeDefaultTest {
    private static final String CLASS_FILE_EXTENSION = ".class";

    private static final String FOOBAR = "foo/bar";

    private static final String QUXBAZ = "qux/baz";

    private static final String BARBAZ = "bar/baz";

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String TEMP = "tmp";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private static final byte[] BINARY_FIRST = new byte[]{ 1, 2, 3 };

    private static final byte[] BINARY_SECOND = new byte[]{ 4, 5, 6 };

    private static final byte[] BINARY_THIRD = new byte[]{ 7, 8, 9 };

    @Mock
    private LoadedTypeInitializer mainLoadedTypeInitializer;

    @Mock
    private LoadedTypeInitializer auxiliaryLoadedTypeInitializer;

    @Mock
    private DynamicType auxiliaryType;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription auxiliaryTypeDescription;

    private DynamicType dynamicType;

    @Test
    public void testByteArray() throws Exception {
        MatcherAssert.assertThat(dynamicType.getBytes(), CoreMatchers.is(DynamicTypeDefaultTest.BINARY_FIRST));
    }

    @Test
    public void testTypeDescription() throws Exception {
        MatcherAssert.assertThat(dynamicType.getTypeDescription(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testRawAuxiliaryTypes() throws Exception {
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().get(auxiliaryTypeDescription), CoreMatchers.is(DynamicTypeDefaultTest.BINARY_SECOND));
    }

    @Test
    public void testTypeInitializersNotAlive() throws Exception {
        MatcherAssert.assertThat(dynamicType.hasAliveLoadedTypeInitializers(), CoreMatchers.is(false));
    }

    @Test
    public void testTypeInitializersAliveMain() throws Exception {
        Mockito.when(mainLoadedTypeInitializer.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(dynamicType.hasAliveLoadedTypeInitializers(), CoreMatchers.is(true));
    }

    @Test
    public void testTypeInitializersAliveAuxiliary() throws Exception {
        Mockito.when(auxiliaryLoadedTypeInitializer.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(dynamicType.hasAliveLoadedTypeInitializers(), CoreMatchers.is(true));
    }

    @Test
    public void testTypeInitializers() throws Exception {
        MatcherAssert.assertThat(dynamicType.getLoadedTypeInitializers().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.getLoadedTypeInitializers().get(typeDescription), CoreMatchers.is(mainLoadedTypeInitializer));
        MatcherAssert.assertThat(dynamicType.getLoadedTypeInitializers().get(auxiliaryTypeDescription), CoreMatchers.is(auxiliaryLoadedTypeInitializer));
    }

    @Test
    public void testFileSaving() throws Exception {
        File folder = DynamicTypeDefaultTest.makeTemporaryFolder();
        boolean folderDeletion;
        boolean fileDeletion;
        try {
            Map<TypeDescription, File> files = dynamicType.saveIn(folder);
            MatcherAssert.assertThat(files.size(), CoreMatchers.is(1));
            DynamicTypeDefaultTest.assertFile(files.get(typeDescription), DynamicTypeDefaultTest.BINARY_FIRST);
        } finally {
            folderDeletion = new File(folder, DynamicTypeDefaultTest.FOO).delete();
            fileDeletion = folder.delete();
        }
        MatcherAssert.assertThat(folderDeletion, CoreMatchers.is(true));
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
        Mockito.verify(auxiliaryType).saveIn(folder);
    }

    @Test
    public void testJarCreation() throws Exception {
        File file = File.createTempFile(DynamicTypeDefaultTest.FOO, DynamicTypeDefaultTest.TEMP);
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        boolean fileDeletion;
        try {
            MatcherAssert.assertThat(dynamicType.toJar(file), CoreMatchers.is(file));
            MatcherAssert.assertThat(file.exists(), CoreMatchers.is(true));
            MatcherAssert.assertThat(file.isFile(), CoreMatchers.is(true));
            MatcherAssert.assertThat(((file.length()) > 0L), CoreMatchers.is(true));
            Map<String, byte[]> bytes = new HashMap<String, byte[]>();
            bytes.put(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_FIRST);
            bytes.put(((DynamicTypeDefaultTest.QUXBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_SECOND);
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
            DynamicTypeDefaultTest.assertJarFile(file, manifest, bytes);
        } finally {
            fileDeletion = file.delete();
        }
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
    }

    @Test
    public void testJarWithExplicitManifestCreation() throws Exception {
        File file = File.createTempFile(DynamicTypeDefaultTest.FOO, DynamicTypeDefaultTest.TEMP);
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        boolean fileDeletion;
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(MANIFEST_VERSION, DynamicTypeDefaultTest.BAR);
            MatcherAssert.assertThat(dynamicType.toJar(file, manifest), CoreMatchers.is(file));
            MatcherAssert.assertThat(file.exists(), CoreMatchers.is(true));
            MatcherAssert.assertThat(file.isFile(), CoreMatchers.is(true));
            MatcherAssert.assertThat(((file.length()) > 0L), CoreMatchers.is(true));
            Map<String, byte[]> bytes = new HashMap<String, byte[]>();
            bytes.put(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_FIRST);
            bytes.put(((DynamicTypeDefaultTest.QUXBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_SECOND);
            DynamicTypeDefaultTest.assertJarFile(file, manifest, bytes);
        } finally {
            fileDeletion = file.delete();
        }
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
    }

    @Test
    public void testJarTargetInjection() throws Exception {
        File sourceFile = File.createTempFile(DynamicTypeDefaultTest.BAR, DynamicTypeDefaultTest.TEMP);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, DynamicTypeDefaultTest.BAR);
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(sourceFile), manifest);
        try {
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        File file = File.createTempFile(DynamicTypeDefaultTest.FOO, DynamicTypeDefaultTest.TEMP);
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        boolean fileDeletion;
        try {
            MatcherAssert.assertThat(dynamicType.inject(sourceFile, file), CoreMatchers.is(file));
            MatcherAssert.assertThat(file.exists(), CoreMatchers.is(true));
            MatcherAssert.assertThat(file.isFile(), CoreMatchers.is(true));
            MatcherAssert.assertThat(((file.length()) > 0L), CoreMatchers.is(true));
            Map<String, byte[]> bytes = new HashMap<String, byte[]>();
            bytes.put(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_FIRST);
            bytes.put(((DynamicTypeDefaultTest.QUXBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_SECOND);
            bytes.put(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_THIRD);
            DynamicTypeDefaultTest.assertJarFile(file, manifest, bytes);
        } finally {
            fileDeletion = (file.delete()) & (sourceFile.delete());
        }
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
    }

    @Test
    public void testJarSelfInjection() throws Exception {
        File file = File.createTempFile(DynamicTypeDefaultTest.BAR, DynamicTypeDefaultTest.TEMP);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, DynamicTypeDefaultTest.BAR);
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest);
        try {
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        boolean fileDeletion;
        try {
            MatcherAssert.assertThat(dynamicType.inject(file), CoreMatchers.is(file));
            MatcherAssert.assertThat(file.exists(), CoreMatchers.is(true));
            MatcherAssert.assertThat(file.isFile(), CoreMatchers.is(true));
            MatcherAssert.assertThat(((file.length()) > 0L), CoreMatchers.is(true));
            Map<String, byte[]> bytes = new HashMap<String, byte[]>();
            bytes.put(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_FIRST);
            bytes.put(((DynamicTypeDefaultTest.QUXBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_SECOND);
            bytes.put(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_THIRD);
            DynamicTypeDefaultTest.assertJarFile(file, manifest, bytes);
        } finally {
            fileDeletion = file.delete();
        }
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
    }

    @Test
    public void testJarSelfInjectionWithDuplicateSpecification() throws Exception {
        File file = File.createTempFile(DynamicTypeDefaultTest.BAR, DynamicTypeDefaultTest.TEMP);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, DynamicTypeDefaultTest.BAR);
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest);
        try {
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
            jarOutputStream.putNextEntry(new JarEntry(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION))));
            jarOutputStream.write(DynamicTypeDefaultTest.BINARY_THIRD);
            jarOutputStream.closeEntry();
        } finally {
            jarOutputStream.close();
        }
        boolean fileDeletion;
        try {
            MatcherAssert.assertThat(dynamicType.inject(file, file), CoreMatchers.is(file));
            MatcherAssert.assertThat(file.exists(), CoreMatchers.is(true));
            MatcherAssert.assertThat(file.isFile(), CoreMatchers.is(true));
            MatcherAssert.assertThat(((file.length()) > 0L), CoreMatchers.is(true));
            Map<String, byte[]> bytes = new HashMap<String, byte[]>();
            bytes.put(((DynamicTypeDefaultTest.FOOBAR) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_FIRST);
            bytes.put(((DynamicTypeDefaultTest.QUXBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_SECOND);
            bytes.put(((DynamicTypeDefaultTest.BARBAZ) + (DynamicTypeDefaultTest.CLASS_FILE_EXTENSION)), DynamicTypeDefaultTest.BINARY_THIRD);
            DynamicTypeDefaultTest.assertJarFile(file, manifest, bytes);
        } finally {
            fileDeletion = file.delete();
        }
        MatcherAssert.assertThat(fileDeletion, CoreMatchers.is(true));
    }

    @Test
    public void testIterationOrder() throws Exception {
        Iterator<TypeDescription> types = dynamicType.getAllTypes().keySet().iterator();
        MatcherAssert.assertThat(types.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(types.next(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(types.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(types.next(), CoreMatchers.is(auxiliaryTypeDescription));
        MatcherAssert.assertThat(types.hasNext(), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testDispatcher() {
        MatcherAssert.assertThat(DISPATCHER, CoreMatchers.instanceOf(.class));
    }
}

