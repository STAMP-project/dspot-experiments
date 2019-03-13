package com.tagtraum.perf.gcviewer.model;


import ExtendedType.UNDEFINED;
import Type.DEF_NEW;
import Type.PERM;
import Type.TENURED;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent.Type;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for class {@link GCEvent}.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 04.02.2012</p>
 */
public class TestGcEvent {
    private GCEvent gcEvent;

    private GCEvent fullGcEvent;

    @Test
    public void testAddGc() {
        // when GC was parsed, only "young" information really is present; "tenured" must be inferred
        Assert.assertEquals("number of details", 1, gcEvent.details.size());
        GCEvent defNewEvent = gcEvent.details().next();
        Assert.assertEquals("type", DEF_NEW.getName(), defNewEvent.getExtendedType().getName());
        Assert.assertEquals("getYoung", defNewEvent, gcEvent.getYoung());
        GCEvent tenured = gcEvent.getTenured();
        Assert.assertNotNull("tenured", tenured);
    }

    @Test
    public void testAddFullGc() {
        // when Full GC was parsed, "young" information was deferred, other were parsed.
        Assert.assertEquals("number of details", 2, fullGcEvent.details.size());
        GCEvent tenured = fullGcEvent.details.get(0);
        Assert.assertEquals("type", TENURED.getName(), tenured.getExtendedType().getName());
        Assert.assertEquals("getTenured", tenured, fullGcEvent.getTenured());
        GCEvent perm = fullGcEvent.details.get(1);
        Assert.assertEquals("type", PERM.getName(), perm.getExtendedType().getName());
        Assert.assertEquals("getPerm", perm, fullGcEvent.getPerm());
        GCEvent young = fullGcEvent.getYoung();
        Assert.assertNotNull("young", young);
    }

    @Test
    public void testGetInferredYoungFullGcEvent() {
        GCEvent young = fullGcEvent.getYoung();
        Assert.assertEquals("type", UNDEFINED, young.getExtendedType());
        Assert.assertEquals("preused", (141564 - 38156), young.getPreUsed());
        Assert.assertEquals("postused", (54636 - 54636), young.getPostUsed());
        Assert.assertEquals("total", (506944 - 349568), young.getTotal());
        Assert.assertEquals("pause", 0.601315, young.getPause(), 1.0E-8);
    }

    @Test
    public void testGetInferredTenuredGcEvent() {
        GCEvent tenured = gcEvent.getTenured();
        Assert.assertEquals("tenured type", UNDEFINED, tenured.getExtendedType());
        Assert.assertEquals("preused", (194540 - 139904), tenured.getPreUsed());
        Assert.assertEquals("postused", (60292 - 5655), tenured.getPostUsed());
        Assert.assertEquals("total tenured", (506944 - 157376), tenured.getTotal());
        Assert.assertEquals("pause", 0.0543079, tenured.getPause(), 1.0E-6);
    }

    @Test
    public void testCloneAndMerge() throws Exception {
        // 87.707: [GC 87.707: [DefNew: 139904K->5655K(157376K), 0.0543079 secs] 194540K->60292K(506944K), 0.0544020 secs] [Times: user=0.03 sys=0.02, real=0.06 secs]
        // 83.403: [Full GC 83.403: [Tenured: 38156K->54636K(349568K), 0.6013150 secs] 141564K->54636K(506944K), [Perm : 73727K->73727K(73728K)], 0.6014256 secs] [Times: user=0.58 sys=0.00, real=0.59 secs]
        GCEvent detailEvent1 = new GCEvent(0.01, 100, 90, 1000, 0.25, Type.G1_YOUNG);
        GCEvent detailEvent2 = new GCEvent(0.01, 500, 200, 1000, 0.29, Type.TENURED);
        GCEvent clonedEvent = detailEvent1.cloneAndMerge(detailEvent2);
        Assert.assertThat("name", clonedEvent.getTypeAsString(), Matchers.equalTo("GC pause (young)+Tenured"));
        Assert.assertThat("heap before", clonedEvent.getPreUsed(), Matchers.is((100 + 500)));
    }
}

