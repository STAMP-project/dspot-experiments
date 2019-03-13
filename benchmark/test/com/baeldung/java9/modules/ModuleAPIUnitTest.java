package com.baeldung.java9.modules;


import java.lang.module.ModuleDescriptor;
import java.sql.Driver;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ModuleAPIUnitTest {
    public static final String JAVA_BASE_MODULE_NAME = "java.base";

    private Module javaBaseModule;

    private Module javaSqlModule;

    private Module module;

    @Test
    public void whenCheckingIfNamed_thenModuleIsNamed() {
        Assert.assertThat(javaBaseModule.isNamed(), CoreMatchers.is(true));
        Assert.assertThat(javaBaseModule.getName(), CoreMatchers.is(ModuleAPIUnitTest.JAVA_BASE_MODULE_NAME));
    }

    @Test
    public void whenCheckingIfNamed_thenModuleIsUnnamed() {
        Assert.assertThat(module.isNamed(), CoreMatchers.is(false));
        Assert.assertThat(module.getName(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void whenExtractingPackagesContainedInAModule_thenModuleContainsOnlyFewOfThem() {
        Assert.assertTrue(javaBaseModule.getPackages().contains("java.lang.annotation"));
        Assert.assertFalse(javaBaseModule.getPackages().contains("java.sql"));
    }

    @Test
    public void whenRetrievingClassLoader_thenClassLoaderIsReturned() {
        Assert.assertThat(module.getClassLoader().getClass().getName(), CoreMatchers.is("jdk.internal.loader.ClassLoaders$AppClassLoader"));
    }

    @Test
    public void whenGettingAnnotationsPresentOnAModule_thenNoAnnotationsArePresent() {
        Assert.assertThat(javaBaseModule.getAnnotations().length, CoreMatchers.is(0));
    }

    @Test
    public void whenGettingLayerOfAModule_thenModuleLayerInformationAreAvailable() {
        ModuleLayer javaBaseModuleLayer = javaBaseModule.getLayer();
        Assert.assertTrue(javaBaseModuleLayer.configuration().findModule(ModuleAPIUnitTest.JAVA_BASE_MODULE_NAME).isPresent());
        Assert.assertThat(javaBaseModuleLayer.configuration().modules().size(), CoreMatchers.is(78));
        Assert.assertTrue(javaBaseModuleLayer.parents().get(0).configuration().parents().isEmpty());
    }

    @Test
    public void whenRetrievingModuleDescriptor_thenTypeOfModuleIsInferred() {
        ModuleDescriptor javaBaseModuleDescriptor = javaBaseModule.getDescriptor();
        ModuleDescriptor javaSqlModuleDescriptor = javaSqlModule.getDescriptor();
        Assert.assertFalse(javaBaseModuleDescriptor.isAutomatic());
        Assert.assertFalse(javaBaseModuleDescriptor.isOpen());
        Assert.assertFalse(javaSqlModuleDescriptor.isAutomatic());
        Assert.assertFalse(javaSqlModuleDescriptor.isOpen());
    }

    @Test
    public void givenModuleName_whenBuildingModuleDescriptor_thenBuilt() {
        Builder moduleBuilder = ModuleDescriptor.newModule("baeldung.base");
        ModuleDescriptor moduleDescriptor = moduleBuilder.build();
        Assert.assertThat(moduleDescriptor.name(), CoreMatchers.is("baeldung.base"));
    }

    @Test
    public void givenModules_whenAccessingModuleDescriptorRequires_thenRequiresAreReturned() {
        Set<Requires> javaBaseRequires = javaBaseModule.getDescriptor().requires();
        Set<Requires> javaSqlRequires = javaSqlModule.getDescriptor().requires();
        Set<String> javaSqlRequiresNames = javaSqlRequires.stream().map(Requires::name).collect(Collectors.toSet());
        Assert.assertThat(javaBaseRequires, empty());
        Assert.assertThat(javaSqlRequires.size(), CoreMatchers.is(3));
        Assert.assertThat(javaSqlRequiresNames, Matchers.containsInAnyOrder("java.base", "java.xml", "java.logging"));
    }

    @Test
    public void givenModules_whenAccessingModuleDescriptorProvides_thenProvidesAreReturned() {
        Set<Provides> javaBaseProvides = javaBaseModule.getDescriptor().provides();
        Set<Provides> javaSqlProvides = javaSqlModule.getDescriptor().provides();
        Set<String> javaBaseProvidesService = javaBaseProvides.stream().map(Provides::service).collect(Collectors.toSet());
        Assert.assertThat(javaBaseProvidesService, Matchers.contains("java.nio.file.spi.FileSystemProvider"));
        Assert.assertThat(javaSqlProvides, empty());
    }

    @Test
    public void givenModules_whenAccessingModuleDescriptorExports_thenExportsAreReturned() {
        Set<Exports> javaBaseExports = javaBaseModule.getDescriptor().exports();
        Set<Exports> javaSqlExports = javaSqlModule.getDescriptor().exports();
        Set<String> javaSqlExportsSource = javaSqlExports.stream().map(Exports::source).collect(Collectors.toSet());
        Assert.assertThat(javaBaseExports.size(), CoreMatchers.is(108));
        Assert.assertThat(javaSqlExports.size(), CoreMatchers.is(3));
        Assert.assertThat(javaSqlExportsSource, Matchers.containsInAnyOrder("java.sql", "javax.transaction.xa", "javax.sql"));
    }

    @Test
    public void givenModules_whenAccessingModuleDescriptorUses_thenUsesAreReturned() {
        Set<String> javaBaseUses = javaBaseModule.getDescriptor().uses();
        Set<String> javaSqlUses = javaSqlModule.getDescriptor().uses();
        Assert.assertThat(javaBaseUses.size(), CoreMatchers.is(34));
        Assert.assertThat(javaSqlUses, Matchers.contains("java.sql.Driver"));
    }

    @Test
    public void givenModules_whenAccessingModuleDescriptorOpen_thenOpenAreReturned() {
        Set<Opens> javaBaseUses = javaBaseModule.getDescriptor().opens();
        Set<Opens> javaSqlUses = javaSqlModule.getDescriptor().opens();
        Assert.assertThat(javaBaseUses, empty());
        Assert.assertThat(javaSqlUses, empty());
    }

    @Test
    public void whenAddingReadsToAModule_thenModuleCanReadNewModule() {
        Module updatedModule = module.addReads(javaSqlModule);
        Assert.assertTrue(updatedModule.canRead(javaSqlModule));
    }

    @Test
    public void whenExportingPackage_thenPackageIsExported() {
        Module updatedModule = module.addExports("com.baeldung.java9.modules", javaSqlModule);
        Assert.assertTrue(updatedModule.isExported("com.baeldung.java9.modules"));
    }

    @Test
    public void whenOpeningAModulePackage_thenPackagedIsOpened() {
        Module updatedModule = module.addOpens("com.baeldung.java9.modules", javaSqlModule);
        Assert.assertTrue(updatedModule.isOpen("com.baeldung.java9.modules", javaSqlModule));
    }

    @Test
    public void whenAddingUsesToModule_thenUsesIsAdded() {
        Module updatedModule = module.addUses(Driver.class);
        Assert.assertTrue(updatedModule.canUse(Driver.class));
    }

    private class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

