package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class NameAllocatorTest {
    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litString21530() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("foo", Integer.MIN_VALUE);
            Assert.assertEquals("foo_", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litString21544() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("\n", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litString21572() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("b3ar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210litString21503() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
        try {
            String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("", 0);
            Assert.assertEquals("", o_tagReuseForbiddenlitNum21210__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217litString21580() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("f<oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("f_oo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219litString21606() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("z&7", 1);
        Assert.assertEquals("z_7", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("z_7", o_tagReuseForbidden_mg21219__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188litString21351() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            nameAllocator.newName("", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189litString21386() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            nameAllocator.newName("foo", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213litString21466() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
        try {
            String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("foo", 1191313852);
            Assert.assertEquals("foo_", o_tagReuseForbiddenlitNum21213__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220litString21619() throws Exception {
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5e{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5e_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191litString21356() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", 1);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
        try {
            nameAllocator.newName("b4ar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192litString21372() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            nameAllocator.newName("b?ar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190litString21338() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
        try {
            nameAllocator.newName("foo__", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688litString21673() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688litString21673__5 = nameAllocator.newName("_1ab", 1);
            Assert.assertEquals("_1ab", o_tagReuseForbiddennull21224_failAssert688litString21673__5);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195litString21368() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
        try {
            nameAllocator.newName("\n", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litNum21898() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 2);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litNum21904() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 0);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litNum21908() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 2);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            String o_tagReuseForbidden_add21215litNum21908__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbidden_add21215litNum21908__8);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217litNum21929() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            String o_tagReuseForbidden_mg21217litNum21929__8 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21217litNum21929__8);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219litNum21938() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            String o_tagReuseForbidden_mg21219litNum21938__9 = nameAllocator.newName("bar", 2);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21219litNum21938__9);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188litNum21756() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", Integer.MIN_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            String o_tagReuseForbiddenlitString21188litNum21756__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21188litNum21756__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200litNum21829() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
        try {
            String o_tagReuseForbiddenlitString21200litNum21829__8 = nameAllocator.newName("9v2", 0);
            Assert.assertEquals("_9v2", o_tagReuseForbiddenlitString21200litNum21829__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189litNum21858() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            String o_tagReuseForbiddenlitString21189litNum21858__8 = nameAllocator.newName("bar", 2);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21189litNum21858__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203litNum21737() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1019162204);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
        try {
            String o_tagReuseForbiddenlitString21203litNum21737__8 = nameAllocator.newName(":", 1);
            Assert.assertEquals("_", o_tagReuseForbiddenlitString21203litNum21737__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201litNum21746() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", Integer.MIN_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
        try {
            String o_tagReuseForbiddenlitString21201litNum21746__8 = nameAllocator.newName("", 1);
            Assert.assertEquals("", o_tagReuseForbiddenlitString21201litNum21746__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202litNum21822() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
        try {
            String o_tagReuseForbiddenlitString21202litNum21822__8 = nameAllocator.newName("\n", -735580791);
            Assert.assertEquals("_", o_tagReuseForbiddenlitString21202litNum21822__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220litNum21945() throws Exception {
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", Integer.MAX_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        try {
            String o_tagReuseForbidden_mg21220litNum21945__11 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21220litNum21945__11);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690litNum21989() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690litNum21989__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690litNum21989__5);
            try {
                nameAllocator.newName(null, 0);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191litNum21766() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", Integer.MIN_VALUE);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
        try {
            String o_tagReuseForbiddenlitString21191litNum21766__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21191litNum21766__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192litNum21786() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", Integer.MIN_VALUE);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            String o_tagReuseForbiddenlitString21192litNum21786__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21192litNum21786__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190litNum21732() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
        try {
            String o_tagReuseForbiddenlitString21190litNum21732__8 = nameAllocator.newName("bar", 1916459613);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21190litNum21732__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688litNum21975() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688litNum21975__5 = nameAllocator.newName("foo", Integer.MAX_VALUE);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21224_failAssert688litNum21975__5);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195litNum21780() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
        try {
            String o_tagReuseForbiddenlitString21195litNum21780__8 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21195litNum21780__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21196litNum21810() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21196__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21196__3);
        try {
            String o_tagReuseForbiddenlitString21196litNum21810__8 = nameAllocator.newName("foo", Integer.MAX_VALUE);
            Assert.assertEquals("foo_", o_tagReuseForbiddenlitString21196litNum21810__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21196__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212_add22052() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212_add22052__8 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212_add22052__8);
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211_add22054() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211_add22054__8 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211_add22054__8);
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215_add22057() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_add22060() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201_add22010() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
        try {
            nameAllocator.newName("", 1);
            nameAllocator.newName("", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213_add22044() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
        try {
            String o_tagReuseForbiddenlitNum21213_add22044__8 = nameAllocator.newName("bar", 1191313852);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21213_add22044__8);
            String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("bar", 1191313852);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192_add22018() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688_add22074() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688_add22074__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21224_failAssert688_add22074__5);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            expected_1.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195_add22016() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21196_add22022() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21196__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21196__3);
        try {
            nameAllocator.newName("foo", 1);
            nameAllocator.newName("foo", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21196__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211_mg22188() throws Exception {
        Object __DSPOT_tag_5375 = new Object();
        String __DSPOT_suggestion_5374 = "^E,nks&(5G%ejJbAc@7u";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21211_mg22188__15 = nameAllocator.newName(__DSPOT_suggestion_5374, __DSPOT_tag_5375);
        Assert.assertEquals("_E_nks__5G_ejJbAc_7u", o_tagReuseForbiddenlitNum21211_mg22188__15);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215_mg22189() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210_mg22173() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
        try {
            String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21210__6);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_mg22193() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219_mg22197() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188_mg22103() throws Exception {
        String __DSPOT_suggestion_5289 = "qGOlSh-_oN9GT3y 7:e2";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21188_mg22103__11 = nameAllocator.newName(__DSPOT_suggestion_5289);
        Assert.assertEquals("qGOlSh__oN9GT3y_7_e2", o_tagReuseForbiddenlitString21188_mg22103__11);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189_mg22141() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203_mg22093() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
        try {
            nameAllocator.newName(":", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201_mg22099() throws Exception {
        String __DSPOT_suggestion_5285 = "E9DX|d6Z2g$t{]qABE)U";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
        try {
            nameAllocator.newName("", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21201_mg22099__11 = nameAllocator.newName(__DSPOT_suggestion_5285);
        Assert.assertEquals("E9DX_d6Z2g$t__qABE_U", o_tagReuseForbiddenlitString21201_mg22099__11);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213_mg22167() throws Exception {
        String __DSPOT_suggestion_5353 = "^eZ:f&XAe@?+)F](ZDPA";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
        try {
            String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("bar", 1191313852);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21213__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21213_mg22167__13 = nameAllocator.newName(__DSPOT_suggestion_5353);
        Assert.assertEquals("_eZ_f_XAe____F__ZDPA", o_tagReuseForbiddenlitNum21213_mg22167__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220_mg22204() throws Exception {
        Object __DSPOT_tag_5391 = new Object();
        String __DSPOT_suggestion_5390 = "1po6E>ITZTCA_UtSGuw*";
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        String o_tagReuseForbidden_mg21220_mg22204__19 = nameAllocator.newName(__DSPOT_suggestion_5390, __DSPOT_tag_5391);
        Assert.assertEquals("_1po6E_ITZTCA_UtSGuw_", o_tagReuseForbidden_mg21220_mg22204__19);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690_mg22222() throws Exception {
        try {
            Object __DSPOT_tag_5408 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690_mg22222__7 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690_mg22222__7);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
            nameAllocator.get(__DSPOT_tag_5408);
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191_mg22108() throws Exception {
        Object __DSPOT_tag_5295 = new Object();
        String __DSPOT_suggestion_5294 = "FS)Q+*mC5 h/CJ&?9<s&";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", 1);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21191_mg22108__13 = nameAllocator.newName(__DSPOT_suggestion_5294, __DSPOT_tag_5295);
        Assert.assertEquals("FS_Q__mC5_h_CJ__9_s_", o_tagReuseForbiddenlitString21191_mg22108__13);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192_mg22116() throws Exception {
        Object __DSPOT_tag_5303 = new Object();
        String __DSPOT_suggestion_5302 = "_:ocSN*D@3M7I{TFq(lf";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21192_mg22116__13 = nameAllocator.newName(__DSPOT_suggestion_5302, __DSPOT_tag_5303);
        Assert.assertEquals("__ocSN_D_3M7I_TFq_lf", o_tagReuseForbiddenlitString21192_mg22116__13);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190_mg22092() throws Exception {
        Object __DSPOT_tag_5279 = new Object();
        String __DSPOT_suggestion_5278 = "YIl}Z]j>-,V1*_?et(Kt";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21190_mg22092__13 = nameAllocator.newName(__DSPOT_suggestion_5278, __DSPOT_tag_5279);
        Assert.assertEquals("YIl_Z_j___V1___et_Kt", o_tagReuseForbiddenlitString21190_mg22092__13);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688_mg22215() throws Exception {
        try {
            String __DSPOT_suggestion_5401 = "}&eG`799nr=ie,7l{{td";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688_mg22215__6 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21224_failAssert688_mg22215__6);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
            nameAllocator.newName(__DSPOT_suggestion_5401);
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195_mg22109() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_rv22230() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        NameAllocator __DSPOT_invoc_12 = nameAllocator.clone();
        __DSPOT_invoc_12.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215null22289() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName(null, 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690null22324() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690null22324__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690null22324__5);
            try {
                nameAllocator.newName(null, null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_remove22084() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litString21529() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litString21540() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("f!oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litString21558() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("ptg", 1);
        Assert.assertEquals("ptg", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("ptg", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210litString21500() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
        try {
            String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("bBar", 0);
            Assert.assertEquals("bBar", o_tagReuseForbiddenlitNum21210__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217litString21581() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("fo", 1);
        Assert.assertEquals("fo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("fo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219litString21598() throws Exception {
        String __DSPOT_suggestion_5269 = "v!X1]-!$hg6cnpBh/xET";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("v_X1___$hg6cnpBh_xET", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188litString21347() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            nameAllocator.newName("xar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189litString21393() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            nameAllocator.newName(":", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213litString21462() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("Enl", 1);
        Assert.assertEquals("Enl", o_tagReuseForbiddenlitNum21213__3);
        try {
            String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("bar", 1191313852);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21213__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("Enl", o_tagReuseForbiddenlitNum21213__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220litString21626() throws Exception {
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("public_", 1);
        Assert.assertEquals("public_", o_tagReuseForbidden_mg21220__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        Assert.assertEquals("public_", o_tagReuseForbidden_mg21220__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191litString21357() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", 1);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
        try {
            nameAllocator.newName("br", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192litString21374() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            nameAllocator.newName("5gx", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190litString21345() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
        try {
            nameAllocator.newName(":", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("f_oo", o_tagReuseForbiddenlitString21190__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688litString21682() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688litString21682__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21224_failAssert688litString21682__5);
            try {
                nameAllocator.newName("oar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195litString21366() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
        try {
            nameAllocator.newName("kdt", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_", o_tagReuseForbiddenlitString21195__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litNum21901() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", Integer.MIN_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litNum21907() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1281042827);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litNum21916() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            String o_tagReuseForbidden_add21215litNum21916__8 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbidden_add21215litNum21916__8);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217litNum21927() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", -968559352);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            String o_tagReuseForbidden_mg21217litNum21927__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21217litNum21927__8);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219litNum21935() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", Integer.MAX_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            String o_tagReuseForbidden_mg21219litNum21935__9 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21219litNum21935__9);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188litNum21754() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 0);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            String o_tagReuseForbiddenlitString21188litNum21754__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21188litNum21754__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200litNum21827() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 2117243712);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
        try {
            String o_tagReuseForbiddenlitString21200litNum21827__8 = nameAllocator.newName("9v2", 1);
            Assert.assertEquals("_9v2", o_tagReuseForbiddenlitString21200litNum21827__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189litNum21853() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 2);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            String o_tagReuseForbiddenlitString21189litNum21853__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21189litNum21853__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203litNum21741() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
        try {
            String o_tagReuseForbiddenlitString21203litNum21741__8 = nameAllocator.newName(":", Integer.MIN_VALUE);
            Assert.assertEquals("_", o_tagReuseForbiddenlitString21203litNum21741__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201litNum21749() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
        try {
            String o_tagReuseForbiddenlitString21201litNum21749__8 = nameAllocator.newName("", 0);
            Assert.assertEquals("", o_tagReuseForbiddenlitString21201litNum21749__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202litNum21821() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
        try {
            String o_tagReuseForbiddenlitString21202litNum21821__8 = nameAllocator.newName("\n", Integer.MIN_VALUE);
            Assert.assertEquals("_", o_tagReuseForbiddenlitString21202litNum21821__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220litNum21950() throws Exception {
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        try {
            String o_tagReuseForbidden_mg21220litNum21950__11 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg21220litNum21950__11);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690litNum21987() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690litNum21987__5 = nameAllocator.newName("foo", 379924789);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690litNum21987__5);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191litNum21765() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", Integer.MAX_VALUE);
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
        try {
            String o_tagReuseForbiddenlitString21191litNum21765__8 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21191litNum21765__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("fo", o_tagReuseForbiddenlitString21191__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192litNum21790() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
        try {
            String o_tagReuseForbiddenlitString21192litNum21790__8 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitString21192litNum21790__8);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__c", o_tagReuseForbiddenlitString21192__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210_add22048() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
        try {
            String o_tagReuseForbiddenlitNum21210_add22048__8 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21210_add22048__8);
            String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("bar", 0);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219_add22063() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219_add22063__11 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219_add22063__11);
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU_", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219_add22063__11);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188_add22012() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200_add22026() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
        try {
            nameAllocator.newName("9v2", 1);
            nameAllocator.newName("9v2", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189_add22032() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_2oo", o_tagReuseForbiddenlitString21189__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203_add22008() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
        try {
            nameAllocator.newName(":", 1);
            nameAllocator.newName(":", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21203__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202_add22024() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
        try {
            nameAllocator.newName("\n", 1);
            nameAllocator.newName("\n", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690_add22080() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690_add22080__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690_add22080__5);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            expected_1.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212_mg22184() throws Exception {
        Object __DSPOT_tag_5371 = new Object();
        String __DSPOT_suggestion_5370 = "[(MS/Tgmf!(GRJ%c9}bW";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21212_mg22184__15 = nameAllocator.newName(__DSPOT_suggestion_5370, __DSPOT_tag_5371);
        Assert.assertEquals("__MS_Tgmf__GRJ_c9_bW", o_tagReuseForbiddenlitNum21212_mg22184__15);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211_mg22185() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210_mg22175() throws Exception {
        String __DSPOT_suggestion_5361 = "UN!:|(e7c!!]:!&JtszJ";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
        try {
            String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21210__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21210_mg22175__13 = nameAllocator.newName(__DSPOT_suggestion_5361);
        Assert.assertEquals("UN____e7c______JtszJ", o_tagReuseForbiddenlitNum21210_mg22175__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21210__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_mg22196() throws Exception {
        Object __DSPOT_tag_5383 = new Object();
        String __DSPOT_suggestion_5382 = "k1eS:1!?jy{cH)CI)C/L";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        String o_tagReuseForbidden_mg21217_mg22196__14 = nameAllocator.newName(__DSPOT_suggestion_5382, __DSPOT_tag_5383);
        Assert.assertEquals("k1eS_1__jy_cH_CI_C_L", o_tagReuseForbidden_mg21217_mg22196__14);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219_mg22199() throws Exception {
        String __DSPOT_suggestion_5385 = "S_d+Ga4hw(]Dc2,I?-bN";
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        String o_tagReuseForbidden_mg21219_mg22199__15 = nameAllocator.newName(__DSPOT_suggestion_5385);
        Assert.assertEquals("S_d_Ga4hw__Dc2_I__bN", o_tagReuseForbidden_mg21219_mg22199__15);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188_mg22101() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21188__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200_mg22129() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
        try {
            nameAllocator.newName("9v2", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21200__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201_mg22100() throws Exception {
        Object __DSPOT_tag_5287 = new Object();
        String __DSPOT_suggestion_5286 = "<2f,[vwhZp&hSdjHg^^O";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
        try {
            nameAllocator.newName("", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21201_mg22100__13 = nameAllocator.newName(__DSPOT_suggestion_5286, __DSPOT_tag_5287);
        Assert.assertEquals("_2f__vwhZp_hSdjHg__O", o_tagReuseForbiddenlitString21201_mg22100__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21201__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202_mg22127() throws Exception {
        String __DSPOT_suggestion_5313 = "6_4PJ|eG-M1QWj;37`nn";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
        try {
            nameAllocator.newName("\n", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString21202_mg22127__11 = nameAllocator.newName(__DSPOT_suggestion_5313);
        Assert.assertEquals("_6_4PJ_eG_M1QWj_37_nn", o_tagReuseForbiddenlitString21202_mg22127__11);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString21202__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213_mg22165() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
        try {
            String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("bar", 1191313852);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21213__6);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21213__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220_mg22201() throws Exception {
        Object __DSPOT_tag_5271 = new Object();
        String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21220__6);
        Assert.assertEquals("ux2__5k_n_V__u__6__q", o_tagReuseForbidden_mg21220__11);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690_mg22223() throws Exception {
        try {
            String __DSPOT_suggestion_5409 = "}icb>QZI0O)ejgPArS<c";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21223_failAssert690_mg22223__6 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21223_failAssert690_mg22223__6);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
            nameAllocator.newName(__DSPOT_suggestion_5409);
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_rv22233() throws Exception {
        Object __DSPOT_tag_5419 = new Object();
        String __DSPOT_suggestion_5418 = "XD@3;ZC u)_0.ne. (h0";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        NameAllocator __DSPOT_invoc_12 = nameAllocator.clone();
        String o_tagReuseForbidden_mg21217_rv22233__16 = __DSPOT_invoc_12.newName(__DSPOT_suggestion_5418, __DSPOT_tag_5419);
        Assert.assertEquals("XD_3_ZC_u__0_ne___h0", o_tagReuseForbidden_mg21217_rv22233__16);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_rv22232() throws Exception {
        String __DSPOT_suggestion_5417 = "x`yZY5L[m0U#$kwnPWWr";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        NameAllocator __DSPOT_invoc_12 = nameAllocator.clone();
        String o_tagReuseForbidden_mg21217_rv22232__14 = __DSPOT_invoc_12.newName(__DSPOT_suggestion_5417);
        Assert.assertEquals("x_yZY5L_m0U_$kwnPWWr", o_tagReuseForbidden_mg21217_rv22232__14);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21224_failAssert688null22318() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddennull21224_failAssert688null22318__5 = nameAllocator.newName("foo", 1);
            Assert.assertEquals("foo", o_tagReuseForbiddennull21224_failAssert688null22318__5);
            try {
                nameAllocator.newName(null, null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21224 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litString21527() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("", 1);
        Assert.assertEquals("", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litString21548() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("b*ar", Integer.MAX_VALUE);
            Assert.assertEquals("b_ar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litString21555() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("Xoo", 1);
        Assert.assertEquals("Xoo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("Xoo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212litNum21899() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 0);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211litNum21906() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", Integer.MIN_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215litNum21914() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            String o_tagReuseForbidden_add21215litNum21914__8 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbidden_add21215litNum21914__8);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215_add22056() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_add22059() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        nameAllocator.clone();
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21217__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219_add22062() throws Exception {
        String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
        Assert.assertEquals("VV__mE6Di__Obu__nVnU", o_tagReuseForbidden_mg21219__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg21219__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212_mg22183() throws Exception {
        String __DSPOT_suggestion_5369 = "W[R8jeG!}m9G&2nmDwiq";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
        try {
            String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21212__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21212_mg22183__13 = nameAllocator.newName(__DSPOT_suggestion_5369);
        Assert.assertEquals("W_R8jeG__m9G_2nmDwiq", o_tagReuseForbiddenlitNum21212_mg22183__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21212__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211_mg22187() throws Exception {
        String __DSPOT_suggestion_5373 = ";ce8X2PV}wPt4DA=r#]r";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
        try {
            String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum21211__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum21211_mg22187__13 = nameAllocator.newName(__DSPOT_suggestion_5373);
        Assert.assertEquals("_ce8X2PV_wPt4DA_r__r", o_tagReuseForbiddenlitNum21211_mg22187__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum21211__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215_mg22191() throws Exception {
        String __DSPOT_suggestion_5377 = "IBg9;uejU=IPs$?:?Cj<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_add21215_mg22191__12 = nameAllocator.newName(__DSPOT_suggestion_5377);
        Assert.assertEquals("IBg9_uejU_IPs$___Cj_", o_tagReuseForbidden_add21215_mg22191__12);
        Assert.assertEquals("foo", o_tagReuseForbidden_add21215__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21210_add22047_failAssert692() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitNum21210__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21210__6 = nameAllocator.newName("bar", 0);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21210_add22047 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219_add22061_failAssert693() throws Exception {
        try {
            String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
            org.junit.Assert.fail("tagReuseForbidden_mg21219_add22061 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188_add22011_failAssert694() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21188_add22011 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200_add22025_failAssert695() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("9v2", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21200_add22025 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189_add22031_failAssert696() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("2oo", 1);
            String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21189_add22031 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'_2oo\' and \'_2oo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203_add22007_failAssert697() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(":", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21203_add22007 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202_add22023_failAssert698() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("\n", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21202_add22023 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull21223_failAssert690_add22078_failAssert700() throws Exception {
        try {
            try {
                NameAllocator nameAllocator = new NameAllocator();
                nameAllocator.newName("foo", 1);
                nameAllocator.newName("foo", 1);
                try {
                    nameAllocator.newName(null, 1);
                } catch (IllegalArgumentException expected) {
                }
                org.junit.Assert.fail("tagReuseForbiddennull21223 should have thrown NullPointerException");
            } catch (NullPointerException expected_1) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull21223_failAssert690_add22078 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_2) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_2.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191_add22013_failAssert701() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("fo", 1);
            String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21191_add22013 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'fo\' and \'fo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190_add22005_failAssert702() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("f{oo", 1);
            String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21190_add22005 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'f_oo\' and \'f_oo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212null22279_failAssert707() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName(null, 1);
            try {
                String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21212null22279 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211null22283_failAssert708() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", null);
            try {
                String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21211null22283 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217null22292_failAssert709() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.clone();
            org.junit.Assert.fail("tagReuseForbidden_mg21217null22292 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219null22298_failAssert710() throws Exception {
        try {
            String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
            org.junit.Assert.fail("tagReuseForbidden_mg21219null22298 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188null22242_failAssert711() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21188null22242 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21200null22258_failAssert712() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21200__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("9v2", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21200null22258 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189null22264_failAssert713() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21189null22264 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21203null22240_failAssert714() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21203__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(":", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21203null22240 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201null22241_failAssert715() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21201null22241 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21202null22257_failAssert716() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21202__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("\n", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21202null22257 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220null22307_failAssert717() throws Exception {
        try {
            Object __DSPOT_tag_5271 = new Object();
            String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, null);
            org.junit.Assert.fail("tagReuseForbidden_mg21220null22307 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191null22245_failAssert718() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21191null22245 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192null22253_failAssert719() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21192null22253 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190null22239_failAssert720() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", 1);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21190null22239 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195null22250_failAssert721() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21195null22250 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21196null22255_failAssert722() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21196__3 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("foo", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21196null22255 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212_add22051_failAssert723() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21212_add22051 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211_add22053_failAssert724() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21211_add22053 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215_add22055_failAssert725() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_add21215_add22055 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_add22058_failAssert726() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.clone();
            org.junit.Assert.fail("tagReuseForbidden_mg21217_add22058 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21201_add22009_failAssert727() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitString21201__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21201_add22009 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21213_add22043_failAssert728() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbiddenlitNum21213__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21213__6 = nameAllocator.newName("bar", 1191313852);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21213_add22043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220_add22064_failAssert729() throws Exception {
        try {
            Object __DSPOT_tag_5271 = new Object();
            String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
            org.junit.Assert.fail("tagReuseForbidden_mg21220_add22064 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("tag 1 cannot be used for both \'foo\' and \'foo_\'", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217_rv22231_failAssert733() throws Exception {
        try {
            Object __DSPOT_tag_5416 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            NameAllocator __DSPOT_invoc_12 = nameAllocator.clone();
            __DSPOT_invoc_12.get(__DSPOT_tag_5416);
            org.junit.Assert.fail("tagReuseForbidden_mg21217_rv22231 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("unknown tag: java.lang.Object@60cfa6fe", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212null22281_failAssert734() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName(null, Integer.MIN_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21212null22281 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211null22284_failAssert735() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName(null, Integer.MAX_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21211null22284 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215null22286_failAssert736() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_add21215null22286 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21217null22293_failAssert737() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21217__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.clone();
            org.junit.Assert.fail("tagReuseForbidden_mg21217null22293 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21219null22297_failAssert738() throws Exception {
        try {
            String __DSPOT_suggestion_5269 = "VV^{mE6Di_-Obu `nVnU";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21219__4 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21219__9 = nameAllocator.newName(__DSPOT_suggestion_5269);
            org.junit.Assert.fail("tagReuseForbidden_mg21219null22297 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21188null22243_failAssert739() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21188__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21188null22243 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21189null22265_failAssert740() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21189__3 = nameAllocator.newName("2oo", 1);
            try {
                nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21189null22265 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg21220null22304_failAssert741() throws Exception {
        try {
            Object __DSPOT_tag_5271 = new Object();
            String __DSPOT_suggestion_5270 = "ux2|!5k{n;V+ u`@6=(q";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg21220__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg21220__11 = nameAllocator.newName(__DSPOT_suggestion_5270, __DSPOT_tag_5271);
            org.junit.Assert.fail("tagReuseForbidden_mg21220null22304 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21191null22246_failAssert742() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21191__3 = nameAllocator.newName("fo", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21191null22246 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21192null22252_failAssert743() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21192__3 = nameAllocator.newName("::c", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21192null22252 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21190null22237_failAssert744() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21190__3 = nameAllocator.newName("f{oo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21190null22237 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21195null22249_failAssert745() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21195__3 = nameAllocator.newName(":", 1);
            try {
                nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21195null22249 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString21196null22256_failAssert746() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString21196__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("foo", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString21196null22256 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21212null22280_failAssert747() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21212__3 = nameAllocator.newName("foo", null);
            try {
                String o_tagReuseForbiddenlitNum21212__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21212null22280 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum21211null22282_failAssert748() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum21211__3 = nameAllocator.newName(null, 1);
            try {
                String o_tagReuseForbiddenlitNum21211__6 = nameAllocator.newName("bar", Integer.MAX_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum21211null22282 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_add21215null22287_failAssert749() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_add21215__3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName(null, 1);
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_add21215null22287 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }
}

