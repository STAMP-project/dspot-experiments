/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.modelling.command.inspection;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.persistence.Id;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandMessageHandlingMember;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.annotation.MessageHandlingMember;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.AggregateRoot;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AnnotatedAggregateMetaModelFactoryTest {
    @Test
    public void testDetectAllAnnotatedHandlers() throws Exception {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers.class);
        CommandMessage<?> message = asCommandMessage("ok");
        Assert.assertEquals(true, getHandler(inspector, message).handle(message, new AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers()));
        Assert.assertEquals(false, getHandler(inspector, message).handle(asCommandMessage("ko"), new AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers()));
    }

    @Test
    public void testDetectAllAnnotatedHandlersInHierarchy() throws Exception {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeSubclass> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeSubclass.class);
        AnnotatedAggregateMetaModelFactoryTest.SomeSubclass target = new AnnotatedAggregateMetaModelFactoryTest.SomeSubclass();
        CommandMessage<?> message = asCommandMessage("sub");
        Assert.assertEquals(true, getHandler(inspector, message).handle(message, target));
        Assert.assertEquals(false, getHandler(inspector, message).handle(asCommandMessage("ok"), target));
    }

    @Test
    public void testDetectFactoryMethodHandler() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass.class);
        CommandMessage<?> message = asCommandMessage("string");
        final MessageHandlingMember<? super AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass> messageHandlingMember = getHandler(inspector, message);
        final Optional<CommandMessageHandlingMember> unwrap = messageHandlingMember.unwrap(CommandMessageHandlingMember.class);
        Assert.assertThat(unwrap, CoreMatchers.notNullValue());
        Assert.assertThat(unwrap.isPresent(), CoreMatchers.is(true));
        final CommandMessageHandlingMember commandMessageHandlingMember = unwrap.get();
        Assert.assertThat(commandMessageHandlingMember.isFactoryHandler(), CoreMatchers.is(true));
    }

    @Test
    public void testEventIsPublishedThroughoutRecursiveHierarchy() {
        // Note that if the inspector does not support recursive entities this will throw an StackOverflowError.
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity.class);
        // Create a hierarchy that we will use in this test.
        // The resulting hierarchy will look as follows:
        // root
        // child1
        // child2
        // child3
        // child4
        String rootId = "root";
        String childId1 = "child1";
        String childId2 = "child2";
        String childId3 = "child3";
        String childId4 = "child4";
        AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity root = new AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity(LinkedList::new, null, rootId);
        // Assert root: it should not have any children (yet)
        Assert.assertEquals(0, root.children.size());
        // Publish an event that is picked up by root. It should have 1 child afterwards.
        inspector.publish(asEventMessage(new AnnotatedAggregateMetaModelFactoryTest.CreateChild(rootId, childId1)), root);
        Assert.assertEquals(1, root.children.size());
        // Assert child1: it should not have any children (yet)
        AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity child1 = root.getChild(childId1);
        Assert.assertNotNull(child1);
        Assert.assertEquals(0, child1.children.size());
        // Publish 2 events that are picked up by child1. It should have 2 children afterwards.
        inspector.publish(asEventMessage(new AnnotatedAggregateMetaModelFactoryTest.CreateChild(childId1, childId2)), root);
        inspector.publish(asEventMessage(new AnnotatedAggregateMetaModelFactoryTest.CreateChild(childId1, childId3)), root);
        Assert.assertEquals(2, child1.children.size());
        // Assert child2: it should not have any children
        AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity child2 = child1.getChild(childId2);
        Assert.assertNotNull(child2);
        Assert.assertEquals(0, child2.children.size());
        // Assert child3: it should not have any children
        AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity child3 = child1.getChild(childId3);
        Assert.assertNotNull(child3);
        Assert.assertEquals(0, child3.children.size());
        // Publish an event that is picked up by child3. It should have 1 child afterwards.
        inspector.publish(asEventMessage(new AnnotatedAggregateMetaModelFactoryTest.CreateChild(childId3, childId4)), root);
        Assert.assertEquals(1, child3.children.size());
        // Assert child4: it should not have any children
        AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity child4 = child3.getChild(childId4);
        Assert.assertNotNull(child4);
        Assert.assertEquals(0, child4.children.size());
    }

    @Test
    public void testLinkedListIsModifiedDuringIterationInRecursiveHierarchy() {
        testCollectionIsModifiedDuringIterationInRecursiveHierarchy(LinkedList::new);
    }

    @Test
    public void testHashSetIsModifiedDuringIterationInRecursiveHierarchy() {
        testCollectionIsModifiedDuringIterationInRecursiveHierarchy(HashSet::new);
    }

    @Test
    public void testCopyOnWriteArrayListIsModifiedDuringIterationInRecursiveHierarchy() {
        testCollectionIsModifiedDuringIterationInRecursiveHierarchy(CopyOnWriteArrayList::new);
    }

    @Test
    public void testConcurrentLinkedQueueIsModifiedDuringIterationInRecursiveHierarchy() {
        testCollectionIsModifiedDuringIterationInRecursiveHierarchy(ConcurrentLinkedQueue::new);
    }

    @Test
    public void testEventIsPublishedThroughoutHierarchy() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeSubclass> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeSubclass.class);
        AtomicLong payload = new AtomicLong();
        inspector.publish(new org.axonframework.eventhandling.GenericEventMessage(payload), new AnnotatedAggregateMetaModelFactoryTest.SomeSubclass());
        Assert.assertEquals(2L, payload.get());
    }

    @Test
    public void testExpectCommandToBeForwardedToEntity() throws Exception {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeSubclass> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeSubclass.class);
        GenericCommandMessage<?> message = new GenericCommandMessage(BigDecimal.ONE);
        AnnotatedAggregateMetaModelFactoryTest.SomeSubclass target = new AnnotatedAggregateMetaModelFactoryTest.SomeSubclass();
        MessageHandlingMember<? super AnnotatedAggregateMetaModelFactoryTest.SomeSubclass> handler = getHandler(inspector, message);
        Assert.assertEquals("1", handler.handle(message, target));
    }

    @Test
    public void testFindIdentifier() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers.class);
        Assert.assertEquals("SomeAnnotatedHandlers", inspector.type());
        Assert.assertEquals("id", inspector.getIdentifier(new AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers()));
        Assert.assertEquals("id", inspector.routingKey());
    }

    @Test
    public void testFindJavaxPersistenceIdentifier() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.JavaxPersistenceAnnotatedHandlers> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.JavaxPersistenceAnnotatedHandlers.class);
        Assert.assertEquals("id", inspector.getIdentifier(new AnnotatedAggregateMetaModelFactoryTest.JavaxPersistenceAnnotatedHandlers()));
        Assert.assertEquals("id", inspector.routingKey());
    }

    @Test
    public void testFindIdentifierInSuperClass() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.SomeSubclass> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeSubclass.class);
        Assert.assertEquals("SomeOtherName", inspector.type());
        Assert.assertEquals("id", inspector.getIdentifier(new AnnotatedAggregateMetaModelFactoryTest.SomeSubclass()));
    }

    @Test(expected = AxonConfigurationException.class)
    public void testIllegalFactoryMethodThrowsExceptionClass() {
        AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.SomeIllegalAnnotatedFactoryMethodClass.class);
    }

    @Test(expected = AxonConfigurationException.class)
    public void typedAggregateIdentifier() {
        AggregateModel<AnnotatedAggregateMetaModelFactoryTest.TypedIdentifierAggregate> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(AnnotatedAggregateMetaModelFactoryTest.TypedIdentifierAggregate.class);
        Assert.assertNotNull(inspector.getIdentifier(new AnnotatedAggregateMetaModelFactoryTest.TypedIdentifierAggregate()));
    }

    @Documented
    @EventHandler
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyCustomEventHandler {}

    @Documented
    @CommandHandler
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyCustomCommandHandler {}

    private static class JavaxPersistenceAnnotatedHandlers {
        @Id
        private String id = "id";

        @CommandHandler(commandName = "java.lang.String")
        public boolean handle(CharSequence test) {
            return test.equals("ok");
        }

        @CommandHandler
        public boolean testInt(Integer test) {
            return test > 0;
        }
    }

    private static class SomeAnnotatedHandlers {
        @AggregateIdentifier
        private String id = "id";

        @CommandHandler(commandName = "java.lang.String")
        public boolean handle(CharSequence test) {
            return test.equals("ok");
        }

        @CommandHandler
        public boolean testInt(Integer test) {
            return test > 0;
        }
    }

    @AggregateRoot(type = "SomeOtherName")
    private static class SomeSubclass extends AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedHandlers {
        @AggregateMember
        private AnnotatedAggregateMetaModelFactoryTest.SomeOtherEntity entity = new AnnotatedAggregateMetaModelFactoryTest.SomeOtherEntity();

        @AnnotatedAggregateMetaModelFactoryTest.MyCustomCommandHandler
        public boolean handleInSubclass(String test) {
            return test.contains("sub");
        }

        @AnnotatedAggregateMetaModelFactoryTest.MyCustomEventHandler
        public void handle(AtomicLong value) {
            value.incrementAndGet();
        }
    }

    private static class CustomIdentifier {}

    @AggregateRoot
    private static class TypedIdentifierAggregate {
        @AggregateIdentifier
        private AnnotatedAggregateMetaModelFactoryTest.CustomIdentifier aggregateIdentifier = new AnnotatedAggregateMetaModelFactoryTest.CustomIdentifier();

        @CommandHandler
        public boolean handleInSubclass(String test) {
            return test.contains("sub");
        }

        @EventHandler
        public void handle(AtomicLong value) {
            value.incrementAndGet();
        }
    }

    private static class SomeOtherEntity {
        @CommandHandler
        public String handle(BigDecimal cmd) {
            return cmd.toPlainString();
        }

        @EventHandler
        public void handle(AtomicLong value) {
            value.incrementAndGet();
        }
    }

    private static class CreateChild {
        private final String parentId;

        private final String childId;

        public CreateChild(String parentId, String childId) {
            this.parentId = parentId;
            this.childId = childId;
        }
    }

    private static class MoveChildUp {
        private final String parentId;

        private final String childId;

        private MoveChildUp(String parentId, String childId) {
            this.parentId = parentId;
            this.childId = childId;
        }
    }

    private static class SomeRecursiveEntity {
        private final Supplier<Collection<AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity>> supplier;

        private final AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity parent;

        private final String entityId;

        @AggregateMember
        private final AnnotatedAggregateMetaModelFactoryTest.SomeIterable<AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity> children;

        public SomeRecursiveEntity(Supplier<Collection<AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity>> supplier, AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity parent, String entityId) {
            this.supplier = supplier;
            this.parent = parent;
            this.entityId = entityId;
            this.children = new AnnotatedAggregateMetaModelFactoryTest.SomeIterable<>(supplier.get());
        }

        public AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity getChild(String childId) {
            for (AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity c : children) {
                if (Objects.equals(c.entityId, childId)) {
                    return c;
                }
            }
            return null;
        }

        @EventHandler
        public void handle(AnnotatedAggregateMetaModelFactoryTest.CreateChild event) {
            if (Objects.equals(this.entityId, event.parentId)) {
                children.add(new AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity(supplier, this, event.childId));
            }
        }

        @EventHandler
        public void handle(AnnotatedAggregateMetaModelFactoryTest.MoveChildUp event) {
            if (Objects.equals(this.entityId, event.parentId)) {
                AnnotatedAggregateMetaModelFactoryTest.SomeRecursiveEntity child = getChild(event.childId);
                Assert.assertNotNull(child);
                Assert.assertTrue(this.children.remove(child));
                Assert.assertTrue(parent.children.add(child));
            }
        }
    }

    public static class SomeAnnotatedFactoryMethodClass {
        @AggregateIdentifier
        private String id = "id";

        @CommandHandler
        public static AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass factoryMethod(String id) {
            return new AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass(id);
        }

        SomeAnnotatedFactoryMethodClass(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class SomeIllegalAnnotatedFactoryMethodClass {
        @AggregateIdentifier
        private String id = "id";

        @CommandHandler
        public static Object illegalFactoryMethod(String id) {
            return new AnnotatedAggregateMetaModelFactoryTest.SomeAnnotatedFactoryMethodClass(id);
        }

        SomeIllegalAnnotatedFactoryMethodClass(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    /**
     * Wrapper implementation to ensure that the @AggregateMember field is solely triggered by the fact it implements
     * Iterable, and doesn't depend on any other interface being declared. See issue #461.
     *
     * @param <T>
     * 		The type contained in this iterable
     */
    private static class SomeIterable<T> implements Iterable<T> {
        private final Collection<T> contents;

        public SomeIterable(Collection<T> contents) {
            this.contents = contents;
        }

        @Override
        public Iterator<T> iterator() {
            return contents.iterator();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            contents.forEach(action);
        }

        @Override
        public Spliterator<T> spliterator() {
            return contents.spliterator();
        }

        public boolean add(T item) {
            return contents.add(item);
        }

        public boolean remove(T item) {
            return contents.remove(item);
        }

        public int size() {
            return contents.size();
        }
    }
}

