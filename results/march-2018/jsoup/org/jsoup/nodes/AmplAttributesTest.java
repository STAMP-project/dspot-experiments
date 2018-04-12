package org.jsoup.nodes;


import java.util.Iterator;
import java.util.regex.PatternSyntaxException;
import org.junit.Test;


public class AmplAttributesTest {
    @Test(timeout = 50000)
    public void htmllitString212_failAssert2() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("");
            a.dataset().size();
            a.dataset().get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString212 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void htmllitString48_failAssert1() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("hEllo");
            a.dataset().size();
            a.dataset().get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString48 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void removeCaseSensitivelitString153872_failAssert2() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivelitString153872 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorlitString212603_failAssert2() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_106 = datas[i][0];
                attribute.getKey();
                String String_107 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIteratorlitString212603 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIterator_replacement212553_failAssert0() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            int i = 1120551479;
            for (Attribute attribute : a) {
                String String_6 = datas[i][0];
                attribute.getKey();
                String String_7 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIterator_replacement212553 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIterator_replacement212553_failAssert0_sd213200_failAssert0() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
                for (String[] atts : datas) {
                    a.put(atts[0], atts[1]);
                }
                Iterator<Attribute> iterator = a.iterator();
                a.iterator().hasNext();
                int i = 1120551479;
                for (Attribute attribute : a) {
                    StringBuffer __DSPOT_arg0_114924 = new StringBuffer(-1990387728);
                    String String_6 = datas[i][0];
                    String __DSPOT_invoc_18 = attribute.getKey();
                    String String_7 = datas[i][1];
                    attribute.getValue();
                    i++;
                    attribute.getKey().contentEquals(new StringBuffer(-1990387728));
                }
                org.junit.Assert.fail("testIterator_replacement212553 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200 should have thrown NegativeArraySizeException");
        } catch (NegativeArraySizeException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIterator_replacement212553_failAssert0_sd213200_failAssert0litString216131_failAssert3() throws Exception {
        try {
            try {
                try {
                    Attributes a = new Attributes();
                    String[][] datas = new String[][]{ new String[]{ "", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
                    for (String[] atts : datas) {
                        a.put(atts[0], atts[1]);
                    }
                    Iterator<Attribute> iterator = a.iterator();
                    a.iterator().hasNext();
                    int i = 1120551479;
                    for (Attribute attribute : a) {
                        StringBuffer __DSPOT_arg0_114924 = new StringBuffer(-1990387728);
                        String String_6 = datas[i][0];
                        String __DSPOT_invoc_18 = attribute.getKey();
                        String String_7 = datas[i][1];
                        attribute.getValue();
                        i++;
                        attribute.getKey().contentEquals(new StringBuffer(-1990387728));
                    }
                    org.junit.Assert.fail("testIterator_replacement212553 should have thrown ArrayIndexOutOfBoundsException");
                } catch (ArrayIndexOutOfBoundsException eee) {
                }
                org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200 should have thrown NegativeArraySizeException");
            } catch (NegativeArraySizeException eee) {
            }
            org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200_failAssert0litString216131 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIterator_replacement212553_failAssert0_sd213200_failAssert0litNum216241_failAssert2() throws Exception {
        try {
            try {
                try {
                    Attributes a = new Attributes();
                    String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
                    for (String[] atts : datas) {
                        a.put(atts[0], atts[1]);
                    }
                    Iterator<Attribute> iterator = a.iterator();
                    a.iterator().hasNext();
                    int i = 1120551479;
                    for (Attribute attribute : a) {
                        StringBuffer __DSPOT_arg0_114924 = new StringBuffer(2147483647);
                        String String_6 = datas[i][0];
                        String __DSPOT_invoc_18 = attribute.getKey();
                        String String_7 = datas[i][1];
                        attribute.getValue();
                        i++;
                        attribute.getKey().contentEquals(new StringBuffer(2147483647));
                    }
                    org.junit.Assert.fail("testIterator_replacement212553 should have thrown ArrayIndexOutOfBoundsException");
                } catch (ArrayIndexOutOfBoundsException eee) {
                }
                org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200 should have thrown NegativeArraySizeException");
            } catch (NegativeArraySizeException eee) {
            }
            org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200_failAssert0litNum216241 should have thrown OutOfMemoryError");
        } catch (OutOfMemoryError eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIterator_replacement212553_failAssert0_sd213200_failAssert0litString216165_failAssert1() throws Exception {
        try {
            try {
                try {
                    Attributes a = new Attributes();
                    String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
                    for (String[] atts : datas) {
                        a.put(atts[0], atts[1]);
                    }
                    Iterator<Attribute> iterator = a.iterator();
                    a.iterator().hasNext();
                    int i = 1120551479;
                    for (Attribute attribute : a) {
                        StringBuffer __DSPOT_arg0_114924 = new StringBuffer(-1990387728);
                        String String_6 = datas[i][0];
                        String __DSPOT_invoc_18 = attribute.getKey();
                        String String_7 = datas[i][1];
                        attribute.getValue();
                        i++;
                        attribute.getKey().contentEquals(new StringBuffer(-1990387728));
                    }
                    org.junit.Assert.fail("testIterator_replacement212553 should have thrown ArrayIndexOutOfBoundsException");
                } catch (ArrayIndexOutOfBoundsException eee) {
                }
                org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200 should have thrown NegativeArraySizeException");
            } catch (NegativeArraySizeException eee) {
            }
            org.junit.Assert.fail("testIterator_replacement212553_failAssert0_sd213200_failAssert0litString216165 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216741_sd217057_failAssert5() throws Exception {
        try {
            String __DSPOT_arg1_116636 = "K<W`ee>6Yd/N_;Gv4]1z";
            String __DSPOT_arg0_116635 = "2#Z_fJ$t(u}?/LU8oZLk";
            String __DSPOT_key_116442 = "<=o`q|n!IGSZt6JXq$u?";
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            String __DSPOT_invoc_8 = a.get("<=o`q|n!IGSZt6JXq$u?");
            a.get("<=o`q|n!IGSZt6JXq$u?").replaceFirst("2#Z_fJ$t(u}?/LU8oZLk", "K<W`ee>6Yd/N_;Gv4]1z");
            org.junit.Assert.fail("testIteratorEmpty_sd216741_sd217057 should have thrown PatternSyntaxException");
        } catch (PatternSyntaxException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216746_sd217295_failAssert9() throws Exception {
        try {
            int __DSPOT_arg3_116801 = -440314064;
            char[] __DSPOT_arg2_116800 = new char[]{ 't', '*', '9', '8' };
            int __DSPOT_arg1_116799 = 940419451;
            int __DSPOT_arg0_116798 = -56215362;
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            String __DSPOT_invoc_7 = a.html();
            a.html().getChars(-56215362, 940419451, new char[]{ 't', '*', '9', '8' }, -440314064);
            org.junit.Assert.fail("testIteratorEmpty_sd216746_sd217295 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216746_sd217293_failAssert10() throws Exception {
        try {
            int __DSPOT_arg3_116796 = -560615692;
            byte[] __DSPOT_arg2_116795 = new byte[0];
            int __DSPOT_arg1_116794 = 2113480705;
            int __DSPOT_arg0_116793 = 1993226560;
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            String __DSPOT_invoc_7 = a.html();
            a.html().getBytes(1993226560, 2113480705, new byte[0], -560615692);
            org.junit.Assert.fail("testIteratorEmpty_sd216746_sd217293 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216754_sd217648_failAssert7() throws Exception {
        try {
            int __DSPOT_arg3_116968 = -1871341739;
            byte[] __DSPOT_arg2_116967 = new byte[]{ 32, 73, -10, 103 };
            int __DSPOT_arg1_116966 = -1219790826;
            int __DSPOT_arg0_116965 = 1327423650;
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            String __DSPOT_invoc_7 = a.toString();
            a.toString().getBytes(1327423650, -1219790826, new byte[]{ 32, 73, -10, 103 }, -1871341739);
            org.junit.Assert.fail("testIteratorEmpty_sd216754_sd217648 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216742_sd217115_failAssert13() throws Exception {
        try {
            int __DSPOT_arg1_116667 = -1625903907;
            int __DSPOT_arg0_116666 = 1053303181;
            String __DSPOT_key_116443 = "Z2KG&K]C+T@Wo#(!U+k^";
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            a.iterator().hasNext();
            String __DSPOT_invoc_8 = a.getIgnoreCase("Z2KG&K]C+T@Wo#(!U+k^");
            a.getIgnoreCase("Z2KG&K]C+T@Wo#(!U+k^").codePointCount(1053303181, -1625903907);
            org.junit.Assert.fail("testIteratorEmpty_sd216742_sd217115 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216741_sd217062_failAssert18litString219810_failAssert4() throws Exception {
        try {
            try {
                int __DSPOT_arg1_116644 = -1312208733;
                int __DSPOT_arg0_116643 = 261228582;
                String __DSPOT_key_116442 = "";
                Attributes a = new Attributes();
                Iterator<Attribute> iterator = a.iterator();
                a.iterator().hasNext();
                String __DSPOT_invoc_8 = a.get("");
                a.get("").subSequence(261228582, -1312208733);
                org.junit.Assert.fail("testIteratorEmpty_sd216741_sd217062 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_sd216741_sd217062_failAssert18litString219810 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorEmpty_sd216742_sd217115_failAssert13litString219496_failAssert5() throws Exception {
        try {
            try {
                int __DSPOT_arg1_116667 = -1625903907;
                int __DSPOT_arg0_116666 = 1053303181;
                String __DSPOT_key_116443 = "";
                Attributes a = new Attributes();
                Iterator<Attribute> iterator = a.iterator();
                a.iterator().hasNext();
                String __DSPOT_invoc_8 = a.getIgnoreCase("");
                a.getIgnoreCase("").codePointCount(1053303181, -1625903907);
                org.junit.Assert.fail("testIteratorEmpty_sd216742_sd217115 should have thrown IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_sd216742_sd217115_failAssert13litString219496 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorRemovable_add220499_failAssert3() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            Iterator<Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            iterator.remove();
            a.size();
            org.junit.Assert.fail("testIteratorRemovable_add220499 should have thrown IllegalStateException");
        } catch (IllegalStateException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testIteratorRemovablelitString220405_failAssert1() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            Iterator<Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            a.size();
            org.junit.Assert.fail("testIteratorRemovablelitString220405 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }
}

