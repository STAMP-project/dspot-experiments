/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Chris Beams
 */
@SuppressWarnings("rawtypes")
public class BridgeMethodResolverTests {
    @Test
    public void testFindBridgedMethod() throws Exception {
        Method unbridged = BridgeMethodResolverTests.MyFoo.class.getDeclaredMethod("someMethod", String.class, Object.class);
        Method bridged = BridgeMethodResolverTests.MyFoo.class.getDeclaredMethod("someMethod", Serializable.class, Object.class);
        Assert.assertFalse(unbridged.isBridge());
        Assert.assertTrue(bridged.isBridge());
        Assert.assertEquals("Unbridged method not returned directly", unbridged, BridgeMethodResolver.findBridgedMethod(unbridged));
        Assert.assertEquals("Incorrect bridged method returned", unbridged, BridgeMethodResolver.findBridgedMethod(bridged));
    }

    @Test
    public void testFindBridgedVarargMethod() throws Exception {
        Method unbridged = BridgeMethodResolverTests.MyFoo.class.getDeclaredMethod("someVarargMethod", String.class, Object[].class);
        Method bridged = BridgeMethodResolverTests.MyFoo.class.getDeclaredMethod("someVarargMethod", Serializable.class, Object[].class);
        Assert.assertFalse(unbridged.isBridge());
        Assert.assertTrue(bridged.isBridge());
        Assert.assertEquals("Unbridged method not returned directly", unbridged, BridgeMethodResolver.findBridgedMethod(unbridged));
        Assert.assertEquals("Incorrect bridged method returned", unbridged, BridgeMethodResolver.findBridgedMethod(bridged));
    }

    @Test
    public void testFindBridgedMethodInHierarchy() throws Exception {
        Method bridgeMethod = BridgeMethodResolverTests.DateAdder.class.getMethod("add", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(bridgeMethod);
        Assert.assertFalse(bridgedMethod.isBridge());
        Assert.assertEquals("add", bridgedMethod.getName());
        Assert.assertEquals(1, bridgedMethod.getParameterCount());
        Assert.assertEquals(Date.class, bridgedMethod.getParameterTypes()[0]);
    }

    @Test
    public void testIsBridgeMethodFor() throws Exception {
        Method bridged = BridgeMethodResolverTests.MyBar.class.getDeclaredMethod("someMethod", String.class, Object.class);
        Method other = BridgeMethodResolverTests.MyBar.class.getDeclaredMethod("someMethod", Integer.class, Object.class);
        Method bridge = BridgeMethodResolverTests.MyBar.class.getDeclaredMethod("someMethod", Object.class, Object.class);
        Assert.assertTrue("Should be bridge method", BridgeMethodResolver.isBridgeMethodFor(bridge, bridged, BridgeMethodResolverTests.MyBar.class));
        Assert.assertFalse("Should not be bridge method", BridgeMethodResolver.isBridgeMethodFor(bridge, other, BridgeMethodResolverTests.MyBar.class));
    }

    @Test
    public void testDoubleParameterization() throws Exception {
        Method objectBridge = BridgeMethodResolverTests.MyBoo.class.getDeclaredMethod("foo", Object.class);
        Method serializableBridge = BridgeMethodResolverTests.MyBoo.class.getDeclaredMethod("foo", Serializable.class);
        Method stringFoo = BridgeMethodResolverTests.MyBoo.class.getDeclaredMethod("foo", String.class);
        Method integerFoo = BridgeMethodResolverTests.MyBoo.class.getDeclaredMethod("foo", Integer.class);
        Assert.assertEquals("foo(String) not resolved.", stringFoo, BridgeMethodResolver.findBridgedMethod(objectBridge));
        Assert.assertEquals("foo(Integer) not resolved.", integerFoo, BridgeMethodResolver.findBridgedMethod(serializableBridge));
    }

    @Test
    public void testFindBridgedMethodFromMultipleBridges() throws Exception {
        Method loadWithObjectReturn = BridgeMethodResolverTests.findMethodWithReturnType("load", Object.class, BridgeMethodResolverTests.SettingsDaoImpl.class);
        Assert.assertNotNull(loadWithObjectReturn);
        Method loadWithSettingsReturn = BridgeMethodResolverTests.findMethodWithReturnType("load", BridgeMethodResolverTests.Settings.class, BridgeMethodResolverTests.SettingsDaoImpl.class);
        Assert.assertNotNull(loadWithSettingsReturn);
        Assert.assertNotSame(loadWithObjectReturn, loadWithSettingsReturn);
        Method method = BridgeMethodResolverTests.SettingsDaoImpl.class.getMethod("load");
        Assert.assertEquals(method, BridgeMethodResolver.findBridgedMethod(loadWithObjectReturn));
        Assert.assertEquals(method, BridgeMethodResolver.findBridgedMethod(loadWithSettingsReturn));
    }

    @Test
    public void testFindBridgedMethodFromParent() throws Exception {
        Method loadFromParentBridge = BridgeMethodResolverTests.SettingsDaoImpl.class.getMethod("loadFromParent");
        Assert.assertTrue(loadFromParentBridge.isBridge());
        Method loadFromParent = BridgeMethodResolverTests.AbstractDaoImpl.class.getMethod("loadFromParent");
        Assert.assertFalse(loadFromParent.isBridge());
        Assert.assertEquals(loadFromParent, BridgeMethodResolver.findBridgedMethod(loadFromParentBridge));
    }

    @Test
    public void testWithSingleBoundParameterizedOnInstantiate() throws Exception {
        Method bridgeMethod = DelayQueue.class.getMethod("add", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Method actualMethod = DelayQueue.class.getMethod("add", Delayed.class);
        Assert.assertFalse(actualMethod.isBridge());
        Assert.assertEquals(actualMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testWithDoubleBoundParameterizedOnInstantiate() throws Exception {
        Method bridgeMethod = BridgeMethodResolverTests.SerializableBounded.class.getMethod("boundedOperation", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Method actualMethod = BridgeMethodResolverTests.SerializableBounded.class.getMethod("boundedOperation", HashMap.class);
        Assert.assertFalse(actualMethod.isBridge());
        Assert.assertEquals(actualMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testWithGenericParameter() throws Exception {
        Method[] methods = BridgeMethodResolverTests.StringGenericParameter.class.getMethods();
        Method bridgeMethod = null;
        Method bridgedMethod = null;
        for (Method method : methods) {
            if (("getFor".equals(method.getName())) && (!(method.getParameterTypes()[0].equals(Integer.class)))) {
                if (method.getReturnType().equals(Object.class)) {
                    bridgeMethod = method;
                } else {
                    bridgedMethod = method;
                }
            }
        }
        Assert.assertTrue(((bridgeMethod != null) && (bridgeMethod.isBridge())));
        Assert.assertTrue(((bridgedMethod != null) && (!(bridgedMethod.isBridge()))));
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testOnAllMethods() throws Exception {
        Method[] methods = BridgeMethodResolverTests.StringList.class.getMethods();
        for (Method method : methods) {
            Assert.assertNotNull(BridgeMethodResolver.findBridgedMethod(method));
        }
    }

    @Test
    public void testSPR2583() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.MessageBroadcasterImpl.class.getMethod("receive", BridgeMethodResolverTests.MessageEvent.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.MessageBroadcasterImpl.class.getMethod("receive", BridgeMethodResolverTests.Event.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Method otherMethod = BridgeMethodResolverTests.MessageBroadcasterImpl.class.getMethod("receive", BridgeMethodResolverTests.NewMessageEvent.class);
        Assert.assertFalse(otherMethod.isBridge());
        Assert.assertFalse("Match identified incorrectly", BridgeMethodResolver.isBridgeMethodFor(bridgeMethod, otherMethod, BridgeMethodResolverTests.MessageBroadcasterImpl.class));
        Assert.assertTrue("Match not found correctly", BridgeMethodResolver.isBridgeMethodFor(bridgeMethod, bridgedMethod, BridgeMethodResolverTests.MessageBroadcasterImpl.class));
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR2603() throws Exception {
        Method objectBridge = BridgeMethodResolverTests.YourHomer.class.getDeclaredMethod("foo", BridgeMethodResolverTests.Bounded.class);
        Method abstractBoundedFoo = BridgeMethodResolverTests.YourHomer.class.getDeclaredMethod("foo", BridgeMethodResolverTests.AbstractBounded.class);
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(objectBridge);
        Assert.assertEquals("foo(AbstractBounded) not resolved.", abstractBoundedFoo, bridgedMethod);
    }

    @Test
    public void testSPR2648() throws Exception {
        Method bridgeMethod = ReflectionUtils.findMethod(BridgeMethodResolverTests.GenericSqlMapIntegerDao.class, "saveOrUpdate", Object.class);
        Assert.assertTrue(((bridgeMethod != null) && (bridgeMethod.isBridge())));
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(bridgeMethod);
        Assert.assertFalse(bridgedMethod.isBridge());
        Assert.assertEquals("saveOrUpdate", bridgedMethod.getName());
    }

    @Test
    public void testSPR2763() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.AbstractDao.class.getDeclaredMethod("save", Object.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.UserDaoImpl.class.getDeclaredMethod("save", BridgeMethodResolverTests.User.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3041() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.BusinessDao.class.getDeclaredMethod("save", BridgeMethodResolverTests.Business.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.BusinessDao.class.getDeclaredMethod("save", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3173() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.UserDaoImpl.class.getDeclaredMethod("saveVararg", BridgeMethodResolverTests.User.class, Object[].class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.UserDaoImpl.class.getDeclaredMethod("saveVararg", Object.class, Object[].class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3304() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.MegaMessageProducerImpl.class.getDeclaredMethod("receive", BridgeMethodResolverTests.MegaMessageEvent.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.MegaMessageProducerImpl.class.getDeclaredMethod("receive", BridgeMethodResolverTests.MegaEvent.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3324() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.BusinessDao.class.getDeclaredMethod("get", Long.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.BusinessDao.class.getDeclaredMethod("get", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3357() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.ExtendsAbstractImplementsInterface.class.getDeclaredMethod("doSomething", BridgeMethodResolverTests.DomainObjectExtendsSuper.class, Object.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.ExtendsAbstractImplementsInterface.class.getDeclaredMethod("doSomething", BridgeMethodResolverTests.DomainObjectSuper.class, Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3485() throws Exception {
        Method bridgedMethod = BridgeMethodResolverTests.DomainObject.class.getDeclaredMethod("method2", BridgeMethodResolverTests.ParameterType.class, byte[].class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Method bridgeMethod = BridgeMethodResolverTests.DomainObject.class.getDeclaredMethod("method2", Serializable.class, Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertEquals(bridgedMethod, BridgeMethodResolver.findBridgedMethod(bridgeMethod));
    }

    @Test
    public void testSPR3534() throws Exception {
        Method bridgeMethod = ReflectionUtils.findMethod(BridgeMethodResolverTests.TestEmailProvider.class, "findBy", Object.class);
        Assert.assertTrue(((bridgeMethod != null) && (bridgeMethod.isBridge())));
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(bridgeMethod);
        Assert.assertFalse(bridgedMethod.isBridge());
        Assert.assertEquals("findBy", bridgedMethod.getName());
    }

    // SPR-16103
    @Test
    public void testClassHierarchy() throws Exception {
        doTestHierarchyResolution(BridgeMethodResolverTests.FooClass.class);
    }

    // SPR-16103
    @Test
    public void testInterfaceHierarchy() throws Exception {
        doTestHierarchyResolution(BridgeMethodResolverTests.FooInterface.class);
    }

    public interface Foo<T extends Serializable> {
        void someMethod(T theArg, Object otherArg);

        void someVarargMethod(T theArg, Object... otherArg);
    }

    public static class MyFoo implements BridgeMethodResolverTests.Foo<String> {
        public void someMethod(Integer theArg, Object otherArg) {
        }

        @Override
        public void someMethod(String theArg, Object otherArg) {
        }

        @Override
        public void someVarargMethod(String theArg, Object... otherArgs) {
        }
    }

    public abstract static class Bar<T> {
        void someMethod(Map<?, ?> m, Object otherArg) {
        }

        void someMethod(T theArg, Map<?, ?> m) {
        }

        abstract void someMethod(T theArg, Object otherArg);
    }

    public abstract static class InterBar<T> extends BridgeMethodResolverTests.Bar<T> {}

    public static class MyBar extends BridgeMethodResolverTests.InterBar<String> {
        @Override
        public void someMethod(String theArg, Object otherArg) {
        }

        public void someMethod(Integer theArg, Object otherArg) {
        }
    }

    public interface Adder<T> {
        void add(T item);
    }

    public abstract static class AbstractDateAdder implements BridgeMethodResolverTests.Adder<Date> {
        @Override
        public abstract void add(Date date);
    }

    public static class DateAdder extends BridgeMethodResolverTests.AbstractDateAdder {
        @Override
        public void add(Date date) {
        }
    }

    public static class Enclosing<T> {
        public class Enclosed<S> {
            public class ReallyDeepNow<R> {
                void someMethod(S s, T t, R r) {
                }
            }
        }
    }

    public static class ExtendsEnclosing extends BridgeMethodResolverTests.Enclosing<String> {
        public class ExtendsEnclosed extends BridgeMethodResolverTests.Enclosing<String>.Enclosed<Integer> {
            public class ExtendsReallyDeepNow extends BridgeMethodResolverTests.Enclosing<String>.Enclosed<Integer>.ReallyDeepNow<Long> {
                @Override
                void someMethod(Integer s, String t, Long r) {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

    public interface Boo<E, T extends Serializable> {
        void foo(T t);
    }

    public static class MyBoo implements BridgeMethodResolverTests.Boo<String, Integer> {
        @Override
        public void foo(String e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void foo(Integer t) {
            throw new UnsupportedOperationException();
        }
    }

    public interface Settings {}

    public interface ConcreteSettings extends BridgeMethodResolverTests.Settings {}

    public interface Dao<T, S> {
        T load();

        S loadFromParent();
    }

    public interface SettingsDao<T extends BridgeMethodResolverTests.Settings, S> extends BridgeMethodResolverTests.Dao<T, S> {
        @Override
        T load();
    }

    public interface ConcreteSettingsDao extends BridgeMethodResolverTests.SettingsDao<BridgeMethodResolverTests.ConcreteSettings, String> {
        @Override
        String loadFromParent();
    }

    abstract static class AbstractDaoImpl<T, S> implements BridgeMethodResolverTests.Dao<T, S> {
        protected T object;

        protected S otherObject;

        protected AbstractDaoImpl(T object, S otherObject) {
            this.object = object;
            this.otherObject = otherObject;
        }

        // @Transactional(readOnly = true)
        @Override
        public S loadFromParent() {
            return otherObject;
        }
    }

    static class SettingsDaoImpl extends BridgeMethodResolverTests.AbstractDaoImpl<BridgeMethodResolverTests.ConcreteSettings, String> implements BridgeMethodResolverTests.ConcreteSettingsDao {
        protected SettingsDaoImpl(BridgeMethodResolverTests.ConcreteSettings object) {
            super(object, "From Parent");
        }

        // @Transactional(readOnly = true)
        @Override
        public BridgeMethodResolverTests.ConcreteSettings load() {
            return super.object;
        }
    }

    public interface Bounded<E> {
        boolean boundedOperation(E e);
    }

    private static class AbstractBounded<E> implements BridgeMethodResolverTests.Bounded<E> {
        @Override
        public boolean boundedOperation(E myE) {
            return true;
        }
    }

    private static class SerializableBounded<E extends HashMap & Delayed> extends BridgeMethodResolverTests.AbstractBounded<E> {
        @Override
        public boolean boundedOperation(E myE) {
            return false;
        }
    }

    public interface GenericParameter<T> {
        T getFor(Class<T> cls);
    }

    @SuppressWarnings("unused")
    private static class StringGenericParameter implements BridgeMethodResolverTests.GenericParameter<String> {
        @Override
        public String getFor(Class<String> cls) {
            return "foo";
        }

        public String getFor(Integer integer) {
            return "foo";
        }
    }

    private static class StringList implements List<String> {
        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(String o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends String> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String set(int index, String element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, String element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<String> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<String> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
    }

    public interface Event {
        int getPriority();
    }

    public static class GenericEvent implements BridgeMethodResolverTests.Event {
        private int priority;

        @Override
        public int getPriority() {
            return priority;
        }

        /**
         * Constructor that takes an event priority
         */
        public GenericEvent(int priority) {
            this.priority = priority;
        }

        /**
         * Default Constructor
         */
        public GenericEvent() {
        }
    }

    public interface UserInitiatedEvent {}

    public abstract static class BaseUserInitiatedEvent extends BridgeMethodResolverTests.GenericEvent implements BridgeMethodResolverTests.UserInitiatedEvent {}

    public static class MessageEvent extends BridgeMethodResolverTests.BaseUserInitiatedEvent {}

    public interface Channel<E extends BridgeMethodResolverTests.Event> {
        void send(E event);

        void subscribe(final BridgeMethodResolverTests.Receiver<E> receiver, Class<E> event);

        void unsubscribe(final BridgeMethodResolverTests.Receiver<E> receiver, Class<E> event);
    }

    public interface Broadcaster {}

    public interface EventBroadcaster extends BridgeMethodResolverTests.Broadcaster {
        void subscribe();

        void unsubscribe();

        void setChannel(BridgeMethodResolverTests.Channel<?> channel);
    }

    public static class GenericBroadcasterImpl implements BridgeMethodResolverTests.Broadcaster {}

    @SuppressWarnings({ "unused", "unchecked" })
    public abstract static class GenericEventBroadcasterImpl<T extends BridgeMethodResolverTests.Event> extends BridgeMethodResolverTests.GenericBroadcasterImpl implements BridgeMethodResolverTests.EventBroadcaster {
        private Class<T>[] subscribingEvents;

        private BridgeMethodResolverTests.Channel<T> channel;

        /**
         * Abstract method to retrieve instance of subclass
         *
         * @return receiver instance
         */
        public abstract BridgeMethodResolverTests.Receiver<T> getInstance();

        @Override
        public void setChannel(BridgeMethodResolverTests.Channel channel) {
            this.channel = channel;
        }

        private String beanName;

        public void setBeanName(String name) {
            this.beanName = name;
        }

        @Override
        public void subscribe() {
        }

        @Override
        public void unsubscribe() {
        }

        public GenericEventBroadcasterImpl(Class<? extends T>... events) {
        }
    }

    public interface Receiver<E extends BridgeMethodResolverTests.Event> {
        void receive(E event);
    }

    public interface MessageBroadcaster extends BridgeMethodResolverTests.Receiver<BridgeMethodResolverTests.MessageEvent> {}

    public static class RemovedMessageEvent extends BridgeMethodResolverTests.MessageEvent {}

    public static class NewMessageEvent extends BridgeMethodResolverTests.MessageEvent {}

    public static class ModifiedMessageEvent extends BridgeMethodResolverTests.MessageEvent {}

    // implement an unrelated interface first (SPR-16288)
    @SuppressWarnings({ "serial", "unchecked" })
    public static class MessageBroadcasterImpl extends BridgeMethodResolverTests.GenericEventBroadcasterImpl<BridgeMethodResolverTests.MessageEvent> implements Serializable , BridgeMethodResolverTests.MessageBroadcaster {
        public MessageBroadcasterImpl() {
            super(BridgeMethodResolverTests.NewMessageEvent.class);
        }

        @Override
        public void receive(BridgeMethodResolverTests.MessageEvent event) {
            throw new UnsupportedOperationException("should not be called, use subclassed events");
        }

        public void receive(BridgeMethodResolverTests.NewMessageEvent event) {
        }

        @Override
        public BridgeMethodResolverTests.Receiver<BridgeMethodResolverTests.MessageEvent> getInstance() {
            return null;
        }

        public void receive(BridgeMethodResolverTests.RemovedMessageEvent event) {
        }

        public void receive(BridgeMethodResolverTests.ModifiedMessageEvent event) {
        }
    }

    // -----------------------------
    // SPR-2454 Test Classes
    // -----------------------------
    public interface SimpleGenericRepository<T> {
        public Class<T> getPersistentClass();

        List<T> findByQuery();

        List<T> findAll();

        T refresh(T entity);

        T saveOrUpdate(T entity);

        void delete(Collection<T> entities);
    }

    public interface RepositoryRegistry {
        <T> BridgeMethodResolverTests.SimpleGenericRepository<T> getFor(Class<T> entityType);
    }

    @SuppressWarnings("unchecked")
    public static class SettableRepositoryRegistry<R extends BridgeMethodResolverTests.SimpleGenericRepository<?>> implements BridgeMethodResolverTests.RepositoryRegistry {
        protected void injectInto(R rep) {
        }

        public void register(R rep) {
        }

        public void register(R... reps) {
        }

        public void setRepos(R... reps) {
        }

        @Override
        public <T> BridgeMethodResolverTests.SimpleGenericRepository<T> getFor(Class<T> entityType) {
            return null;
        }

        public void afterPropertiesSet() throws Exception {
        }
    }

    public interface ConvenientGenericRepository<T, ID extends Serializable> extends BridgeMethodResolverTests.SimpleGenericRepository<T> {
        T findById(ID id, boolean lock);

        List<T> findByExample(T exampleInstance);

        void delete(ID id);

        void delete(T entity);
    }

    public static class GenericHibernateRepository<T, ID extends Serializable> implements BridgeMethodResolverTests.ConvenientGenericRepository<T, ID> {
        /**
         *
         *
         * @param c
         * 		Mandatory. The domain class this repository is responsible for.
         */
        // Since it is impossible to determine the actual type of a type
        // parameter (!), we resort to requiring the caller to provide the
        // actual type as parameter, too.
        // Not set in a constructor to enable easy CGLIB-proxying (passing
        // constructor arguments to Spring AOP proxies is quite cumbersome).
        public void setPersistentClass(Class<T> c) {
        }

        @Override
        public Class<T> getPersistentClass() {
            return null;
        }

        @Override
        public T findById(ID id, boolean lock) {
            return null;
        }

        @Override
        public List<T> findAll() {
            return null;
        }

        @Override
        public List<T> findByExample(T exampleInstance) {
            return null;
        }

        @Override
        public List<T> findByQuery() {
            return null;
        }

        @Override
        public T saveOrUpdate(T entity) {
            return null;
        }

        @Override
        public T refresh(T entity) {
            return null;
        }

        @Override
        public void delete(ID id) {
        }

        @Override
        public void delete(Collection<T> entities) {
        }
    }

    public static class HibernateRepositoryRegistry extends BridgeMethodResolverTests.SettableRepositoryRegistry<BridgeMethodResolverTests.GenericHibernateRepository<?, ?>> {
        @Override
        public void injectInto(BridgeMethodResolverTests.GenericHibernateRepository<?, ?> rep) {
        }

        @Override
        public <T> BridgeMethodResolverTests.GenericHibernateRepository<T, ?> getFor(Class<T> entityType) {
            return null;
        }
    }

    // -------------------
    // SPR-2603 classes
    // -------------------
    public interface Homer<E> {
        void foo(E e);
    }

    public static class MyHomer<T extends BridgeMethodResolverTests.Bounded<T>, L extends T> implements BridgeMethodResolverTests.Homer<L> {
        @Override
        public void foo(L t) {
            throw new UnsupportedOperationException();
        }
    }

    public static class YourHomer<T extends BridgeMethodResolverTests.AbstractBounded<T>, L extends T> extends BridgeMethodResolverTests.MyHomer<T, L> {
        @Override
        public void foo(L t) {
            throw new UnsupportedOperationException();
        }
    }

    public interface GenericDao<T> {
        void saveOrUpdate(T t);
    }

    public interface ConvenienceGenericDao<T> extends BridgeMethodResolverTests.GenericDao<T> {}

    public static class GenericSqlMapDao<T extends Serializable> implements BridgeMethodResolverTests.ConvenienceGenericDao<T> {
        @Override
        public void saveOrUpdate(T t) {
            throw new UnsupportedOperationException();
        }
    }

    public static class GenericSqlMapIntegerDao<T extends Number> extends BridgeMethodResolverTests.GenericSqlMapDao<T> {
        @Override
        public void saveOrUpdate(T t) {
        }
    }

    public static class Permission {}

    public static class User {}

    public interface UserDao {
        // @Transactional
        void save(BridgeMethodResolverTests.User user);

        // @Transactional
        void save(BridgeMethodResolverTests.Permission perm);
    }

    public abstract static class AbstractDao<T> {
        public void save(T t) {
        }

        public void saveVararg(T t, Object... args) {
        }
    }

    public static class UserDaoImpl extends BridgeMethodResolverTests.AbstractDao<BridgeMethodResolverTests.User> implements BridgeMethodResolverTests.UserDao {
        @Override
        public void save(BridgeMethodResolverTests.Permission perm) {
        }

        @Override
        public void saveVararg(BridgeMethodResolverTests.User user, Object... args) {
        }
    }

    public interface DaoInterface<T, P> {
        T get(P id);
    }

    public abstract static class BusinessGenericDao<T, PK extends Serializable> implements BridgeMethodResolverTests.DaoInterface<T, PK> {
        public void save(T object) {
        }
    }

    public static class Business<T> {}

    public static class BusinessDao extends BridgeMethodResolverTests.BusinessGenericDao<BridgeMethodResolverTests.Business<?>, Long> {
        @Override
        public void save(BridgeMethodResolverTests.Business<?> business) {
        }

        @Override
        public BridgeMethodResolverTests.Business<?> get(Long id) {
            return null;
        }

        public BridgeMethodResolverTests.Business<?> get(String code) {
            return null;
        }
    }

    // -------------------
    // SPR-3304 classes
    // -------------------
    private static class MegaEvent {}

    private static class MegaMessageEvent extends BridgeMethodResolverTests.MegaEvent {}

    private static class NewMegaMessageEvent extends BridgeMethodResolverTests.MegaEvent {}

    private static class ModifiedMegaMessageEvent extends BridgeMethodResolverTests.MegaEvent {}

    public interface MegaReceiver<E extends BridgeMethodResolverTests.MegaEvent> {
        void receive(E event);
    }

    public interface MegaMessageProducer extends BridgeMethodResolverTests.MegaReceiver<BridgeMethodResolverTests.MegaMessageEvent> {}

    private static class Other<S, E> {}

    @SuppressWarnings("unused")
    private static class MegaMessageProducerImpl extends BridgeMethodResolverTests.Other<Long, String> implements BridgeMethodResolverTests.MegaMessageProducer {
        public void receive(BridgeMethodResolverTests.NewMegaMessageEvent event) {
            throw new UnsupportedOperationException();
        }

        public void receive(BridgeMethodResolverTests.ModifiedMegaMessageEvent event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void receive(BridgeMethodResolverTests.MegaMessageEvent event) {
            throw new UnsupportedOperationException();
        }
    }

    // -------------------
    // SPR-3357 classes
    // -------------------
    private static class DomainObjectSuper {}

    private static class DomainObjectExtendsSuper extends BridgeMethodResolverTests.DomainObjectSuper {}

    public interface IGenericInterface<D extends BridgeMethodResolverTests.DomainObjectSuper> {
        <T> void doSomething(final D domainObject, final T value);
    }

    @SuppressWarnings("unused")
    private abstract static class AbstractImplementsInterface<D extends BridgeMethodResolverTests.DomainObjectSuper> implements BridgeMethodResolverTests.IGenericInterface<D> {
        @Override
        public <T> void doSomething(D domainObject, T value) {
        }

        public void anotherBaseMethod() {
        }
    }

    private static class ExtendsAbstractImplementsInterface extends BridgeMethodResolverTests.AbstractImplementsInterface<BridgeMethodResolverTests.DomainObjectExtendsSuper> {
        @Override
        public <T> void doSomething(BridgeMethodResolverTests.DomainObjectExtendsSuper domainObject, T value) {
            super.doSomething(domainObject, value);
        }
    }

    // -------------------
    // SPR-3485 classes
    // -------------------
    @SuppressWarnings("serial")
    private static class ParameterType implements Serializable {}

    private static class AbstractDomainObject<P extends Serializable, R> {
        public R method1(P p) {
            return null;
        }

        public void method2(P p, R r) {
        }
    }

    private static class DomainObject extends BridgeMethodResolverTests.AbstractDomainObject<BridgeMethodResolverTests.ParameterType, byte[]> {
        @Override
        public byte[] method1(BridgeMethodResolverTests.ParameterType p) {
            return super.method1(p);
        }

        @Override
        public void method2(BridgeMethodResolverTests.ParameterType p, byte[] r) {
            super.method2(p, r);
        }
    }

    // -------------------
    // SPR-3534 classes
    // -------------------
    public interface SearchProvider<RETURN_TYPE, CONDITIONS_TYPE> {
        Collection<RETURN_TYPE> findBy(CONDITIONS_TYPE conditions);
    }

    public static class SearchConditions {}

    public interface IExternalMessageProvider<S extends BridgeMethodResolverTests.ExternalMessage, T extends BridgeMethodResolverTests.ExternalMessageSearchConditions<?>> extends BridgeMethodResolverTests.SearchProvider<S, T> {}

    public static class ExternalMessage {}

    public static class ExternalMessageSearchConditions<T extends BridgeMethodResolverTests.ExternalMessage> extends BridgeMethodResolverTests.SearchConditions {}

    public static class ExternalMessageProvider<S extends BridgeMethodResolverTests.ExternalMessage, T extends BridgeMethodResolverTests.ExternalMessageSearchConditions<S>> implements BridgeMethodResolverTests.IExternalMessageProvider<S, T> {
        @Override
        public Collection<S> findBy(T conditions) {
            return null;
        }
    }

    public static class EmailMessage extends BridgeMethodResolverTests.ExternalMessage {}

    public static class EmailSearchConditions extends BridgeMethodResolverTests.ExternalMessageSearchConditions<BridgeMethodResolverTests.EmailMessage> {}

    public static class EmailMessageProvider extends BridgeMethodResolverTests.ExternalMessageProvider<BridgeMethodResolverTests.EmailMessage, BridgeMethodResolverTests.EmailSearchConditions> {}

    public static class TestEmailProvider extends BridgeMethodResolverTests.EmailMessageProvider {
        @Override
        public Collection<BridgeMethodResolverTests.EmailMessage> findBy(BridgeMethodResolverTests.EmailSearchConditions conditions) {
            return null;
        }
    }

    // -------------------
    // SPR-16103 classes
    // -------------------
    public abstract static class BaseEntity {}

    public static class FooEntity extends BridgeMethodResolverTests.BaseEntity {}

    public static class BaseClass<T> {
        public <S extends T> S test(S T) {
            return null;
        }
    }

    public static class EntityClass<T extends BridgeMethodResolverTests.BaseEntity> extends BridgeMethodResolverTests.BaseClass<T> {
        @Override
        public <S extends T> S test(S T) {
            return null;
        }
    }

    public static class FooClass extends BridgeMethodResolverTests.EntityClass<BridgeMethodResolverTests.FooEntity> {
        @Override
        public <S extends BridgeMethodResolverTests.FooEntity> S test(S T) {
            return null;
        }
    }

    public interface BaseInterface<T> {
        <S extends T> S test(S T);
    }

    public interface EntityInterface<T extends BridgeMethodResolverTests.BaseEntity> extends BridgeMethodResolverTests.BaseInterface<T> {
        @Override
        <S extends T> S test(S T);
    }

    public interface FooInterface extends BridgeMethodResolverTests.EntityInterface<BridgeMethodResolverTests.FooEntity> {
        @Override
        <S extends BridgeMethodResolverTests.FooEntity> S test(S T);
    }
}

