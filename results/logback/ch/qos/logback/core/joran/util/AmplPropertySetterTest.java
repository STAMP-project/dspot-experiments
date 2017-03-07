

package ch.qos.logback.core.joran.util;


public class AmplPropertySetterTest {
    ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry defaultComponentRegistry = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);

    ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();

    ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);

    @org.junit.Before
    public void setUp() {
        setter.setContext(context);
    }

    @org.junit.After
    public void tearDown() {
    }

    @org.junit.Test
    public void testCanAggregateComponent() {
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, setter.computeAggregationType("door"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("count"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Count"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("name"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Name"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Duration"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("fs"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("open"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Open"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, setter.computeAggregationType("Window"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY_COLLECTION, setter.computeAggregationType("adjective"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("filterReply"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("houseColor"));
    }

    @org.junit.Test
    public void testSetProperty() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test
    public void testSetCamelProperty() {
        setter.setProperty("camelCase", "trot");
        org.junit.Assert.assertEquals("trot", house.getCamelCase());
        setter.setProperty("camelCase", "gh");
        org.junit.Assert.assertEquals("gh", house.getCamelCase());
    }

    @org.junit.Test
    public void testSetComplexProperty() {
        ch.qos.logback.core.joran.util.Door door = new ch.qos.logback.core.joran.util.Door();
        setter.setComplexProperty("door", door);
        org.junit.Assert.assertEquals(door, house.getDoor());
    }

    @org.junit.Test
    public void testgetClassNameViaImplicitRules() {
        java.lang.Class<?> compClass = setter.getClassNameViaImplicitRules("door", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.Door.class, compClass);
    }

    @org.junit.Test
    public void testgetComplexPropertyColleClassNameViaImplicitRules() {
        java.lang.Class<?> compClass = setter.getClassNameViaImplicitRules("window", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.Window.class, compClass);
    }

    @org.junit.Test
    public void testPropertyCollection() {
        setter.addBasicProperty("adjective", "nice");
        setter.addBasicProperty("adjective", "big");
        org.junit.Assert.assertEquals(2, house.adjectiveList.size());
        org.junit.Assert.assertEquals("nice", house.adjectiveList.get(0));
        org.junit.Assert.assertEquals("big", house.adjectiveList.get(1));
    }

    @org.junit.Test
    public void testComplexCollection() {
        ch.qos.logback.core.joran.util.Window w1 = new ch.qos.logback.core.joran.util.Window();
        w1.handle = 10;
        ch.qos.logback.core.joran.util.Window w2 = new ch.qos.logback.core.joran.util.Window();
        w2.handle = 20;
        setter.addComplexProperty("window", w1);
        setter.addComplexProperty("window", w2);
        org.junit.Assert.assertEquals(2, house.windowList.size());
        org.junit.Assert.assertEquals(10, house.windowList.get(0).handle);
        org.junit.Assert.assertEquals(20, house.windowList.get(1).handle);
    }

    @org.junit.Test
    public void testSetComplexWithCamelCaseName() {
        ch.qos.logback.core.joran.util.SwimmingPool pool = new ch.qos.logback.core.joran.util.SwimmingPoolImpl();
        setter.setComplexProperty("swimmingPool", pool);
        org.junit.Assert.assertEquals(pool, house.getSwimmingPool());
    }

    @org.junit.Test
    public void testDuration() {
        setter.setProperty("duration", "1.4 seconds");
        org.junit.Assert.assertEquals(1400, house.getDuration().getMilliseconds());
    }

    @org.junit.Test
    public void testFileSize() {
        setter.setProperty("fs", "2 kb");
        org.junit.Assert.assertEquals((2 * 1024), house.getFs().getSize());
    }

    @org.junit.Test
    public void testFilterReply() {
        setter.setProperty("filterReply", "ACCEPT");
        org.junit.Assert.assertEquals(ch.qos.logback.core.spi.FilterReply.ACCEPT, house.getFilterReply());
    }

    @org.junit.Test
    public void testEnum() {
        setter.setProperty("houseColor", "BLUE");
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.HouseColor.BLUE, house.getHouseColor());
    }

    @org.junit.Test
    public void testDefaultClassAnnonation() {
        java.lang.reflect.Method relevantMethod = setter.getRelevantMethod("SwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY);
        org.junit.Assert.assertNotNull(relevantMethod);
        java.lang.Class<?> spClass = setter.getDefaultClassNameByAnnonation("SwimmingPool", relevantMethod);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.SwimmingPoolImpl.class, spClass);
        java.lang.Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("SwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.SwimmingPoolImpl.class, classViaImplicitRules);
    }

    @org.junit.Test
    public void testDefaultClassAnnotationForLists() {
        java.lang.reflect.Method relevantMethod = setter.getRelevantMethod("LargeSwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION);
        org.junit.Assert.assertNotNull(relevantMethod);
        java.lang.Class<?> spClass = setter.getDefaultClassNameByAnnonation("LargeSwimmingPool", relevantMethod);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.LargeSwimmingPoolImpl.class, spClass);
        java.lang.Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("LargeSwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.LargeSwimmingPoolImpl.class, classViaImplicitRules);
    }

    @org.junit.Test
    public void charset() {
        setter.setProperty("charset", "UTF-8");
        org.junit.Assert.assertEquals(java.nio.charset.Charset.forName("UTF-8"), house.getCharset());
        house.setCharset(null);
        setter.setProperty("charset", "UTF");
        org.junit.Assert.assertNull(house.getCharset());
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        checker.containsException(java.nio.charset.UnsupportedCharsetException.class);
    }

    @org.junit.Test
    public void bridgeMethodsShouldBeIgnored() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf82() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf82__8 = orangeSetter.getObjClass();
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getGenericSuperclass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSimpleName(), "Orange");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertTrue(((java.security.Permissions) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getPermissions())).isReadOnly());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getTypeName(), "java.lang.Object");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getName(), "java.lang.Object");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getTypeName(), "java.lang.Object");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSuperclass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getModifiers(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getSimpleName(), "Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getCanonicalName(), "java.lang.Object");
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getName(), "java.lang.Object");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSimpleName(), "Object");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).toGenericString(), "public class java.lang.Object");
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSimpleName(), "Object");
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getModifiers(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getGenericSuperclass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getCanonicalName(), "java.lang.Object");
        org.junit.Assert.assertNull(((java.security.CodeSource) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getAnnotatedSuperclass());
        org.junit.Assert.assertNull(((java.security.CodeSource) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getCodeSource())).getCertificates());
        org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSuperclass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).toGenericString(), "public class java.lang.Object");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getAnnotatedSuperclass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf91_failAssert20() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            ch.qos.logback.core.util.AggregationType vc_40 = ((ch.qos.logback.core.util.AggregationType) (null));
            java.lang.String vc_38 = ((java.lang.String) (null));
            orangeSetter.getRelevantMethod(vc_38, vc_40);
            java.lang.Object o_14_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf91 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf109() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_50 = ((java.lang.Object) (null));
        org.junit.Assert.assertNull(vc_50);
        java.lang.String vc_48 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_48);
        orangeSetter.addComplexProperty(vc_48, vc_50);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf75() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
        org.junit.Assert.assertNull(vc_30);
        java.lang.String vc_29 = new java.lang.String();
        org.junit.Assert.assertEquals(vc_29, "");
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf75__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_29, vc_30);
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf75__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf122_failAssert29() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.Object vc_57 = new java.lang.Object();
            java.lang.reflect.Method vc_54 = ((java.lang.reflect.Method) (null));
            orangeSetter.invokeMethodWithSingleParameterOnThisObject(vc_54, vc_57);
            java.lang.Object o_14_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf122 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf57_failAssert12() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_24 = ((ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry) (null));
            ch.qos.logback.core.util.AggregationType vc_22 = ((ch.qos.logback.core.util.AggregationType) (null));
            java.lang.String vc_21 = new java.lang.String();
            orangeSetter.getClassNameViaImplicitRules(vc_21, vc_22, vc_24);
            java.lang.Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf57 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf134() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_63 = new java.lang.Object();
        java.lang.String vc_60 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_60);
        orangeSetter.setComplexProperty(vc_60, vc_63);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf112() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_51 = new java.lang.Object();
        java.lang.String vc_49 = new java.lang.String();
        org.junit.Assert.assertEquals(vc_49, "");
        orangeSetter.addComplexProperty(vc_49, vc_51);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf133() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_62 = ((java.lang.Object) (null));
        org.junit.Assert.assertNull(vc_62);
        java.lang.String vc_60 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_60);
        orangeSetter.setComplexProperty(vc_60, vc_62);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf110() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_51 = new java.lang.Object();
        java.lang.String vc_48 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_48);
        orangeSetter.addComplexProperty(vc_48, vc_51);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf29_failAssert2() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.String vc_10 = ((java.lang.String) (null));
            orangeSetter.computeAggregationType(vc_10);
            java.lang.Object o_12_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf39() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.reflect.Method vc_16 = ((java.lang.reflect.Method) (null));
        org.junit.Assert.assertNull(vc_16);
        java.lang.String vc_15 = new java.lang.String();
        org.junit.Assert.assertEquals(vc_15, "");
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf39__12 = orangeSetter.getByConcreteType(vc_15, vc_16);
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf39__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf348_failAssert2() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_16 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_16);
            java.lang.String vc_14 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_14);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = orangeSetter.getByConcreteType(vc_14, vc_16);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_93 = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();
            ch.qos.logback.core.util.AggregationType vc_90 = ((ch.qos.logback.core.util.AggregationType) (null));
            java.lang.String vc_88 = ((java.lang.String) (null));
            orangeSetter.getClassNameViaImplicitRules(vc_88, vc_90, vc_93);
            java.lang.Object o_28_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf37_cf348 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf133_cf5952() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.Object vc_62 = ((java.lang.Object) (null));
        org.junit.Assert.assertNull(vc_62);
        org.junit.Assert.assertNull(vc_62);
        java.lang.String vc_60 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_60);
        org.junit.Assert.assertNull(vc_60);
        orangeSetter.setComplexProperty(vc_60, vc_62);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        java.lang.Object vc_798 = ((java.lang.Object) (null));
        org.junit.Assert.assertNull(vc_798);
        java.lang.String vc_796 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_796);
        orangeSetter.addComplexProperty(vc_796, vc_798);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1137_failAssert4() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_30);
            java.lang.String vc_28 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            java.lang.reflect.Method vc_220 = ((java.lang.reflect.Method) (null));
            java.lang.String vc_218 = ((java.lang.String) (null));
            ch.qos.logback.core.joran.util.PropertySetter vc_216 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_216.getByConcreteType(vc_218, vc_220);
            java.lang.Object o_28_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1137 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf136_cf7873_failAssert22() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.Object vc_63 = new java.lang.Object();
            java.lang.String vc_61 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_61, "");
            orangeSetter.setComplexProperty(vc_61, vc_63);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            java.lang.reflect.Method vc_968 = ((java.lang.reflect.Method) (null));
            ch.qos.logback.core.joran.util.PropertySetter vc_964 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_964.getByConcreteType(vc_61, vc_968);
            java.lang.Object o_140_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf136_cf7873 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1309() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
        org.junit.Assert.assertNull(vc_30);
        org.junit.Assert.assertNull(vc_30);
        java.lang.String vc_28 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_28);
        org.junit.Assert.assertNull(vc_28);
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
        java.lang.Object o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20 = orangeSetter.getObj();
        org.junit.Assert.assertTrue(o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20.equals(orange));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf82_cf1919_failAssert18() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf82__8 = orangeSetter.getObjClass();
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isPrimitive());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getGenericSuperclass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSigners());
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSimpleName(), "Orange");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isInterface());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isEnum());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertTrue(((java.security.Permissions) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getPermissions())).isReadOnly());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isSynthetic());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getModifiers(), 1025);
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isAnnotation());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getTypeName(), "java.lang.Object");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isPrimitive());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getName(), "java.lang.Object");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isArray());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isLocalClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getTypeName(), "java.lang.Object");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isInterface());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingMethod());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSuperclass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getModifiers(), 1);
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isAnonymousClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getSimpleName(), "Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getCanonicalName(), "java.lang.Object");
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationVersion());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isAnonymousClass());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getComponentType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getName(), "java.lang.Object");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isPrimitive());
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getClassLoader());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isAnnotation());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnumConstants());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isPrimitive());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getSimpleName(), "Object");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertEquals(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getSigners());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).toGenericString(), "public class java.lang.Object");
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isSynthetic());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSigners());
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSimpleName(), "Object");
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isInterface());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isInterface());
            org.junit.Assert.assertEquals(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getModifiers(), 1);
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getGenericSuperclass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getClassLoader());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isInterface());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getCanonicalName(), "java.lang.Object");
            org.junit.Assert.assertNull(((java.security.CodeSource) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getCodeSource())).getCodeSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getAnnotatedSuperclass());
            org.junit.Assert.assertNull(((java.security.CodeSource) (((java.security.ProtectionDomain) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getProtectionDomain())).getCodeSource())).getCertificates());
            org.junit.Assert.assertFalse(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getPackage())).isSealed());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).getSuperclass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnumConstants());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).isSynthetic());
            org.junit.Assert.assertNull(((java.lang.Class) (((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getGenericSuperclass())).getRawType())).getEnclosingClass());
            org.junit.Assert.assertNull(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getEnumConstants());
            org.junit.Assert.assertFalse(((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).toGenericString(), "public class java.lang.Object");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getAnnotatedSuperclass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((java.lang.Class) (o_bridgeMethodsShouldBeIgnored_cf82__8)).getSuperclass())).getGenericSuperclass())).getEnclosingClass());
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_365 = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();
            ch.qos.logback.core.util.AggregationType vc_362 = ((ch.qos.logback.core.util.AggregationType) (null));
            java.lang.String vc_361 = new java.lang.String();
            orangeSetter.getClassNameViaImplicitRules(vc_361, vc_362, vc_365);
            java.lang.Object o_306_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf82_cf1919 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf512() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.reflect.Method vc_16 = ((java.lang.reflect.Method) (null));
        org.junit.Assert.assertNull(vc_16);
        org.junit.Assert.assertNull(vc_16);
        java.lang.String vc_14 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_14);
        org.junit.Assert.assertNull(vc_14);
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = orangeSetter.getByConcreteType(vc_14, vc_16);
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
        java.lang.Object vc_119 = new java.lang.Object();
        java.lang.String vc_117 = new java.lang.String();
        org.junit.Assert.assertEquals(vc_117, "");
        orangeSetter.addComplexProperty(vc_117, vc_119);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf134_cf6728_failAssert9() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.Object vc_63 = new java.lang.Object();
            java.lang.String vc_60 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_60);
            orangeSetter.setComplexProperty(vc_60, vc_63);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            java.lang.Object vc_866 = ((java.lang.Object) (null));
            java.lang.String vc_865 = new java.lang.String();
            ch.qos.logback.core.joran.util.PropertySetter vc_862 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_862.addComplexProperty(vc_865, vc_866);
            java.lang.Object o_142_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf134_cf6728 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf443_failAssert15() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_16 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_16);
            java.lang.String vc_14 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_14);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = orangeSetter.getByConcreteType(vc_14, vc_16);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
            ch.qos.logback.core.joran.util.PropertySetter vc_100 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_100.getObjClass();
            java.lang.Object o_24_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf37_cf443 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf133_cf5523_failAssert3() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.Object vc_62 = ((java.lang.Object) (null));
            org.junit.Assert.assertNull(vc_62);
            java.lang.String vc_60 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_60);
            orangeSetter.setComplexProperty(vc_60, vc_62);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            java.lang.String vc_759 = new java.lang.String();
            orangeSetter.computeAggregationType(vc_759);
            java.lang.Object o_140_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf133_cf5523 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1309_cf10503_failAssert3() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_30);
            org.junit.Assert.assertNull(vc_30);
            java.lang.String vc_28 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28);
            org.junit.Assert.assertNull(vc_28);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            java.lang.Object o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20 = orangeSetter.getObj();
            org.junit.Assert.assertTrue(o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20.equals(orange));
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_1317 = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();
            ch.qos.logback.core.util.AggregationType vc_1314 = ((ch.qos.logback.core.util.AggregationType) (null));
            orangeSetter.getClassNameViaImplicitRules(vc_28, vc_1314, vc_1317);
            java.lang.Object o_34_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1309_cf10503 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf512_cf11449_failAssert7() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_16 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_16);
            org.junit.Assert.assertNull(vc_16);
            java.lang.String vc_14 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_14);
            org.junit.Assert.assertNull(vc_14);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = orangeSetter.getByConcreteType(vc_14, vc_16);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
            java.lang.Object vc_119 = new java.lang.Object();
            java.lang.String vc_117 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_117, "");
            orangeSetter.addComplexProperty(vc_117, vc_119);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.invokeMethodWithSingleParameterOnThisObject(vc_16, vc_119);
            java.lang.Object o_156_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf37_cf512_cf11449 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1418_cf9070() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
        org.junit.Assert.assertNull(vc_30);
        org.junit.Assert.assertNull(vc_30);
        org.junit.Assert.assertNull(vc_30);
        java.lang.String vc_28 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_28);
        org.junit.Assert.assertNull(vc_28);
        org.junit.Assert.assertNull(vc_28);
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
        java.lang.Object vc_267 = new java.lang.Object();
        orangeSetter.setComplexProperty(vc_28, vc_267);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        java.lang.Object vc_1071 = new java.lang.Object();
        java.lang.String vc_1068 = ((java.lang.String) (null));
        org.junit.Assert.assertNull(vc_1068);
        orangeSetter.addComplexProperty(vc_1068, vc_1071);
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf136_cf7873_failAssert22_add11564() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSigners());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getClassLoader());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isInterface());
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getModifiers(), 16401);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getCanonicalName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isPrimitive());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isMemberClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_6_0)).ordinal(), 1);
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getName(), "ch.qos.logback.core.util");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSimpleName(), "AggregationType");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getTypeName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<ch.qos.logback.core.util.AggregationType>");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getComponentType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).toGenericString(), "public final enum ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_6_0)).name(), "AS_BASIC_PROPERTY");
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getEnclosingMethod());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
            org.junit.Assert.assertTrue(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_6_0)).getDeclaringClass())).getSuperclass())).isArray());
            java.lang.Object vc_63 = new java.lang.Object();
            java.lang.String vc_61 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_61, "");
            org.junit.Assert.assertEquals(vc_61, "");
            orangeSetter.setComplexProperty(vc_61, vc_63);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            java.lang.reflect.Method vc_968 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_968);
            ch.qos.logback.core.joran.util.PropertySetter vc_964 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            org.junit.Assert.assertNull(vc_964);
            vc_964.getByConcreteType(vc_61, vc_968);
            java.lang.Object o_140_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf136_cf7873 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1309_cf10545_failAssert10() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_30);
            org.junit.Assert.assertNull(vc_30);
            java.lang.String vc_28 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28);
            org.junit.Assert.assertNull(vc_28);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            java.lang.Object o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20 = orangeSetter.getObj();
            org.junit.Assert.assertTrue(o_bridgeMethodsShouldBeIgnored_cf73_cf1309__20.equals(orange));
            java.lang.String vc_1320 = ((java.lang.String) (null));
            ch.qos.logback.core.joran.util.PropertySetter vc_1318 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_1318.getDefaultClassNameByAnnonation(vc_1320, vc_30);
            java.lang.Object o_34_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1309_cf10545 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1418_cf8660_failAssert4() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_30);
            org.junit.Assert.assertNull(vc_30);
            java.lang.String vc_28 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28);
            org.junit.Assert.assertNull(vc_28);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            java.lang.Object vc_267 = new java.lang.Object();
            orangeSetter.setComplexProperty(vc_28, vc_267);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            java.lang.reflect.Method vc_1036 = ((java.lang.reflect.Method) (null));
            java.lang.String vc_1035 = new java.lang.String();
            ch.qos.logback.core.joran.util.PropertySetter vc_1032 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
            vc_1032.getByConcreteType(vc_1035, vc_1036);
            java.lang.Object o_154_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1418_cf8660 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1418_cf9020_failAssert15() {
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            java.lang.Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            java.lang.reflect.Method vc_30 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_30);
            org.junit.Assert.assertNull(vc_30);
            java.lang.String vc_28 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28);
            org.junit.Assert.assertNull(vc_28);
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            java.lang.Object vc_267 = new java.lang.Object();
            orangeSetter.setComplexProperty(vc_28, vc_267);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getStatusManager());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isEnum());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSigners());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSimpleName(), "Orange");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getSimpleName(), "Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObj().equals(orange));
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getModifiers(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getPackage())).getName(), "ch.qos.logback.core.joran.util");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getObjClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter) (orangeSetter)).getContext());
            ch.qos.logback.core.util.AggregationType vc_1060 = ((ch.qos.logback.core.util.AggregationType) (null));
            java.lang.String vc_1059 = new java.lang.String();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.getRelevantMethod(vc_1059, vc_1060);
            java.lang.Object o_156_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1418_cf9020 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void charset_add12352() {
        setter.setProperty("charset", "UTF-8");
        org.junit.Assert.assertEquals(java.nio.charset.Charset.forName("UTF-8"), house.getCharset());
        house.setCharset(null);
        setter.setProperty("charset", "UTF");
        org.junit.Assert.assertNull(house.getCharset());
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        boolean o_charset_add12352__11 = checker.containsException(java.nio.charset.UnsupportedCharsetException.class);
        org.junit.Assert.assertTrue(o_charset_add12352__11);
        checker.containsException(java.nio.charset.UnsupportedCharsetException.class);
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279962_failAssert7() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                ch.qos.logback.core.util.AggregationType vc_28600 = ((ch.qos.logback.core.util.AggregationType) (null));
                java.lang.String vc_28599 = new java.lang.String();
                setter.getRelevantMethod(vc_28599, vc_28600);
                java.lang.Object o_38_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279962 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf280002_failAssert15() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.Object vc_28617 = new java.lang.Object();
                java.lang.reflect.Method vc_28614 = ((java.lang.reflect.Method) (null));
                setter.invokeMethodWithSingleParameterOnThisObject(vc_28614, vc_28617);
                java.lang.Object o_38_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf280002 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279869() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.String String_vc_3981 = "jack";
            org.junit.Assert.assertEquals(String_vc_3981, "jack");
            ch.qos.logback.core.util.AggregationType o_testSetProperty_cf279869__34 = setter.computeAggregationType(String_vc_3981);
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getModifiers(), 16401);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSimpleName(), "AggregationType");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getTypeName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSigners());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getEnclosingConstructor());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getClassLoader());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).toGenericString(), "public final enum ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).ordinal(), 0);
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).desiredAssertionStatus());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isAnnotation());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getCanonicalName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).name(), "NOT_FOUND");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isArray());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isLocalClass());
            org.junit.Assert.assertTrue(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isEnum());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isMemberClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getEnclosingMethod());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getName(), "ch.qos.logback.core.util");
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getComponentType());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<ch.qos.logback.core.util.AggregationType>");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279869__34)).getDeclaringClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279904_failAssert26() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_28584 = ((ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry) (null));
                ch.qos.logback.core.util.AggregationType vc_28582 = ((ch.qos.logback.core.util.AggregationType) (null));
                java.lang.String vc_28580 = ((java.lang.String) (null));
                setter.getClassNameViaImplicitRules(vc_28580, vc_28582, vc_28584);
                java.lang.Object o_40_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279904 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279884() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28576);
            java.lang.String vc_28575 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28575, "");
            java.lang.Class<?> o_testSetProperty_cf279884__36 = setter.getByConcreteType(vc_28575, vc_28576);
            org.junit.Assert.assertNull(o_testSetProperty_cf279884__36);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279950() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.Object o_testSetProperty_cf279950__32 = setter.getObj();
            org.junit.Assert.assertTrue(o_testSetProperty_cf279950__32.equals(house));
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).isOpen());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getFs());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getName(), "jack");
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getCharset());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getHouseColor());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getCount(), 10);
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getTemperature());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getFilterReply());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getCamelCase());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getSwimmingPool());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getDoor());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279950__32)).getDuration());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279868_failAssert53() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.String vc_28570 = ((java.lang.String) (null));
                setter.computeAggregationType(vc_28570);
                java.lang.Object o_36_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279868 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279938() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28590);
            java.lang.String vc_28589 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28589, "");
            java.lang.Class<?> o_testSetProperty_cf279938__36 = setter.getDefaultClassNameByAnnonation(vc_28589, vc_28590);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938__36);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279884_cf282890_failAssert23() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28576);
                java.lang.String vc_28575 = new java.lang.String();
                org.junit.Assert.assertEquals(vc_28575, "");
                java.lang.Class<?> o_testSetProperty_cf279884__36 = setter.getByConcreteType(vc_28575, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279884__36);
                java.lang.String vc_28979 = new java.lang.String();
                setter.computeAggregationType(vc_28979);
                java.lang.Object o_48_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279884_cf282890 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279934_cf280466_failAssert11() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28590);
                java.lang.String vc_28588 = ((java.lang.String) (null));
                org.junit.Assert.assertNull(vc_28588);
                java.lang.Class<?> o_testSetProperty_cf279934__36 = setter.getDefaultClassNameByAnnonation(vc_28588, vc_28590);
                org.junit.Assert.assertNull(o_testSetProperty_cf279934__36);
                ch.qos.logback.core.util.AggregationType vc_28668 = ((ch.qos.logback.core.util.AggregationType) (null));
                setter.getRelevantMethod(vc_28588, vc_28668);
                java.lang.Object o_48_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279934_cf280466 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279934_cf280232_failAssert3() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28590);
                java.lang.String vc_28588 = ((java.lang.String) (null));
                org.junit.Assert.assertNull(vc_28588);
                java.lang.Class<?> o_testSetProperty_cf279934__36 = setter.getDefaultClassNameByAnnonation(vc_28588, vc_28590);
                org.junit.Assert.assertNull(o_testSetProperty_cf279934__36);
                java.lang.reflect.Method vc_28644 = ((java.lang.reflect.Method) (null));
                java.lang.String vc_28642 = ((java.lang.String) (null));
                ch.qos.logback.core.joran.util.PropertySetter vc_28640 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
                vc_28640.getByConcreteType(vc_28642, vc_28644);
                java.lang.Object o_52_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279934_cf280232 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279880_cf282581_failAssert1() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28576);
                java.lang.String vc_28574 = ((java.lang.String) (null));
                org.junit.Assert.assertNull(vc_28574);
                java.lang.Class<?> o_testSetProperty_cf279880__36 = setter.getByConcreteType(vc_28574, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279880__36);
                java.lang.Object vc_28951 = new java.lang.Object();
                java.lang.String vc_28949 = new java.lang.String();
                ch.qos.logback.core.joran.util.PropertySetter vc_28946 = ((ch.qos.logback.core.joran.util.PropertySetter) (null));
                vc_28946.addComplexProperty(vc_28949, vc_28951);
                java.lang.Object o_52_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279880_cf282581 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279880_literalMutation282145() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 33.1);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28576);
            org.junit.Assert.assertNull(vc_28576);
            java.lang.String vc_28574 = ((java.lang.String) (null));
            org.junit.Assert.assertNull(vc_28574);
            org.junit.Assert.assertNull(vc_28574);
            java.lang.Class<?> o_testSetProperty_cf279880__36 = setter.getByConcreteType(vc_28574, vc_28576);
            org.junit.Assert.assertNull(o_testSetProperty_cf279880__36);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279938_cf280961_failAssert30() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28590);
                java.lang.String vc_28589 = new java.lang.String();
                org.junit.Assert.assertEquals(vc_28589, "");
                java.lang.Class<?> o_testSetProperty_cf279938__36 = setter.getDefaultClassNameByAnnonation(vc_28589, vc_28590);
                org.junit.Assert.assertNull(o_testSetProperty_cf279938__36);
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_28720 = ((ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry) (null));
                ch.qos.logback.core.util.AggregationType vc_28718 = ((ch.qos.logback.core.util.AggregationType) (null));
                java.lang.String String_vc_4003 = "temperature";
                setter.getClassNameViaImplicitRules(String_vc_4003, vc_28718, vc_28720);
                java.lang.Object o_52_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279938_cf280961 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279934_cf280547_failAssert13() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28590);
                java.lang.String vc_28588 = ((java.lang.String) (null));
                org.junit.Assert.assertNull(vc_28588);
                java.lang.Class<?> o_testSetProperty_cf279934__36 = setter.getDefaultClassNameByAnnonation(vc_28588, vc_28590);
                org.junit.Assert.assertNull(o_testSetProperty_cf279934__36);
                java.lang.Object vc_28685 = new java.lang.Object();
                java.lang.reflect.Method vc_28682 = ((java.lang.reflect.Method) (null));
                setter.invokeMethodWithSingleParameterOnThisObject(vc_28682, vc_28685);
                java.lang.Object o_50_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279934_cf280547 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279884_cf283071_cf284238_failAssert30() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28576);
                org.junit.Assert.assertNull(vc_28576);
                java.lang.String vc_28575 = new java.lang.String();
                org.junit.Assert.assertEquals(vc_28575, "");
                org.junit.Assert.assertEquals(vc_28575, "");
                java.lang.Class<?> o_testSetProperty_cf279884__36 = setter.getByConcreteType(vc_28575, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279884__36);
                java.lang.Class<?> o_testSetProperty_cf279884_cf283071__44 = setter.getDefaultClassNameByAnnonation(vc_28575, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279884_cf283071__44);
                ch.qos.logback.core.util.AggregationType vc_29144 = ((ch.qos.logback.core.util.AggregationType) (null));
                java.lang.String String_vc_4065 = "jack";
                setter.getRelevantMethod(String_vc_4065, vc_29144);
                java.lang.Object o_58_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279884_cf283071_cf284238 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279884_cf282929_cf285255_failAssert0() {
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                java.lang.Object o_11_0 = house.getCount();
                java.lang.Object o_13_0 = ((double) (house.getTemperature()));
                java.lang.Object o_15_0 = house.getName();
                java.lang.Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                java.lang.Object o_28_0 = house.getCount();
                java.lang.Object o_30_0 = house.getName();
                java.lang.reflect.Method vc_28576 = ((java.lang.reflect.Method) (null));
                org.junit.Assert.assertNull(vc_28576);
                org.junit.Assert.assertNull(vc_28576);
                java.lang.String vc_28575 = new java.lang.String();
                org.junit.Assert.assertEquals(vc_28575, "");
                org.junit.Assert.assertEquals(vc_28575, "");
                java.lang.Class<?> o_testSetProperty_cf279884__36 = setter.getByConcreteType(vc_28575, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279884__36);
                java.lang.String String_vc_4042 = "Count";
                org.junit.Assert.assertEquals(String_vc_4042, "Count");
                java.lang.Class<?> o_testSetProperty_cf279884_cf282929__46 = setter.getByConcreteType(String_vc_4042, vc_28576);
                org.junit.Assert.assertNull(o_testSetProperty_cf279884_cf282929__46);
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_29264 = ((ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry) (null));
                ch.qos.logback.core.util.AggregationType vc_29262 = ((ch.qos.logback.core.util.AggregationType) (null));
                java.lang.String vc_29260 = ((java.lang.String) (null));
                setter.getClassNameViaImplicitRules(vc_29260, vc_29262, vc_29264);
                java.lang.Object o_64_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf279884_cf282929_cf285255 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279938_cf280889_cf286918() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            java.lang.String vc_28589 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            java.lang.Class<?> o_testSetProperty_cf279938__36 = setter.getDefaultClassNameByAnnonation(vc_28589, vc_28590);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938__36);
            java.lang.reflect.Method vc_28712 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28712);
            org.junit.Assert.assertNull(vc_28712);
            java.lang.String vc_28711 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28711, "");
            org.junit.Assert.assertEquals(vc_28711, "");
            java.lang.Class<?> o_testSetProperty_cf279938_cf280889__48 = setter.getByConcreteType(vc_28711, vc_28712);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938_cf280889__48);
            java.lang.String String_vc_4111 = "10";
            org.junit.Assert.assertEquals(String_vc_4111, "10");
            ch.qos.logback.core.util.AggregationType o_testSetProperty_cf279938_cf280889_cf286918__62 = setter.computeAggregationType(String_vc_4111);
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSimpleName(), "AggregationType");
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isArray());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getModifiers(), 16401);
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSigners());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getClassLoader());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getEnclosingMethod());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isLocalClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).toGenericString(), "public final enum ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).ordinal(), 0);
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isSynthetic());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isAnnotation());
            org.junit.Assert.assertTrue(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isAnonymousClass());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getEnclosingClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getComponentType());
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getName(), "ch.qos.logback.core.util");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).isSealed());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).isMemberClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertFalse(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).desiredAssertionStatus());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getTypeName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getCanonicalName(), "ch.qos.logback.core.util.AggregationType");
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<ch.qos.logback.core.util.AggregationType>");
            org.junit.Assert.assertNull(((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).getDeclaringClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType) (o_testSetProperty_cf279938_cf280889_cf286918__62)).name(), "NOT_FOUND");
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279938_cf280889_cf287139() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            java.lang.String vc_28589 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            java.lang.Class<?> o_testSetProperty_cf279938__36 = setter.getDefaultClassNameByAnnonation(vc_28589, vc_28590);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938__36);
            java.lang.reflect.Method vc_28712 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28712);
            org.junit.Assert.assertNull(vc_28712);
            java.lang.String vc_28711 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28711, "");
            org.junit.Assert.assertEquals(vc_28711, "");
            java.lang.Class<?> o_testSetProperty_cf279938_cf280889__48 = setter.getByConcreteType(vc_28711, vc_28712);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938_cf280889__48);
            java.lang.Object o_testSetProperty_cf279938_cf280889_cf287139__60 = setter.getObj();
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getCamelCase());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getHouseColor());
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getName(), "jack");
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getCount(), 10);
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).isOpen());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getFs());
            org.junit.Assert.assertTrue(o_testSetProperty_cf279938_cf280889_cf287139__60.equals(house));
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getSwimmingPool());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getCharset());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getDuration());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getTemperature());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getFilterReply());
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House) (o_testSetProperty_cf279938_cf280889_cf287139__60)).getDoor());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf279938_cf280889_cf286962() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            java.lang.reflect.Method vc_28590 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            org.junit.Assert.assertNull(vc_28590);
            java.lang.String vc_28589 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            org.junit.Assert.assertEquals(vc_28589, "");
            java.lang.Class<?> o_testSetProperty_cf279938__36 = setter.getDefaultClassNameByAnnonation(vc_28589, vc_28590);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938__36);
            java.lang.reflect.Method vc_28712 = ((java.lang.reflect.Method) (null));
            org.junit.Assert.assertNull(vc_28712);
            org.junit.Assert.assertNull(vc_28712);
            java.lang.String vc_28711 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_28711, "");
            org.junit.Assert.assertEquals(vc_28711, "");
            java.lang.Class<?> o_testSetProperty_cf279938_cf280889__48 = setter.getByConcreteType(vc_28711, vc_28712);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938_cf280889__48);
            java.lang.String vc_29459 = new java.lang.String();
            org.junit.Assert.assertEquals(vc_29459, "");
            java.lang.Class<?> o_testSetProperty_cf279938_cf280889_cf286962__62 = setter.getByConcreteType(vc_29459, vc_28712);
            org.junit.Assert.assertNull(o_testSetProperty_cf279938_cf280889_cf286962__62);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf136() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_63 = new java.lang.Object();
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_61 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_61, "");
        // StatementAdderMethod cloned existing statement
        orangeSetter.setComplexProperty(vc_61, vc_63);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf136_cf7861_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_63 = new java.lang.Object();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_61 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_61, "");
            // StatementAdderMethod cloned existing statement
            orangeSetter.setComplexProperty(vc_61, vc_63);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
            // StatementAdderOnAssert create null value
            java.lang.String vc_962 = (java.lang.String)null;
            // StatementAddOnAssert local variable replacement
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            // StatementAdderMethod cloned existing statement
            setter.computeAggregationType(vc_962);
            // MethodAssertGenerator build local variable
            Object o_142_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf136_cf7861 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_16 = (java.lang.reflect.Method)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create null value
        java.lang.String vc_14 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_14, vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf512_cf10966_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_16 = (java.lang.reflect.Method)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16);
            // StatementAdderOnAssert create null value
            java.lang.String vc_14 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14);
            // AssertGenerator replace invocation
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_14, vc_16);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_119 = new java.lang.Object();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_117 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_117, "");
            // StatementAdderMethod cloned existing statement
            orangeSetter.addComplexProperty(vc_117, vc_119);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
            // StatementAdderOnAssert create null value
            java.lang.String vc_1374 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.util.PropertySetter vc_1372 = (ch.qos.logback.core.joran.util.PropertySetter)null;
            // StatementAdderMethod cloned existing statement
            vc_1372.getByConcreteType(vc_1374, vc_16);
            // MethodAssertGenerator build local variable
            Object o_156_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf37_cf512_cf10966 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37_cf512_cf10993() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_16 = (java.lang.reflect.Method)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create null value
        java.lang.String vc_14 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_14, vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_119 = new java.lang.Object();
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_117 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_117, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_117, "");
        // StatementAdderMethod cloned existing statement
        orangeSetter.addComplexProperty(vc_117, vc_119);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_1376 = (java.lang.reflect.Method)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1376);
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1375 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1375, "");
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37_cf512_cf10993__154 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_1375, vc_1376);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37_cf512_cf10993__154);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_30 = (java.lang.reflect.Method)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_30);
        // StatementAdderOnAssert create null value
        java.lang.String vc_28 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_28);
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73_cf1418_cf8748_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_30 = (java.lang.reflect.Method)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_30);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_30);
            // StatementAdderOnAssert create null value
            java.lang.String vc_28 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_28);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_28);
            // AssertGenerator replace invocation
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_267 = new java.lang.Object();
            // StatementAdderMethod cloned existing statement
            orangeSetter.setComplexProperty(vc_28, vc_267);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_1044 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_1042 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1041 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.util.PropertySetter vc_1038 = (ch.qos.logback.core.joran.util.PropertySetter)null;
            // StatementAdderMethod cloned existing statement
            vc_1038.getClassNameViaImplicitRules(vc_1041, vc_1042, vc_1044);
            // MethodAssertGenerator build local variable
            Object o_156_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf73_cf1418_cf8748 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf82_cf1943() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf82__8 = // StatementAdderMethod cloned existing statement
orangeSetter.getObjClass();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getGenericSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.security.Permissions)((java.security.ProtectionDomain)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getProtectionDomain()).getPermissions()).isReadOnly());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getTypeName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getSimpleName(), "Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).toGenericString(), "public class java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getSimpleName(), "Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getGenericSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getCanonicalName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.security.CodeSource)((java.security.ProtectionDomain)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getProtectionDomain()).getCodeSource()).getCodeSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getAnnotatedSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).getSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getGenericSuperclass()).getRawType()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getAnnotatedSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82__8).getSuperclass()).getGenericSuperclass()).getEnclosingClass());
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296 = // StatementAdderMethod cloned existing statement
orangeSetter.getObjClass();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.security.Permissions)((java.security.ProtectionDomain)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getProtectionDomain()).getPermissions()).isReadOnly());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getGenericSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.security.CodeSource)((java.security.ProtectionDomain)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getProtectionDomain()).getCodeSource()).getCodeSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.security.CodeSource)((java.security.ProtectionDomain)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getProtectionDomain()).getCodeSource()).getCertificates());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getAnnotatedSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getGenericSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getSimpleName(), "Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).toGenericString(), "public class java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getSimpleName(), "Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getAnnotatedSuperclass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getTypeName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getGenericSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getGenericSuperclass()).getRawType()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getSuperclass()).getCanonicalName(), "java.lang.Object");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)o_bridgeMethodsShouldBeIgnored_cf82_cf1943__296).getSuperclass()).getComponentType());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }
}

