package com.vaadin.tests.server;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.Test;


public class ClassesSerializableTest {
    /**
     * JARs that will be scanned for classes to test, in addition to classpath
     * directories.
     */
    private static final String JAR_PATTERN = ".*vaadin.*\\.jar";

    private static final String[] BASE_PACKAGES = new String[]{ "com.vaadin" };

    private static final String[] EXCLUDED_PATTERNS = new String[]{ "com\\.vaadin\\.demo\\..*"// 
    , "com\\.vaadin\\.external\\.org\\.apache\\.commons\\.fileupload\\..*"// 
    , "com\\.vaadin\\.launcher\\..*"// 
    , "com\\.vaadin\\.client\\..*"// 
    , "com\\.vaadin\\.server\\.widgetsetutils\\..*"// 
    , "com\\.vaadin\\.server\\.themeutils\\..*"// 
    , "com\\.vaadin\\.tests\\..*"// exclude automated tests
    , "com\\.vaadin\\.tools\\..*"// 
    , "com\\.vaadin\\.ui\\.themes\\..*"// 
    , // exact class level filtering
    "com\\.vaadin\\.event\\.FieldEvents"// 
    , "com\\.vaadin\\.util\\.FileTypeResolver", "com\\.vaadin\\.event\\.LayoutEvents"// 
    , "com\\.vaadin\\.event\\.MouseEvents"// 
    , "com\\.vaadin\\.event\\.UIEvents"// 
    , "com\\.vaadin\\.server\\.VaadinPortlet"// 
    , "com\\.vaadin\\.server\\.MockServletConfig"// 
    , "com\\.vaadin\\.server\\.MockServletContext"// 
    , "com\\.vaadin\\.server\\.Constants"// 
    , "com\\.vaadin\\.server\\.VaadinServiceClassLoaderUtil"// 
    , "com\\.vaadin\\.server\\.VaadinServiceClassLoaderUtil\\$GetClassLoaderPrivilegedAction"// 
    , "com\\.vaadin\\.server\\.communication\\.FileUploadHandler\\$SimpleMultiPartInputStream"// 
    , "com\\.vaadin\\.server\\.communication\\.PushRequestHandler.*", "com\\.vaadin\\.server\\.communication\\.PushHandler.*"// PushHandler
    , "com\\.vaadin\\.server\\.communication\\.DateSerializer"// 
    , "com\\.vaadin\\.server\\.communication\\.JSONSerializer"// 
    , "com\\.vaadin\\.ui\\.declarative\\.DesignContext"// 
    , // and its inner classes do not need to be serializable
    "com\\.vaadin\\.v7\\.util\\.SerializerHelper"// fully static
    , // class level filtering, also affecting nested classes and
    // interfaces
    "com\\.vaadin\\.server\\.LegacyCommunicationManager.*"// 
    , "com\\.vaadin\\.buildhelpers.*"// 
    , "com\\.vaadin\\.util\\.EncodeUtil.*"// 
    , "com\\.vaadin\\.util\\.ReflectTools.*"// 
    , "com\\.vaadin\\.data\\.provider\\.InMemoryDataProviderHelpers", "com\\.vaadin\\.data\\.provider\\.HierarchyMapper\\$TreeLevelQuery", "com\\.vaadin\\.data\\.util\\.ReflectTools.*"// 
    , "com\\.vaadin\\.data\\.util\\.JsonUtil.*"// 
    , "com\\.vaadin\\.data\\.util\\.BeanUtil.*", // the JSR-303 constraint interpolation context
    "com\\.vaadin\\.data\\.validator\\.BeanValidator\\$1"// 
    , "com\\.vaadin\\.sass.*"// 
    , "com\\.vaadin\\.testbench.*"// 
    , "com\\.vaadin\\.util\\.CurrentInstance\\$1"// 
    , "com\\.vaadin\\.server\\.AbstractClientConnector\\$1"// 
    , "com\\.vaadin\\.server\\.AbstractClientConnector\\$1\\$1"// 
    , "com\\.vaadin\\.server\\.JsonCodec\\$1"// 
    , "com\\.vaadin\\.server\\.communication\\.PushConnection"// 
    , "com\\.vaadin\\.server\\.communication\\.AtmospherePushConnection.*"// 
    , "com\\.vaadin\\.ui\\.components\\.colorpicker\\.ColorUtil"// 
    , "com\\.vaadin\\.util\\.ConnectorHelper"// 
    , "com\\.vaadin\\.server\\.VaadinSession\\$FutureAccess"// 
    , "com\\.vaadin\\.external\\..*"// 
    , "com\\.vaadin\\.util\\.WeakValueMap.*"// 
    , "com\\.vaadin\\.themes\\.valoutil\\.BodyStyleName"// 
    , "com\\.vaadin\\.server\\.communication\\.JSR356WebsocketInitializer.*"// 
    , "com\\.vaadin\\.screenshotbrowser\\.ScreenshotBrowser.*"// 
    , "com\\.vaadin\\.osgi.*"// 
    , "com\\.vaadin\\.server\\.osgi.*", // V7
    "com\\.vaadin\\.v7\\.ui\\.themes\\.BaseTheme", "com\\.vaadin\\.v7\\.ui\\.themes\\.ChameleonTheme", "com\\.vaadin\\.v7\\.ui\\.themes\\.Reindeer", "com\\.vaadin\\.v7\\.ui\\.themes\\.Runo", "com\\.vaadin\\.v7\\.tests\\.VaadinClasses", "com\\.vaadin\\.v7\\.event\\.FieldEvents"// 
    , "com\\.vaadin\\.v7\\.data\\.util.BeanItemContainerGenerator.*", "com\\.vaadin\\.v7\\.data\\.util\\.sqlcontainer\\.connection\\.MockInitialContextFactory", "com\\.vaadin\\.v7\\.data\\.util\\.sqlcontainer\\.DataGenerator", "com\\.vaadin\\.v7\\.data\\.util\\.sqlcontainer\\.FreeformQueryUtil" };

    /**
     * Tests that all the relevant classes and interfaces under
     * {@link #BASE_PACKAGES} implement Serializable.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClassesSerializable() throws Exception {
        List<String> rawClasspathEntries = ClassesSerializableTest.getRawClasspathEntries();
        List<String> classes = new ArrayList<>();
        for (String location : rawClasspathEntries) {
            classes.addAll(findServerClasses(location));
        }
        ArrayList<Field> nonSerializableFunctionFields = new ArrayList<>();
        List<Class<?>> nonSerializableClasses = new ArrayList<>();
        for (String className : classes) {
            Class<?> cls = Class.forName(className);
            // Don't add classes that have a @Ignore annotation on the class
            if (isTestClass(cls)) {
                continue;
            }
            // report fields that use lambda types that won't be serializable
            // (also in synthetic classes)
            Stream.of(cls.getDeclaredFields()).filter(( field) -> ClassesSerializableTest.isFunctionalType(field.getGenericType())).forEach(nonSerializableFunctionFields::add);
            // skip annotations and synthetic classes
            if ((cls.isAnnotation()) || (cls.isSynthetic())) {
                continue;
            }
            if ((!(cls.isInterface())) && (!(Modifier.isAbstract(cls.getModifiers())))) {
                serializeAndDeserialize(cls);
            }
            // report non-serializable classes and interfaces
            if (!(Serializable.class.isAssignableFrom(cls))) {
                if (((cls.getSuperclass()) == (Object.class)) && ((cls.getInterfaces().length) == 1)) {
                    // Single interface implementors
                    Class<?> iface = cls.getInterfaces()[0];
                    if (iface == (Runnable.class)) {
                        // Ignore Runnables used with access()
                        continue;
                    } else
                        if (iface == (Comparator.class)) {
                            // Ignore inline comparators
                            continue;
                        }

                }
                nonSerializableClasses.add(cls);
                // TODO easier to read when testing
                // System.err.println(cls);
            }
        }
        // useful failure message including all non-serializable classes and
        // interfaces
        if (!(nonSerializableClasses.isEmpty())) {
            failSerializableClasses(nonSerializableClasses);
        }
        if (!(nonSerializableFunctionFields.isEmpty())) {
            failSerializableFields(nonSerializableFunctionFields);
        }
    }
}

