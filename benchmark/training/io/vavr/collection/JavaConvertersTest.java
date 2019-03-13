/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.collection;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JavaConvertersTest {
    private final JavaConvertersTest.ListFactory listFactory;

    private final JavaConvertersTest.ChangePolicy changePolicy;

    private final JavaConvertersTest.ElementType elementType;

    private final JavaConvertersTest.ElementNullability elementNullability;

    public JavaConvertersTest(String name, JavaConvertersTest.ListFactory listFactory, JavaConvertersTest.ChangePolicy changePolicy, JavaConvertersTest.ElementType elementType, JavaConvertersTest.ElementNullability elementNullability) {
        this.listFactory = listFactory;
        this.changePolicy = changePolicy;
        this.elementType = elementType;
        this.elementNullability = elementNullability;
    }

    // -- add(T)
    @Test
    public void shouldAddElementToEmptyListView() {
        ifSupported(() -> {
            final List<Character> list = empty();
            assertThat(list.add('1')).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('1'));
        });
    }

    @Test
    public void shouldAddElementToEndOfNonEmptyListView() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.add('2')).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    @Test
    public void shouldAddSubtypeToEndOfNonEmptyListView() {
        abstract class A {
            @Override
            public final int hashCode() {
                return 0;
            }
        }
        final class B extends A {
            @Override
            public boolean equals(Object o) {
                return o instanceof B;
            }
        }
        final class C extends A {
            @Override
            public boolean equals(Object o) {
                return o instanceof C;
            }
        }
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<A> list = of(new B());
                assertThat(list.add(new C())).isTrue();
                assertThat(list).isEqualTo(Arrays.asList(new B(), new C()));
            });
        }
    }

    @Test
    public void shouldAddNull() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            ifSupported(() -> {
                final List<Character> list = empty();
                assertThat(list.add(null)).isTrue();
                assertThat(list).isEqualTo(Arrays.asList(((Object) (null))));
            });
        }
    }

    @Test
    public void shouldAddSelf() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Object> list = empty();
                assertThat(list.add(list)).isTrue();
                assertThat(list).isEqualTo(list);
            });
        }
    }

    // -- add(int, T)
    @Test
    public void shouldThrowWhenAddingElementAtNegativeIndexToEmpty() {
        ifSupported(() -> empty().add((-1), null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingElementAtNonExistingIndexToEmpty() {
        ifSupported(() -> empty().add(1, null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingElementAtNegativeIndexToNonEmpty() {
        ifSupported(() -> of('1').add((-1), null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingElementAtNonExistingIndexToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            // should throw for eagerly evaluated collections
            list.add(2, null);
            // afterburner for lazy persistent collections
            list.size();
        }, IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldAddNullToEmptyAtIndex0() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            ifSupported(() -> {
                final List<Character> list = empty();
                list.add(0, null);
                assertThat(list).isEqualTo(Arrays.asList(((Object) (null))));
            });
        }
    }

    @Test
    public void shouldAddNonNullToEmptyAtIndex0() {
        ifSupported(() -> {
            final List<Character> list = empty();
            list.add(0, '1');
            assertThat(list).isEqualTo(Arrays.asList('1'));
        });
    }

    @Test
    public void shouldAddElementAtSizeIndexToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            list.add(1, '2');
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    // -- addAll(Collection)
    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEWhenAddingAllNullCollectionToEmpty() {
        empty().addAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEWhenAddingAllNullCollectionToNonEmpty() {
        of('1').addAll(null);
    }

    @Test
    public void shouldReturnFalseIfAddAllEmptyToEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            final List<Character> javaList = Arrays.asList();
            assertThat(list.addAll(javaList)).isFalse();
            assertThat(list).isEqualTo(javaList);
        });
    }

    @Test
    public void shouldReturnFalseIfAddAllEmptyToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.addAll(Arrays.asList())).isFalse();
            assertThat(list).isEqualTo(of('1'));
        });
    }

    @Test
    public void shouldReturnTrueIfAddAllNonEmptyToEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            final List<Character> javaList = Arrays.asList('1');
            assertThat(list.addAll(javaList)).isTrue();
            assertThat(list).isEqualTo(javaList);
        });
    }

    @Test
    public void shouldReturnTrueIfAddAllNonEmptyToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.addAll(Arrays.asList('1', '2', '3'))).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('1', '2', '3', '1', '2', '3'));
        });
    }

    // -- addAll(int, Collection)
    @Test
    public void shouldThrowNPEWhenAddingAllNullCollectionAtFirstIndexToEmpty() {
        assertThatThrownBy(() -> empty().addAll(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowNPEWhenAddingAllNullCollectionAtFirstIndexToNonEmpty() {
        assertThatThrownBy(() -> of('1').addAll(0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowWhenAddingAllCollectionElementsAtNegativeIndexToEmpty() {
        ifSupported(() -> empty().addAll((-1), Arrays.asList('1', '2', '3')), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingAllCollectionElementsAtNonExistingIndexToEmpty() {
        ifSupported(() -> empty().addAll(1, Arrays.asList('1', '2', '3')), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingAllCollectionElementsAtNegativeIndexToNonEmpty() {
        ifSupported(() -> of('1').addAll((-1), Arrays.asList('1', '2', '3')), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenAddingAllCollectionElementsAtNonExistingIndexToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            // should throw for eagerly evaluated collections
            list.addAll(2, Arrays.asList('1', '2', '3'));
            // afterburner for lazy persistent collections
            list.size();
        }, IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldAddAllCollectionElementsAtFirstIndexToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('4');
            assertThat(list.addAll(0, Arrays.asList('1', '2', '3'))).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('1', '2', '3', '4'));
        });
    }

    @Test
    public void shouldAddAllCollectionElementsAtSizeIndexToEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            final List<Character> javaList = Arrays.asList('1', '2', '3');
            assertThat(list.addAll(0, javaList)).isTrue();
            assertThat(list).isEqualTo(javaList);
        });
    }

    @Test
    public void shouldAddAllCollectionElementsAtSizeIndexToNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.addAll(1, Arrays.asList('2', '3'))).isTrue();
            assertThat(list).isEqualTo(of('1', '2', '3'));
        });
    }

    // -- clear()
    @Test
    public void shouldThrowWhenCallingClearOnEmpty() {
        final List<Character> empty = empty();
        empty.clear();
        assertThat(empty).isEqualTo(Arrays.asList());
    }

    @Test
    public void shouldThrowWhenCallingClearOnNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            list.clear();
            assertThat(list).isEqualTo(Arrays.asList());
        });
    }

    // -- contains(Object)
    @Test
    public void shouldRecognizeThatEmptyListViewDoesNotContainElement() {
        assertThat(empty().contains('1')).isFalse();
    }

    @Test
    public void shouldRecognizeThatNonEmptyListViewDoesNotContainElement() {
        assertThat(of('1').contains('2')).isFalse();
    }

    @Test
    public void shouldRecognizeThatNWhenEmptyListViewContainElement() {
        assertThat(of('1').contains('1')).isTrue();
    }

    @Test
    public void shouldEnsureThatEmptyListViewContainsPermitsNullElements() {
        assertThat(empty().contains(null)).isFalse();
    }

    @Test
    public void shouldEnsureThatNonEmptyListViewContainsPermitsNullElements() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            assertThat(ofNull().contains(null)).isTrue();
        }
    }

    @Test
    public void shouldBehaveLikeStandardJavaWhenCallingContainsOfIncompatibleTypeOnEmpty() {
        assertThat(empty().contains("")).isFalse();
    }

    @Test
    public void shouldBehaveLikeStandardJavaWhenCallingContainsOfIncompatibleTypeWhenNotEmpty() {
        assertThat(of('1').contains("")).isFalse();
    }

    @Test
    public void shouldRecognizeListContainsSelf() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Object> list = empty();
                list.add(list);
                assertThat(list.contains(list)).isTrue();
            });
        }
    }

    // -- containsAll(Collection)
    @Test
    public void shouldThrowNPEWhenCallingContainsAllNullWhenEmpty() {
        assertThatThrownBy(() -> empty().containsAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowNPEWhenCallingContainsAllNullWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').containsAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRecognizeNonEmptyContainsAllEmpty() {
        assertThat(of('1').containsAll(Arrays.asList())).isTrue();
    }

    @Test
    public void shouldRecognizeNonEmptyContainsAllNonEmpty() {
        assertThat(of('1').containsAll(Arrays.asList('1'))).isTrue();
    }

    @Test
    public void shouldRecognizeNonEmptyNotContainsAllNonEmpty() {
        assertThat(of('1', '2').containsAll(Arrays.asList('1', '3'))).isFalse();
    }

    @Test
    public void shouldRecognizeEmptyContainsAllEmpty() {
        assertThat(empty().containsAll(Arrays.asList())).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnEmptyContainsAllGivenNull() {
        empty().containsAll(null);
    }

    @Test
    public void shouldRecognizeListContainsAllSelf() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Object> list = empty();
                list.add(list);
                assertThat(list.containsAll(list)).isTrue();
            });
        }
    }

    // -- equals(Object)
    @Test
    public void shouldRecognizeEqualsSame() {
        final List<Character> list = of('1');
        assertThat(list.equals(list)).isTrue();
    }

    @Test
    public void shouldRecognizeEmptyEqualsEmpty() {
        assertThat(empty().equals(empty())).isTrue();
    }

    @Test
    public void shouldRecognizeNonEmptyEqualsNonEmpty() {
        assertThat(of('1').equals(of('1'))).isTrue();
    }

    @Test
    public void shouldRecognizeNonEmptyNotEqualsNonEmpty() {
        assertThat(of('1', '2').equals(of('1', '3'))).isFalse();
    }

    @Test
    public void shouldRecognizeEmptyNotEqualsNonEmpty() {
        assertThat(empty().equals(of('1'))).isFalse();
    }

    @Test
    public void shouldRecognizeNonEmptyNotEqualsEmpty() {
        assertThat(of('1').equals(empty())).isFalse();
    }

    @Test
    public void shouldRecognizeEmptyNotEqualsNull() {
        assertThat(empty().equals(null)).isFalse();
    }

    @Test
    public void shouldRecognizeNonEmptyNotEqualsNull() {
        assertThat(of('1').equals(null)).isFalse();
    }

    @Test
    public void shouldRecognizeNonEqualityOfSameValuesOfDifferentType() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            assertThat(of(1).equals(of(1.0))).isFalse();
        }
    }

    @Test
    public void shouldRecognizeSelfEqualityOfListThatContainsItself() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Object> list = empty();
                list.add(list);
                assertThat(list.equals(list)).isTrue();
            });
        }
    }

    // -- get(int)
    @Test
    public void shouldThrowWhenEmptyGetWithNegativeIndex() {
        assertThatThrownBy(() -> empty().get((-1))).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenEmptyGetWithIndexEqualsSize() {
        assertThatThrownBy(() -> empty().get(0)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenNonEmptyGetWithNegativeIndex() {
        assertThatThrownBy(() -> of('1').get((-1))).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenNonEmptyGetWithIndexEqualsSize() {
        assertThatThrownBy(() -> of('1').get(1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldGetAtFirstIndex() {
        assertThat(of('1', '2', '3').get(0)).isEqualTo('1');
    }

    @Test
    public void shouldGetAtLastIndex() {
        assertThat(of('1', '2', '3').get(2)).isEqualTo('3');
    }

    // -- hashCode()
    @Test
    public void shouldCalculateHashCodeOfEmptyLikeJava() {
        assertThat(empty().hashCode()).isEqualTo(Arrays.asList().hashCode());
    }

    @Test
    public void shouldCalculateHashCodeOfNonEmptyLikeJava() {
        assertThat(of('1', '2', '3').hashCode()).isEqualTo(Arrays.asList('1', '2', '3').hashCode());
    }

    @Test
    public void shouldThrowInsteadOfLoopingInfinitelyWhenComputingHashCodeOfListThatContainsItself() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Object> list = empty();
                list.add(list);
                list.hashCode();
            }, StackOverflowError.class);
        }
    }

    // -- indexOf(Object)
    @Test
    public void shouldReturnIndexOfNonExistingElementWhenEmpty() {
        assertThat(empty().indexOf('0')).isEqualTo((-1));
    }

    @Test
    public void shouldReturnIndexOfNonExistingElementWhenNonEmpty() {
        assertThat(of('1', '2', '3').indexOf('0')).isEqualTo((-1));
    }

    @Test
    public void shouldReturnIndexOfFirstOccurrenceWhenNonEmpty() {
        assertThat(of('1', '2', '3', '2').indexOf('2')).isEqualTo(1);
    }

    @Test
    public void shouldReturnIndexOfNullWhenNonEmpty() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            assertThat(of('1', null, '2', null).indexOf(null)).isEqualTo(1);
        }
    }

    @Test
    public void shouldReturnIndexOfWrongTypedElementWhenNonEmpty() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            assertThat(of('1', '2').indexOf("a")).isEqualTo((-1));
        }
    }

    // -- isEmpty()
    @Test
    public void shouldReturnTrueWhenCallingIsEmptyWhenEmpty() {
        assertThat(empty().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenCallingIsEmptyWhenNotEmpty() {
        assertThat(of('1').isEmpty()).isFalse();
    }

    // -- iterator()
    @Test
    public void shouldReturnIteratorWhenEmpty() {
        assertThat(empty().iterator()).isNotNull();
    }

    @Test
    public void shouldReturnIteratorWhenNotEmpty() {
        assertThat(of('1').iterator()).isNotNull();
    }

    @Test
    public void shouldReturnEmptyIteratorWhenEmpty() {
        assertThat(empty().iterator().hasNext()).isFalse();
    }

    @Test
    public void shouldReturnNonEmptyIteratorWhenNotEmpty() {
        assertThat(of('1').iterator().hasNext()).isTrue();
    }

    @Test
    public void shouldThrowWhenCallingNextOnIteratorWhenEmpty() {
        assertThatThrownBy(() -> empty().iterator().next()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldNotThrowWhenCallingNextOnIteratorWhenNotEmpty() {
        assertThat(of('1').iterator().next()).isEqualTo('1');
    }

    @Test
    public void shouldThrowWhenCallingNextTooOftenOnIteratorWhenNotEmpty() {
        final Iterator<Character> iterator = of('1').iterator();
        iterator.next();
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldIterateAsExpectedWhenCallingIteratorWhenNotEmpty() {
        final Iterator<Character> iterator = of('1', '2', '3').iterator();
        assertThat(iterator.next()).isEqualTo('1');
        assertThat(iterator.next()).isEqualTo('2');
        assertThat(iterator.next()).isEqualTo('3');
    }

    @Test
    public void shouldNotHaveNextWhenAllIteratorElementsWereConsumedByNext() {
        final Iterator<Character> iterator = of('1').iterator();
        iterator.next();
        assertThat(iterator.hasNext()).isFalse();
    }

    // -- iterator().forEachRemaining()
    @Test
    public void shouldPerformNoSideEffectForEachRemainingOfEmpty() {
        final List<Character> actual = new ArrayList<>();
        this.<Character>empty().<Character>iterator().forEachRemaining(actual::add);
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    public void shouldPerformNoSideEffectsForEachRemainingOfNonEmptyButAllIterated() {
        final List<Character> actual = new ArrayList<>();
        final Iterator<Character> iterator = of('1').iterator();
        iterator.next();
        iterator.forEachRemaining(actual::add);
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    public void shouldPerformSideEffectsForEachRemainingOfNonEmpty() {
        final List<Character> actual = new ArrayList<>();
        final Iterator<Character> iterator = of('1', '2').iterator();
        iterator.next();
        iterator.forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Arrays.asList('2'));
    }

    @Test
    public void shouldThrowWhenRemovingElementFromListWhileIteratingForEachRemaining() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final Iterator<Character> iterator = list.iterator();
            iterator.forEachRemaining(list::remove);
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldThrowWhenAddingElementFromListWhileIteratingForEachRemaining() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final Iterator<Character> iterator = list.iterator();
            iterator.forEachRemaining(list::add);
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldThrowWhenRemovingElementFromIteratorWhileIteratingForEachRemaining() {
        ifSupported(() -> {
            final Iterator<Character> iterator = of('1', '2').iterator();
            iterator.forEachRemaining(( e) -> iterator.remove());
        }, IllegalStateException.class);
    }

    // -- iterator().remove()
    @Test
    public void shouldThrowWhenCallingRemoveOnEmptyIterator() {
        ifSupported(() -> empty().iterator().remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveOnNonEmptyIteratorWithoutHavingCalledNext() {
        ifSupported(() -> of('1').iterator().remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveTwiceOnNonEmptyIteratorAfterHavingCalledNext() {
        ifSupported(() -> {
            final Iterator<Character> iter = of('1', '2', '3').iterator();
            iter.next();
            iter.remove();
            iter.remove();// should fail

        }, IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenModifyingListWhileIteratingAndRemoving() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final Iterator<Character> iter = list.iterator();
            iter.next();
            list.add('4');
            iter.remove();
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldRemoveFirstListElementWhenIterating() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final Iterator<Character> iter = list.iterator();
            iter.next();
            iter.remove();
            assertThat(list).isEqualTo(Arrays.asList('2', '3'));
        });
    }

    @Test
    public void shouldRemoveInnerListElementWhenIterating() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final Iterator<Character> iter = list.iterator();
            iter.next();
            iter.next();
            iter.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldRemoveLastListElementWhenIterating() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final Iterator<Character> iter = list.iterator();
            iter.next();
            iter.next();
            iter.next();
            iter.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    // -- lastIndexOf()
    @Test
    public void shouldReturnLastIndexOfNonExistingElementWhenEmpty() {
        assertThat(empty().lastIndexOf('0')).isEqualTo((-1));
    }

    @Test
    public void shouldReturnLastIndexOfNonExistingElementWhenNonEmpty() {
        assertThat(of('1', '2', '3').lastIndexOf('0')).isEqualTo((-1));
    }

    @Test
    public void shouldReturnLastIndexOfFirstOccurrenceWhenNonEmpty() {
        assertThat(of('1', '2', '3', '2').lastIndexOf('2')).isEqualTo(3);
    }

    @Test
    public void shouldReturnLastIndexOfNullWhenNonEmpty() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            assertThat(of('1', null, '2', null).lastIndexOf(null)).isEqualTo(3);
        }
    }

    @Test
    public void shouldReturnLastIndexOfWrongTypedElementWhenNonEmpty() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            assertThat(of('1', null, '2').lastIndexOf("a")).isEqualTo((-1));
        }
    }

    // -- listIterator()
    @Test
    public void shouldReturnListIteratorWhenEmpty() {
        assertThat(empty().listIterator()).isNotNull();
    }

    @Test
    public void shouldReturnListIteratorWhenNotEmpty() {
        assertThat(of('1').listIterator()).isNotNull();
    }

    // -- listIterator().add()
    @Test
    public void shouldUseListIteratorToAddElementToEmptyList() {
        ifSupported(() -> {
            final List<Character> list = empty();
            list.listIterator().add('1');
            assertThat(list).isEqualTo(Arrays.asList('1'));
        });
    }

    @Test
    public void shouldAddElementToListIteratorStart() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            list.listIterator().add('2');
            assertThat(list).isEqualTo(Arrays.asList('2', '1'));
        });
    }

    @Test
    public void shouldAddingElementToListIteratorEnd() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.add('2');
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    @Test
    public void shouldAddElementHavingWrongTypeToListIterator() {
        if ((elementType) == (JavaConvertersTest.ElementType.GENERIC)) {
            ifSupported(() -> {
                final List<Character> list = of('1');
                @SuppressWarnings("unchecked")
                final ListIterator<Object> listIterator = ((ListIterator<Object>) ((Object) (list.listIterator())));
                listIterator.add("x");
                assertThat(list).isEqualTo(Arrays.asList("x", '1'));
            });
        }
    }

    @Test
    public void shouldReturnUnaffectedNextWhenCallingAddOnListIterator() {
        ifSupported(() -> {
            final ListIterator<Character> listIterator = of('1').listIterator();
            listIterator.add('2');
            assertThat(listIterator.next()).isEqualTo('1');
        });
    }

    @Test
    public void shouldReturnNewElementWhenCallingAddAndThenPreviousOnListIterator() {
        ifSupported(() -> {
            final ListIterator<Character> listIterator = of('1').listIterator();
            listIterator.next();
            listIterator.add('2');
            assertThat(listIterator.previous()).isEqualTo('2');
        });
    }

    @Test
    public void shouldIncreaseNextIndexByOneWhenCallingAddOnListIterator() {
        ifSupported(() -> {
            final ListIterator<Character> listIterator = of('1').listIterator();
            assertThat(listIterator.nextIndex()).isEqualTo(0);
            listIterator.add('2');
            assertThat(listIterator.nextIndex()).isEqualTo(1);
        });
    }

    @Test
    public void shouldIncreasePreviousIndexByOneWhenCallingAddOnListIterator() {
        ifSupported(() -> {
            final ListIterator<Character> listIterator = of('1').listIterator();
            assertThat(listIterator.previousIndex()).isEqualTo((-1));
            listIterator.add('2');
            assertThat(listIterator.previousIndex()).isEqualTo(0);
        });
    }

    // -- listIterator().hasNext()
    @Test
    public void shouldNotHaveNextWhenEmptyListIterator() {
        assertThat(empty().listIterator().hasNext()).isFalse();
    }

    @Test
    public void shouldHaveNextWhenNonEmptyListIterator() {
        assertThat(of('1').listIterator().hasNext()).isTrue();
    }

    @Test
    public void shouldNotHaveNextWhenAllListIteratorElementsWereConsumedByNext() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        assertThat(listIterator.hasNext()).isTrue();
        listIterator.next();
        assertThat(listIterator.hasNext()).isFalse();
    }

    // -- listIterator().next()
    @Test
    public void shouldThrowWhenCallingNextOnListIteratorWhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator().next()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldNotThrowWhenCallingNextOnListIteratorWhenNotEmpty() {
        assertThat(of('1').listIterator().next()).isEqualTo('1');
    }

    @Test
    public void shouldThrowWhenCallingNextTooOftenOnListIteratorWhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        listIterator.next();
        assertThatThrownBy(listIterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldIterateAsExpectedWhenCallingListIteratorWhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1', '2', '3').listIterator();
        assertThat(listIterator.next()).isEqualTo('1');
        assertThat(listIterator.next()).isEqualTo('2');
        assertThat(listIterator.next()).isEqualTo('3');
    }

    @Test
    public void shouldThrowWhenCallingListIteratorNextAndListElementWasRemoved() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            assertThat(listIterator.hasNext()).isTrue();
            list.remove(0);
            listIterator.next();
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldThrowWhenCallingListIteratorNextAndListElementWasAdded() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            assertThat(listIterator.hasNext()).isTrue();
            list.add('2');
            listIterator.next();
        }, ConcurrentModificationException.class);
    }

    // -- listIterator().nextIndex()
    @Test
    public void shouldReturnNextIndexOfEmptyListIterator() {
        assertThat(empty().listIterator().nextIndex()).isEqualTo(0);
    }

    @Test
    public void shouldReturnNextIndexOfNonEmptyListIterator() {
        assertThat(of('1').listIterator().nextIndex()).isEqualTo(0);
    }

    @Test
    public void shouldReturnCorrectNextIndexOfListIteratorAfterIteratingAllElements() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        listIterator.next();
        assertThat(listIterator.nextIndex()).isEqualTo(1);
    }

    // -- listIterator().hasPrevious()
    @Test
    public void shouldNotHavePreviousWhenEmptyListIterator() {
        assertThat(empty().listIterator().hasPrevious()).isFalse();
    }

    @Test
    public void shouldNotHavePreviousWhenNonEmptyListIterator() {
        assertThat(of('1').listIterator().hasPrevious()).isFalse();
    }

    @Test
    public void shouldNotHavePreviousWhenAllListIteratorElementsWereConsumedByPrevious() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        listIterator.next();
        assertThat(listIterator.hasPrevious()).isTrue();
        listIterator.previous();
        assertThat(listIterator.hasPrevious()).isFalse();
    }

    // -- listIterator().previous()
    @Test
    public void shouldThrowWhenCallingPreviousOnListIteratorWhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator().previous()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldThrowWhenCallingPreviousOnListIteratorWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').listIterator().previous()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldRepeatedlyReturnTheSameElementWhenAlternatingNextAndPrevious() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        final List<Character> actual = new ArrayList<>();
        actual.add(listIterator.next());
        actual.add(listIterator.previous());
        actual.add(listIterator.next());
        actual.add(listIterator.previous());
        assertThat(actual).isEqualTo(Arrays.asList('1', '1', '1', '1'));
    }

    @Test
    public void shouldIterateListIteratorBackwards() {
        final ListIterator<Character> listIterator = of('1', '2', '3', '4').listIterator();
        final List<Character> actual = new ArrayList<>();
        while (listIterator.hasNext()) {
            listIterator.next();
        } 
        while (listIterator.hasPrevious()) {
            actual.add(listIterator.previous());
        } 
        assertThat(actual).isEqualTo(Arrays.asList('4', '3', '2', '1'));
    }

    @Test
    public void shouldThrowWhenCallingPreviousTooOftenOnListIteratorWhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        listIterator.next();
        listIterator.previous();
        assertThatThrownBy(listIterator::previous).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldThrowWhenCallingListIteratorPreviousAndListElementWasRemoved() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            assertThat(listIterator.hasPrevious()).isFalse();
            listIterator.next();
            assertThat(listIterator.hasPrevious()).isTrue();
            list.remove(0);
            listIterator.previous();
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldThrowWhenCallingListIteratorPreviousAndListElementWasAdded() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            assertThat(listIterator.hasPrevious()).isFalse();
            listIterator.next();
            assertThat(listIterator.hasPrevious()).isTrue();
            list.add('2');
            listIterator.previous();
        }, ConcurrentModificationException.class);
    }

    // -- listIterator.previousIndex()
    @Test
    public void shouldReturnPreviousIndexOfEmptyListIterator() {
        assertThat(empty().listIterator().previousIndex()).isEqualTo((-1));
    }

    @Test
    public void shouldReturnPreviousIndexOfNonEmptyListIterator() {
        assertThat(of('1').listIterator().previousIndex()).isEqualTo((-1));
    }

    @Test
    public void shouldReturnCorrectPreviousIndexOfListIteratorAfterIteratingAllElements() {
        final ListIterator<Character> listIterator = of('1').listIterator();
        listIterator.next();
        assertThat(listIterator.previousIndex()).isEqualTo(0);
    }

    // -- listIterator().remove()
    @Test
    public void shouldThrowWhenCallingRemoveOnEmptyListIterator() {
        ifSupported(() -> empty().listIterator().remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveOnNonEmptyListIterator() {
        ifSupported(() -> of('1').listIterator().remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterRemoveHasBeenCalledAfterTheLastCallOfNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            listIterator.remove();
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterRemoveHasBeenCalledAfterTheLastCallOfPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            listIterator.previous();
            listIterator.remove();
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterAddHasBeenCalledAfterTheLastCallOfNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.add('4');
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterAddHasBeenCalledAfterTheLastCallOfPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.previous();
            listIterator.add('4');
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldRemoveLastElementOfListIteratorThatWasReturnedByNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            assertThat(listIterator.next()).isEqualTo('2');
            listIterator.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldRemoveLastElementOfListIteratorThatWasReturnedByPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            assertThat(listIterator.previous()).isEqualTo('2');
            listIterator.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveOnListIteratorAfterListWasChanged() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            list.add('2');
            assertThatThrownBy(listIterator::remove).isInstanceOf(ConcurrentModificationException.class);
        });
    }

    // -- listIterator().set()
    @Test
    public void shouldThrowWhenCallingSetOnEmptyListIterator() {
        ifSupported(() -> empty().listIterator().set('0'), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingSetOnNonEmptyListIterator() {
        ifSupported(() -> of('1').listIterator().set('0'), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingSetAfterRemoveHasBeenCalledAfterTheLastCallOfNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            listIterator.remove();
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterRemoveHasBeenCalledAfterTheLastCallOfPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            listIterator.previous();
            listIterator.remove();
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterAddHasBeenCalledAfterTheLastCallOfNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.add('4');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterAddHasBeenCalledAfterTheLastCallOfPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.previous();
            listIterator.add('4');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldSetLastElementOfListIteratorThatWasReturnedByNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            assertThat(listIterator.next()).isEqualTo('2');
            listIterator.set('0');
            assertThat(list).isEqualTo(Arrays.asList('1', '0', '3'));
        });
    }

    @Test
    public void shouldSetLastElementOfListIteratorThatWasReturnedByPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            listIterator.next();
            assertThat(listIterator.previous()).isEqualTo('2');
            listIterator.set('0');
            assertThat(list).isEqualTo(Arrays.asList('1', '0', '3'));
        });
    }

    @Test
    public void shouldThrowWhenCallingSetOnListIteratorAfterListWasChanged() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator();
            listIterator.next();
            list.add('2');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(ConcurrentModificationException.class);
        });
    }

    // -- listIterator(int)
    @Test
    public void shouldThrowWhenListIteratorAtNegativeIndexWhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator((-1))).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenListIteratorAtNegativeIndexWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').listIterator((-1))).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldNotThrowWhenListIteratorAtSizeIndexWhenEmpty() {
        assertThat(empty().listIterator(0)).isNotNull();
    }

    @Test
    public void shouldNotThrowWhenListIteratorAtSizeIndexWhenNotEmpty() {
        assertThat(of('1').listIterator(1)).isNotNull();
    }

    @Test
    public void shouldThrowWhenListIteratorAtIndexGreaterSizeWhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator(1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenListIteratorAtIndexGreaterSizeWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').listIterator(2)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    // -- listIterator(int).hasNext()
    @Test
    public void shouldReturnEmptyListIteratorAtFirstIndexWhenEmpty() {
        assertThat(empty().listIterator(0).hasNext()).isFalse();
    }

    @Test
    public void shouldReturnNonEmptyListIteratorAtFirstIndexWhenNotEmpty() {
        assertThat(of('1').listIterator(0).hasNext()).isTrue();
    }

    @Test
    public void shouldNotHaveNextWhenAllListIteratorElementsAtFirstIndexWereConsumedByNext() {
        final ListIterator<Character> listIterator = of('1').listIterator(0);
        listIterator.next();
        assertThat(listIterator.hasNext()).isFalse();
    }

    // -- listIterator(int).next()
    @Test
    public void shouldThrowWhenCallingNextOnListIteratorAtFirstIndexWhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator(0).next()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldNotThrowWhenCallingNextOnListIteratorAtFirstIndexWhenNotEmpty() {
        assertThat(of('1').listIterator(0).next()).isEqualTo('1');
    }

    @Test
    public void shouldThrowWhenCallingNextTooOftenOnListIteratorAtFirstIndexWhenNotEmpty() {
        final Iterator<Character> listIterator = of('1').listIterator(0);
        listIterator.next();
        assertThatThrownBy(listIterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldIterateAsExpectedWhenCallingListIteratorAtFirstIndexWhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1', '2', '3').listIterator(0);
        assertThat(listIterator.next()).isEqualTo('1');
        assertThat(listIterator.next()).isEqualTo('2');
        assertThat(listIterator.next()).isEqualTo('3');
    }

    @Test
    public void shouldIterateAsExpectedWhenCallingListIteratorAtNonFirstIndexWhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1', '2', '3').listIterator(1);
        assertThat(listIterator.next()).isEqualTo('2');
        assertThat(listIterator.next()).isEqualTo('3');
    }

    // -- listIterator().nextIndex()
    @Test
    public void shouldReturnNextIndexOfEmptyListIteratorAtFirstIndex() {
        assertThat(empty().listIterator(0).nextIndex()).isEqualTo(0);
    }

    @Test
    public void shouldReturnNextIndexOfNonEmptyListIteratorAtIndex1() {
        assertThat(of('1').listIterator(1).nextIndex()).isEqualTo(1);
    }

    @Test
    public void shouldReturnCorrectNextIndexOfListIteratorAtIndex1AfterIteratingAllElements() {
        final ListIterator<Character> listIterator = of('1', '2').listIterator(1);
        listIterator.next();
        assertThat(listIterator.nextIndex()).isEqualTo(2);
    }

    // -- listIterator().hasPrevious()
    @Test
    public void shouldNotHavePreviousWhenEmptyListIteratorAtIndex0() {
        assertThat(empty().listIterator(0).hasPrevious()).isFalse();
    }

    @Test
    public void shouldHavePreviousWhenNonEmptyListIteratorAtIndex1() {
        assertThat(of('1').listIterator(1).hasPrevious()).isTrue();
    }

    @Test
    public void shouldNotHavePreviousWhenAllListIteratorAtIndex0ElementsWereConsumedByPrevious() {
        final ListIterator<Character> listIterator = of('1').listIterator(1);
        assertThat(listIterator.hasPrevious()).isTrue();
        listIterator.previous();
        assertThat(listIterator.hasPrevious()).isFalse();
    }

    // -- listIterator().previous()
    @Test
    public void shouldThrowWhenCallingPreviousOnListIteratorAtIndex0WhenEmpty() {
        assertThatThrownBy(() -> empty().listIterator(0).previous()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldThrowWhenCallingPreviousOnListIteratorAtIndex0WhenNotEmpty() {
        assertThatThrownBy(() -> of('1').listIterator(0).previous()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldRepeatedlyReturnTheSameElementWhenAlternatingNextAndPreviousAtIndex1() {
        final ListIterator<Character> listIterator = of('1').listIterator(1);
        final List<Character> actual = new ArrayList<>();
        actual.add(listIterator.previous());
        actual.add(listIterator.next());
        actual.add(listIterator.previous());
        actual.add(listIterator.next());
        assertThat(actual).isEqualTo(Arrays.asList('1', '1', '1', '1'));
    }

    @Test
    public void shouldIterateListIteratorAtLastIndexBackwards() {
        final ListIterator<Character> listIterator = of('1', '2', '3', '4').listIterator(4);
        final List<Character> actual = new ArrayList<>();
        while (listIterator.hasPrevious()) {
            actual.add(listIterator.previous());
        } 
        assertThat(actual).isEqualTo(Arrays.asList('4', '3', '2', '1'));
    }

    @Test
    public void shouldThrowWhenCallingPreviousTooOftenOnListIteratorAtIndex1WhenNotEmpty() {
        final ListIterator<Character> listIterator = of('1').listIterator(1);
        listIterator.previous();
        assertThatThrownBy(listIterator::previous).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldThrowWhenCallingListIteratorAtIndex1PreviousAndListElementWasRemoved() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator(1);
            assertThat(listIterator.hasPrevious()).isTrue();
            list.remove(0);
            listIterator.previous();
        }, ConcurrentModificationException.class);
    }

    @Test
    public void shouldThrowWhenCallingListIteratorAtIndex1PreviousAndListElementWasAdded() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            final ListIterator<Character> listIterator = list.listIterator(1);
            assertThat(listIterator.hasPrevious()).isTrue();
            list.add('2');
            listIterator.previous();
        }, ConcurrentModificationException.class);
    }

    // -- listIterator.previousIndex()
    @Test
    public void shouldReturnPreviousIndexOfEmptyListIteratorAtIndex0() {
        assertThat(empty().listIterator(0).previousIndex()).isEqualTo((-1));
    }

    @Test
    public void shouldReturnPreviousIndexOfNonEmptyListIteratorAtIndex1() {
        assertThat(of('1').listIterator(1).previousIndex()).isEqualTo(0);
    }

    @Test
    public void shouldReturnCorrectPreviousIndexOfListIteratorAtIndex1AfterIteratingAllElements() {
        final ListIterator<Character> listIterator = of('1', '2').listIterator(1);
        listIterator.next();
        assertThat(listIterator.previousIndex()).isEqualTo(1);
    }

    // -- listIterator().remove()
    @Test
    public void shouldThrowWhenCallingRemoveOnEmptyListIteratorAtIndex0() {
        ifSupported(() -> empty().listIterator(0).remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveOnNonEmptyListIteratorAtIndex1() {
        ifSupported(() -> of('1').listIterator(1).remove(), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterRemoveHasBeenCalledAfterTheLastCallOfNextAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.remove();
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterRemoveHasBeenCalledAfterTheLastCallOfPreviousAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.previous();
            listIterator.remove();
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterAddHasBeenCalledAfterTheLastCallOfNextAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.add('4');
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveAfterAddHasBeenCalledAfterTheLastCallOfPreviousAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.previous();
            listIterator.add('4');
            assertThatThrownBy(listIterator::remove).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldRemoveLastElementOfListIteratorAtIndex1ThatWasReturnedByNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            assertThat(listIterator.next()).isEqualTo('2');
            listIterator.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldRemoveLastElementOfListIteratorAtIndex1ThatWasReturnedByPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            assertThat(listIterator.previous()).isEqualTo('2');
            listIterator.remove();
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldThrowWhenCallingRemoveOnListIteratorAtIndex1AfterListWasChanged() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            list.add('2');
            assertThatThrownBy(listIterator::remove).isInstanceOf(ConcurrentModificationException.class);
        });
    }

    // -- listIterator().set()
    @Test
    public void shouldThrowWhenCallingSetOnEmptyListIteratorAtIndex0() {
        ifSupported(() -> empty().listIterator(0).set('0'), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingSetOnNonEmptyListIteratorAtIndex1() {
        ifSupported(() -> of('1', '2').listIterator(1).set('0'), IllegalStateException.class);
    }

    @Test
    public void shouldThrowWhenCallingSetAfterRemoveHasBeenCalledAfterTheLastCallOfNextAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.remove();
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterRemoveHasBeenCalledAfterTheLastCallOfPreviousAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.previous();
            listIterator.remove();
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterAddHasBeenCalledAfterTheLastCallOfNextAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.add('4');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldThrowWhenCallingSetAfterAddHasBeenCalledAfterTheLastCallOfPreviousAtIndex1() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            listIterator.previous();
            listIterator.add('4');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    public void shouldSetLastElementOfListIteratorAtIndex1ThatWasReturnedByNext() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            assertThat(listIterator.next()).isEqualTo('3');
            listIterator.set('0');
            assertThat(list).isEqualTo(Arrays.asList('1', '2', '0'));
        });
    }

    @Test
    public void shouldSetLastElementOfListIteratorAtIndex1ThatWasReturnedByPrevious() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            assertThat(listIterator.previous()).isEqualTo('2');
            listIterator.set('0');
            assertThat(list).isEqualTo(Arrays.asList('1', '0', '3'));
        });
    }

    @Test
    public void shouldThrowWhenCallingSetOnListIteratorAtIndex1AfterListWasChanged() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            final ListIterator<Character> listIterator = list.listIterator(1);
            listIterator.next();
            list.add('3');
            assertThatThrownBy(() -> listIterator.set('0')).isInstanceOf(ConcurrentModificationException.class);
        });
    }

    // -- remove(int)
    @Test
    public void shouldThrowWhenRemovingElementAtNegativeIndexWhenEmpty() {
        ifSupported(() -> empty().remove((-1)), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenRemovingElementAtNegativeIndexWhenNotEmpty() {
        ifSupported(() -> of('1').remove((-1)), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenRemovingElementAtSizeIndexWhenEmpty() {
        ifSupported(() -> empty().remove(0), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenRemovingElementAtSizeIndexWhenNotEmpty() {
        ifSupported(() -> of('1').remove(1), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldRemoveTheElementAtFirstIndex() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.remove(0)).isEqualTo('1');
            assertThat(list).isEqualTo(Arrays.asList('2', '3'));
        });
    }

    @Test
    public void shouldRemoveTheElementAtInnerIndex() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.remove(1)).isEqualTo('2');
            assertThat(list).isEqualTo(Arrays.asList('1', '3'));
        });
    }

    @Test
    public void shouldRemoveTheElementAtLastIndex() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.remove(2)).isEqualTo('3');
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    @Test
    public void shouldRemoveAllUsingFirstIndex() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            list.remove(0);
            list.remove(0);
            list.remove(0);
            assertThat(list).isEmpty();
        });
    }

    @Test
    public void shouldRemoveAllUsingDescendingIndices() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            list.remove(2);
            list.remove(1);
            list.remove(0);
            assertThat(list).isEmpty();
        });
    }

    @Test
    public void shouldThrowWhenTryingToRemoveMoreElementsThanPresent() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            list.remove(0);
            list.remove(0);
            assertThatThrownBy(() -> list.remove(0)).isInstanceOf(IndexOutOfBoundsException.class);
        });
    }

    // -- remove(Object)
    @Test
    public void shouldRemoveElementFromEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            assertThat(list.remove(((Object) ('1')))).isFalse();
            assertThat(list).isEqualTo(empty());
        });
    }

    @Test
    public void shouldRemoveNonExistingElementFromNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            assertThat(list.remove(((Object) ('3')))).isFalse();
            assertThat(list).isEqualTo(of('1', '2'));
        });
    }

    @Test
    public void shouldRemoveExistingElementFromNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.remove(((Object) ('2')))).isTrue();
            assertThat(list).isEqualTo(of('1', '3'));
        });
    }

    @Test
    public void shouldRemoveNull() {
        if ((elementNullability) == (JavaConvertersTest.ElementNullability.NULLABLE)) {
            ifSupported(() -> {
                final List<Character> list = of('1', null, '2');
                assertThat(list.remove(null)).isTrue();
                assertThat(list).isEqualTo(of('1', '2'));
            });
        }
    }

    // -- removeAll(Collection)
    @Test
    public void shouldThrowNPEWhenCallingRemoveAllNullWhenEmpty() {
        assertThatThrownBy(() -> empty().removeAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowNPEWhenCallingRemoveAllNullWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').removeAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRemoveAllWhenCollectionsAreDistinct() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.removeAll(Arrays.asList('2'))).isFalse();
        });
    }

    @Test
    public void shouldRemoveAllWhenCollectionsAreNotDistinct() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            assertThat(list.removeAll(Arrays.asList('2', '3'))).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('1'));
        });
    }

    @Test
    public void shouldRemoveAllWhenCollectionsAreEqual() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            assertThat(list.removeAll(Arrays.asList('1', '2'))).isTrue();
            assertThat(list).isEmpty();
        });
    }

    @Test
    public void shouldRemoveAllEmptyFromEmpty() {
        ifSupported(() -> {
            final List<Character> list = of();
            assertThat(list.removeAll(Arrays.asList())).isFalse();
            assertThat(list).isEmpty();
        });
    }

    @Test
    public void shouldRemoveAllEmptyFromNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.removeAll(Arrays.asList())).isFalse();
            assertThat(list).isEqualTo(Arrays.asList('1'));
        });
    }

    @Test
    public void shouldRemoveAllNonEmptyFromEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            assertThat(list.removeAll(Arrays.asList('1'))).isFalse();
            assertThat(list).isEmpty();
        });
    }

    // -- replaceAll(UnaryOperator)
    @Test
    public void shouldThrowWhenEmptyReplaceAllGivenNullUnaryOperator() {
        assertThatThrownBy(() -> this.<Character>empty().replaceAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowWhenNonEmptyReplaceAllGivenNullUnaryOperator() {
        assertThatThrownBy(() -> of('1').replaceAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldNotThrowWhenReplacingAllOfEmpty() {
        empty().replaceAll(UnaryOperator.identity());
    }

    @Test
    public void shouldReplaceAllOfNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            list.replaceAll(( c) -> ((char) (c + 1)));
            assertThat(list).isEqualTo(Arrays.asList('2', '3'));
        });
    }

    // -- retainAll(Collection)
    @Test
    public void shouldThrowNPEWhenCallingRetainAllNullWhenEmpty() {
        assertThatThrownBy(() -> empty().retainAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowNPEWhenCallingRetainAllNullWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').retainAll(null)).isInstanceOf(NullPointerException.class);
    }

    // -- retainAll(Collection) tests
    @Test
    public void shouldThrowWhenRetainAllNull() {
        assertThatThrownBy(() -> empty().retainAll(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> of('1').retainAll(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRetainAllEmptyOfEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            assertThat(list.retainAll(Arrays.asList())).isFalse();
            assertThat(list).isEmpty();
        });
    }

    @Test
    public void shouldRetainAllEmptyOfNonEmpty() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.retainAll(Arrays.asList())).isTrue();
            assertThat(list).isEqualTo(Arrays.asList());
        });
    }

    @Test
    public void shouldRetainAllNonEmptyOfEmpty() {
        ifSupported(() -> {
            final List<Character> list = empty();
            assertThat(list.retainAll(Arrays.asList('1'))).isFalse();
            assertThat(list).isEqualTo(Arrays.asList());
        });
    }

    @Test
    public void shouldRetainAllWhenDisjoint() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.retainAll(Arrays.asList('2'))).isTrue();
            assertThat(list).isEqualTo(Arrays.asList());
        });
    }

    @Test
    public void shouldRetainAllWhenIntersecting() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            assertThat(list.retainAll(Arrays.asList('2', '3'))).isTrue();
            assertThat(list).isEqualTo(Arrays.asList('2'));
        });
    }

    @Test
    public void shouldRetainAllWhenEqual() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2');
            assertThat(list.retainAll(Arrays.asList('1', '2'))).isFalse();
            assertThat(list).isEqualTo(Arrays.asList('1', '2'));
        });
    }

    // -- set(int, T)
    @Test
    public void shouldThrowWhenSettingElementAtNegativeIndexWhenEmpty() {
        ifSupported(() -> empty().set((-1), null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenSettingElementAtNegativeIndexWhenNotEmpty() {
        ifSupported(() -> of('1').set((-1), null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenSettingElementAtSizeIndexWhenEmpty() {
        ifSupported(() -> empty().set(0, null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldThrowWhenSettingElementAtSizeIndexWhenNotEmpty() {
        ifSupported(() -> of('1').set(1, null), IndexOutOfBoundsException.class);
    }

    @Test
    public void shouldSetElementAtFirstIndexWhenListWithSingleElement() {
        ifSupported(() -> {
            final List<Character> list = of('1');
            assertThat(list.set(0, 'a')).isEqualTo('1');
            assertThat(list).isEqualTo(Arrays.asList('a'));
        });
    }

    @Test
    public void shouldSetElementAtFirstIndexWhenListWithThreeElements() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.set(0, 'a')).isEqualTo('1');
            assertThat(list).isEqualTo(Arrays.asList('a', '2', '3'));
        });
    }

    @Test
    public void shouldSetElementAtLastIndexWhenListWithThreeElements() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.set(2, 'a')).isEqualTo('3');
            assertThat(list).isEqualTo(Arrays.asList('1', '2', 'a'));
        });
    }

    @Test
    public void shouldSetElementAtMiddleIndexWhenListWithThreeElements() {
        ifSupported(() -> {
            final List<Character> list = of('1', '2', '3');
            assertThat(list.set(1, 'a')).isEqualTo('2');
            assertThat(list).isEqualTo(Arrays.asList('1', 'a', '3'));
        });
    }

    // -- size()
    @Test
    public void shouldReturnSizeOfEmpty() {
        assertThat(empty().size()).isEqualTo(0);
    }

    @Test
    public void shouldReturnSizeOfNonEmpty() {
        assertThat(of('1', '2', '3').size()).isEqualTo(3);
    }

    // -- sort(Comparator)
    @Test
    public void shouldSortEmptyList() {
        final List<Character> list = empty();
        list.sort(Comparator.naturalOrder());
        assertThat(list).isEmpty();
    }

    @Test
    public void shouldSortNonEmptyList() {
        ifSupported(() -> {
            final List<Character> list = of('3', '1', '2');
            list.sort(Comparator.naturalOrder());
            assertThat(list).isEqualTo(Arrays.asList('1', '2', '3'));
        });
    }

    // -- spliterator()
    @Test
    public void shouldReturnNonNullSpliteratorWhenEmpty() {
        final Spliterator<Object> spliterator = empty().spliterator();
        assertThat(spliterator).isNotNull();
        assertThat(spliterator.tryAdvance(( e) -> {
            throw new AssertionError(("spliterator reports element for empty collection: " + e));
        })).isFalse();
    }

    @Test
    public void shouldReturnNonNullSpliteratorWhenNotEmpty() {
        final Spliterator<Character> spliterator = of('1').spliterator();
        assertThat(spliterator).isNotNull();
        assertThat(spliterator.tryAdvance(( e) -> assertThat(e).isEqualTo('1'))).isTrue();
        assertThat(spliterator.tryAdvance(( e) -> {
            throw new AssertionError(("spliterator reports element for empty collection: " + e));
        })).isFalse();
    }

    @Test
    public void shouldHaveSpliteratorOrderedCharacteristicsWhenEmpty() {
        final Spliterator<Object> spliterator = empty().spliterator();
        assertThat(((spliterator.characteristics()) & (Spliterator.ORDERED))).isEqualTo(Spliterator.ORDERED);
    }

    @Test
    public void shouldHaveSpliteratorOrderedCharacteristicsWhenNotEmpty() {
        final Spliterator<Character> spliterator = of('1').spliterator();
        assertThat(((spliterator.characteristics()) & (Spliterator.ORDERED))).isEqualTo(Spliterator.ORDERED);
    }

    // -- subList(int, int)
    @Test
    public void shouldReturnEmptyWhenSubListFrom0To0OnEmpty() {
        assertThat(empty().subList(0, 0)).isEmpty();
    }

    @Test
    public void shouldReturnEmptyWhenSubListFrom0To0OnNonEmpty() {
        assertThat(of('1').subList(0, 0)).isEmpty();
    }

    @Test
    public void shouldReturnListWithFirstElementWhenSubListFrom0To1OnNonEmpty() {
        assertThat(of('1').subList(0, 1)).isEqualTo(Arrays.asList('1'));
    }

    @Test
    public void shouldReturnEmptyWhenSubListFrom1To1OnNonEmpty() {
        assertThat(of('1').subList(1, 1)).isEmpty();
    }

    @Test
    public void shouldReturnSubListWhenIndicesAreWithinRange() {
        assertThat(of('1', '2', '3').subList(1, 3)).isEqualTo(Arrays.asList('2', '3'));
    }

    @Test
    public void shouldReturnEmptyWhenSubListIndicesBothAreUpperBound() {
        assertThat(of('1', '2', '3').subList(3, 3)).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnSubListOnNonEmptyWhenBeginIndexIsGreaterThanEndIndex() {
        of('1', '2', '3').subList(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnSubListOnEmptyWhenBeginIndexIsGreaterThanEndIndex() {
        empty().subList(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubListOnNonEmptyWhenBeginIndexExceedsLowerBound() {
        of('1', '2', '3').subList((-1), 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubListOnEmptyWhenBeginIndexExceedsLowerBound() {
        empty().subList((-1), 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubList2OnEmpty() {
        empty().subList(0, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubListWhenEndIndexExceedsUpperBound() {
        of('1', '2', '3').subList(1, 4).size();// force computation of last element, e.g. for lazy delegate

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnSubListWhenBeginIndexIsGreaterThanEndIndex() {
        of('1', '2', '3').subList(2, 1).size();// force computation of last element, e.g. for lazy delegate

    }

    @Test
    public void shouldReturnEqualInstanceIfSubListStartsAtZeroAndEndsAtLastElement() {
        assertThat(of('1', '2', '3').subList(0, 3)).isEqualTo(Arrays.asList('1', '2', '3'));
    }

    // -- toArray()
    @Test
    public void shouldConvertEmptyToArray() {
        assertThat(empty().toArray()).isEqualTo(new Object[0]);
    }

    @Test
    public void shouldConvertNonEmptyToArray() {
        assertThat(of('1').toArray()).isEqualTo(new Object[]{ '1' });
    }

    // -- toArray(T[])
    @Test
    public void shouldThrowNPEWhenCallingToArrayNullWhenEmpty() {
        assertThatThrownBy(() -> empty().toArray(((Object[]) (null)))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldThrowNPEWhenCallingToArrayNullWhenNotEmpty() {
        assertThatThrownBy(() -> of('1').toArray(((Object[]) (null)))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldConvertEmptyToArrayPassingArrayOfCorrectSize() {
        assertThat(this.<Character>empty().toArray(new Character[0])).isEqualTo(new Character[0]);
    }

    @Test
    public void shouldConvertEmptyToArrayPassingArrayOfGreaterSize() {
        assertThat(this.<Character>empty().toArray(new Character[]{ 'x' })).isEqualTo(new Character[]{ null });
    }

    @Test
    public void shouldConvertNonEmptyToArrayPassingArrayOfCorrectSize() {
        assertThat(of('1', '2').toArray(new Character[2])).isEqualTo(new Character[]{ '1', '2' });
    }

    @Test
    public void shouldConvertNonEmptyToArrayPassingArrayOfGreaterSize() {
        assertThat(of('1', '2').toArray(new Character[]{ 'a', 'b', 'c', 'd' })).isEqualTo(new Character[]{ '1', '2', null, 'd' });
    }

    @Test
    public void shouldConvertNonEmptyToArrayPassingArrayOfSmallerSize() {
        assertThat(of('1', '2').toArray(new Character[1])).isEqualTo(new Character[]{ '1', '2' });
    }

    static final class ListFactory {
        private final Function<Object[], List<Object>> listFactory;

        ListFactory(Function<Object[], List<Object>> listFactory) {
            this.listFactory = listFactory;
        }

        @SuppressWarnings("unchecked")
        <T> List<T> of(T... elements) {
            return ((List<T>) (listFactory.apply(elements)));
        }
    }

    enum ChangePolicy {

        IMMUTABLE,
        MUTABLE;}

    enum ElementType {

        FIXED,
        GENERIC;}

    enum ElementNullability {

        NULLABLE,
        NON_NULLABLE;}
}

