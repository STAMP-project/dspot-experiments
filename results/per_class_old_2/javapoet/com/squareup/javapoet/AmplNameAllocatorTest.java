

package com.squareup.javapoet;


public final class AmplNameAllocatorTest {
    @org.junit.Test
    public void usage() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo", 1)).isEqualTo("foo");
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("bar", 2)).isEqualTo("bar");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(1)).isEqualTo("foo");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(2)).isEqualTo("bar");
    }

    @org.junit.Test
    public void nameCollision() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo")).isEqualTo("foo");
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo")).isEqualTo("foo_");
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo")).isEqualTo("foo__");
    }

    @org.junit.Test
    public void nameCollisionWithTag() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo", 1)).isEqualTo("foo");
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo", 2)).isEqualTo("foo_");
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("foo", 3)).isEqualTo("foo__");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(1)).isEqualTo("foo");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(2)).isEqualTo("foo_");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(3)).isEqualTo("foo__");
    }

    @org.junit.Test
    public void characterMappingSubstitute() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("a-b", 1)).isEqualTo("a_b");
    }

    @org.junit.Test
    public void characterMappingSurrogate() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("a\ud83c\udf7ab", 1)).isEqualTo("a_b");
    }

    @org.junit.Test
    public void characterMappingInvalidStartButValidPart() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("1ab", 1)).isEqualTo("_1ab");
    }

    @org.junit.Test
    public void characterMappingInvalidStartIsInvalidPart() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("&ab", 1)).isEqualTo("_ab");
    }

    @org.junit.Test
    public void javaKeyword() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        com.google.common.truth.Truth.assertThat(nameAllocator.newName("public", 1)).isEqualTo("public_");
        com.google.common.truth.Truth.assertThat(nameAllocator.get(1)).isEqualTo("public_");
    }

    @org.junit.Test
    public void tagReuseForbidden() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        nameAllocator.newName("foo", 1);
        try {
            nameAllocator.newName("bar", 1);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("tag 1 cannot be used for both 'foo' and 'bar'");
        }
    }

    @org.junit.Test
    public void useBeforeAllocateForbidden() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator nameAllocator = new com.squareup.javapoet.NameAllocator();
        try {
            nameAllocator.get(1);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("unknown tag: 1");
        }
    }

    @org.junit.Test
    public void cloneUsage() throws java.lang.Exception {
        com.squareup.javapoet.NameAllocator outterAllocator = new com.squareup.javapoet.NameAllocator();
        outterAllocator.newName("foo", 1);
        com.squareup.javapoet.NameAllocator innerAllocator1 = outterAllocator.clone();
        com.google.common.truth.Truth.assertThat(innerAllocator1.newName("bar", 2)).isEqualTo("bar");
        com.google.common.truth.Truth.assertThat(innerAllocator1.newName("foo", 3)).isEqualTo("foo_");
        com.squareup.javapoet.NameAllocator innerAllocator2 = outterAllocator.clone();
        com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
        com.google.common.truth.Truth.assertThat(innerAllocator2.newName("bar", 3)).isEqualTo("bar");
    }

    @org.junit.Test
    public void cloneUsage_literalMutation3847_failAssert17_literalMutation4846() throws java.lang.Exception {
        try {
            com.squareup.javapoet.NameAllocator outterAllocator = new com.squareup.javapoet.NameAllocator();
            java.lang.String o_cloneUsage_literalMutation3847_failAssert17_literalMutation4846__5 = outterAllocator.newName("]2^", 1);
            org.junit.Assert.assertEquals(o_cloneUsage_literalMutation3847_failAssert17_literalMutation4846__5, "_2_");
            com.squareup.javapoet.NameAllocator innerAllocator1 = outterAllocator.clone();
            com.google.common.truth.Truth.assertThat(innerAllocator1.newName("bar", 3)).isEqualTo("bar");
            com.google.common.truth.Truth.assertThat(innerAllocator1.newName("foo", 3)).isEqualTo("foo_");
            com.squareup.javapoet.NameAllocator innerAllocator2 = outterAllocator.clone();
            com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
            com.google.common.truth.Truth.assertThat(innerAllocator2.newName("bar", 3)).isEqualTo("bar");
            org.junit.Assert.fail("cloneUsage_literalMutation3847 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void cloneUsage_literalMutation3837_cf4115_failAssert20() throws java.lang.Exception {
        try {
            com.squareup.javapoet.NameAllocator outterAllocator = new com.squareup.javapoet.NameAllocator();
            java.lang.String o_cloneUsage_literalMutation3837__3 = outterAllocator.newName("foo", 0);
            java.lang.Object vc_34 = ((java.lang.Object) (null));
            java.lang.String vc_33 = new java.lang.String();
            outterAllocator.newName(vc_33, vc_34);
            java.lang.Object o_11_0 = o_cloneUsage_literalMutation3837__3;
            com.squareup.javapoet.NameAllocator innerAllocator1 = outterAllocator.clone();
            com.google.common.truth.Truth.assertThat(innerAllocator1.newName("bar", 2)).isEqualTo("bar");
            com.google.common.truth.Truth.assertThat(innerAllocator1.newName("foo", 3)).isEqualTo("foo_");
            com.squareup.javapoet.NameAllocator innerAllocator2 = outterAllocator.clone();
            com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
            com.google.common.truth.Truth.assertThat(innerAllocator2.newName("bar", 3)).isEqualTo("bar");
            org.junit.Assert.fail("cloneUsage_literalMutation3837_cf4115 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void cloneUsage_literalMutation3835_failAssert10_add4698_cf8006_failAssert31() throws java.lang.Exception {
        try {
            try {
                com.squareup.javapoet.NameAllocator outterAllocator = new com.squareup.javapoet.NameAllocator();
                java.lang.String o_cloneUsage_literalMutation3835_failAssert10_add4698__5 = outterAllocator.newName("foo", 2);
                java.lang.Object vc_474 = ((java.lang.Object) (null));
                java.lang.String vc_473 = new java.lang.String();
                outterAllocator.newName(vc_473, vc_474);
                java.lang.Object o_13_0 = o_cloneUsage_literalMutation3835_failAssert10_add4698__5;
                com.squareup.javapoet.NameAllocator innerAllocator1 = outterAllocator.clone();
                com.google.common.truth.Truth.assertThat(innerAllocator1.newName("bar", 2)).isEqualTo("bar");
                com.google.common.truth.Truth.assertThat(innerAllocator1.newName("foo", 3)).isEqualTo("foo_");
                com.squareup.javapoet.NameAllocator innerAllocator2 = outterAllocator.clone();
                com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
                com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
                com.google.common.truth.Truth.assertThat(innerAllocator2.newName("bar", 3)).isEqualTo("bar");
                org.junit.Assert.fail("cloneUsage_literalMutation3835 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("cloneUsage_literalMutation3835_failAssert10_add4698_cf8006 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.NameAllocatorTest#cloneUsage */
    @org.junit.Test(timeout = 10000)
    public void cloneUsage_add3825_failAssert0_literalMutation4343_cf9401_failAssert38() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.javapoet.NameAllocator outterAllocator = new com.squareup.javapoet.NameAllocator();
                // AssertGenerator replace invocation
                java.lang.String o_cloneUsage_add3825_failAssert0_literalMutation4343__5 = // MethodCallAdder
outterAllocator.newName("foo", 1);
                // StatementAdderOnAssert create null value
                java.lang.Object vc_694 = (java.lang.Object)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_103 = "%ar";
                // StatementAdderMethod cloned existing statement
                outterAllocator.newName(String_vc_103, vc_694);
                // MethodAssertGenerator build local variable
                Object o_13_0 = o_cloneUsage_add3825_failAssert0_literalMutation4343__5;
                outterAllocator.newName("foo", 1);
                com.squareup.javapoet.NameAllocator innerAllocator1 = outterAllocator.clone();
                com.google.common.truth.Truth.assertThat(innerAllocator1.newName("bar", 2)).isEqualTo("bar");
                com.google.common.truth.Truth.assertThat(innerAllocator1.newName("foo", 3)).isEqualTo("foo_");
                com.squareup.javapoet.NameAllocator innerAllocator2 = outterAllocator.clone();
                com.google.common.truth.Truth.assertThat(innerAllocator2.newName("foo", 2)).isEqualTo("foo_");
                com.google.common.truth.Truth.assertThat(innerAllocator2.newName("bar", 3)).isEqualTo("%ar");
                org.junit.Assert.fail("cloneUsage_add3825 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("cloneUsage_add3825_failAssert0_literalMutation4343_cf9401 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

