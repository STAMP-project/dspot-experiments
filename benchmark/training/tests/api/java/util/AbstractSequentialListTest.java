/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.util;


import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import junit.framework.TestCase;


public class AbstractSequentialListTest extends TestCase {
    class ASLT<E> extends AbstractSequentialList<E> {
        LinkedList<E> l = new LinkedList<E>();

        @Override
        public ListIterator<E> listIterator(int index) {
            return l.listIterator(index);
        }

        @Override
        public int size() {
            return l.size();
        }
    }

    /**
     * {@link java.util.AbstractSequentialList#addAll(int, java.util.Collection)}
     */
    public void test_addAll_ILCollection() {
        AbstractSequentialList<String> al = new AbstractSequentialListTest.ASLT<String>();
        String[] someList = new String[]{ "Aardvark", "Bear", "Chimpanzee", "Duck" };
        Collection<String> c = Arrays.asList(someList);
        al.addAll(c);
        TestCase.assertTrue("Should return true", al.addAll(2, c));
    }

    class Mock_unsupportedListIterator implements ListIterator {
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return true;
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object next() {
            return null;
        }

        public int nextIndex() {
            return 0;
        }

        public Object previous() {
            return null;
        }

        public int previousIndex() {
            return 0;
        }

        public void remove() {
        }

        public void set(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    class Mock_ListIterator<E> implements ListIterator<E> {
        final String wrongElement = "String";

        public void add(E o) {
            if (o.equals(wrongElement))
                throw new IllegalArgumentException();

            if (o == null)
                throw new NullPointerException();

        }

        public boolean hasNext() {
            return false;
        }

        public boolean hasPrevious() {
            return false;
        }

        public E next() {
            return null;
        }

        public int nextIndex() {
            return 0;
        }

        public E previous() {
            return null;
        }

        public int previousIndex() {
            return 0;
        }

        public void remove() {
        }

        public void set(E o) {
        }
    }

    public void test_addAllILjava_util_Collection() {
        AbstractSequentialList asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_unsupportedListIterator();
            }
        };
        Collection strV = new Vector<String>();
        strV.add("String");
        strV.add("1");
        strV.add("3.14");
        try {
            asl.addAll(0, strV);
            TestCase.fail("UnsupportedOperationException expected.");
        } catch (UnsupportedOperationException ee) {
            // expected
        }
        try {
            asl.addAll(0, null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException ee) {
            // expected
        }
        // ClassCastException can not be checked for this method.
        asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_ListIterator();
            }
        };
        try {
            asl.addAll(0, strV);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        strV.remove("String");
        strV.add(null);
        try {
            asl.addAll(0, strV);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        strV.remove(null);
        asl.addAll(0, strV);
        asl = new LinkedList();
        try {
            asl.addAll((-10), strV);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            asl.addAll(1, strV);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void test_addILjava_lang_Object() {
        AbstractSequentialList asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_unsupportedListIterator();
            }
        };
        try {
            asl.add(0, 1);
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_ListIterator();
            }
        };
        try {
            asl.add(0, "String");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ee) {
            // expected
        }
        try {
            asl.add(0, null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException ee) {
            // expected
        }
        // ClassCastException can not be checked for this method.
        asl.add(0, 1);
        asl = new LinkedList();
        try {
            asl.add((-1), 1);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException ee) {
            // expected
        }
        asl.add(0, 1);
        try {
            asl.add(2, 1);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException ee) {
            // expected
        }
    }

    public void test_getI() {
        final String[] buff = new String[]{ "0", "1", "2", "3", "4", "5" };
        AbstractSequentialList asl = new AbstractSequentialList() {
            int currPos = 0;

            @Override
            public int size() {
                return buff.length;
            }

            @Override
            public ListIterator listIterator(int index) {
                currPos = index;
                return new ListIterator() {
                    public void add(Object o) {
                    }

                    public boolean hasNext() {
                        return true;
                    }

                    public boolean hasPrevious() {
                        return false;
                    }

                    public Object next() {
                        return buff[currPos];
                    }

                    public int nextIndex() {
                        return 0;
                    }

                    public Object previous() {
                        return null;
                    }

                    public int previousIndex() {
                        return 0;
                    }

                    public void remove() {
                    }

                    public void set(Object o) {
                    }
                };
            }
        };
        for (int i = 0; i < (buff.length); i++) {
            TestCase.assertEquals(buff[i], asl.get(i));
        }
        try {
            asl.get(((asl.size()) + 1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            asl.get((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void test_iterrator() {
        AbstractSequentialList asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_unsupportedListIterator();
            }
        };
        TestCase.assertTrue(asl.iterator().getClass().toString().contains("Mock_unsupportedListIterator"));
        asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_ListIterator();
            }
        };
        TestCase.assertTrue(asl.iterator().getClass().toString().contains("Mock_ListIterator"));
        asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return null;
            }
        };
        TestCase.assertNull(asl.iterator());
    }

    public void test_removeI() {
        AbstractSequentialList asl = new AbstractSequentialList() {
            String[] buff = new String[]{ "0", "1", "2", "3", "4", "5" };

            int currPos = 0;

            @Override
            public int size() {
                return buff.length;
            }

            @Override
            public ListIterator listIterator(int index) {
                currPos = index;
                return new ListIterator() {
                    public void add(Object o) {
                    }

                    public boolean hasNext() {
                        return true;
                    }

                    public boolean hasPrevious() {
                        return false;
                    }

                    public Object next() {
                        return buff[currPos];
                    }

                    public int nextIndex() {
                        return 0;
                    }

                    public Object previous() {
                        return null;
                    }

                    public int previousIndex() {
                        return 0;
                    }

                    public void remove() {
                        buff[currPos] = "removed element";
                    }

                    public void set(Object o) {
                    }
                };
            }
        };
        try {
            asl.remove(((asl.size()) + 1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            asl.remove((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        for (int i = 0; i < (asl.size()); i++) {
            TestCase.assertFalse(asl.get(i).toString().contains("removed element"));
            asl.remove(i);
            TestCase.assertTrue(asl.get(i).toString().contains("removed element"));
        }
    }

    public void test_setILjava_lang_Object() {
        AbstractSequentialList asl = new AbstractSequentialList() {
            String[] buff = new String[]{ "0", "1", "2", "3", "4", "5" };

            final String illegalStr = "Illegal element";

            int currPos = 0;

            @Override
            public int size() {
                return buff.length;
            }

            @Override
            public ListIterator listIterator(int index) {
                currPos = index;
                return new ListIterator() {
                    public void add(Object o) {
                    }

                    public boolean hasNext() {
                        return true;
                    }

                    public boolean hasPrevious() {
                        return false;
                    }

                    public Object next() {
                        return buff[currPos];
                    }

                    public int nextIndex() {
                        return 0;
                    }

                    public Object previous() {
                        return null;
                    }

                    public int previousIndex() {
                        return 0;
                    }

                    public void remove() {
                        buff[currPos] = "removed element";
                    }

                    public void set(Object o) {
                        if (o == null)
                            throw new NullPointerException();

                        if (o.equals(illegalStr))
                            throw new IllegalArgumentException();

                        buff[currPos] = ((String) (o));
                    }
                };
            }
        };
        try {
            asl.set(((asl.size()) + 1), "new element");
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            asl.set((-1), "new element");
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        for (int i = 0; i < (asl.size()); i++) {
            TestCase.assertFalse(asl.get(i).toString().contains("new element"));
            asl.set(i, "new element");
            TestCase.assertTrue(asl.get(i).toString().contains("new element"));
        }
        try {
            asl.set(1, new Double(1));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // 
        }
        try {
            asl.set(1, "Illegal element");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ee) {
            // expected
        }
        try {
            asl.set(1, null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException ee) {
            // expected
        }
        asl = new AbstractSequentialList() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public ListIterator listIterator(int index) {
                return new AbstractSequentialListTest.Mock_unsupportedListIterator();
            }
        };
        try {
            asl.set(0, "New element");
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
}

