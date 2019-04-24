package org.xwiki.component.internal.namespace;


import org.junit.Assert;
import org.junit.Test;
import org.xwiki.component.namespace.Namespace;
import org.xwiki.component.namespace.NamespaceUtils;


public class AmplNamespaceUtilsTest {
    @Test(timeout = 10000)
    public void toNamespace_literalMutationString1828() throws Exception {
        Namespace o_toNamespace_literalMutationString1828__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString1828__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString1828__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1828__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1828__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1828__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString1828__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1828__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1828__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1828__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString1828__7 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1828__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1828__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1828__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString1828__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString1828__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1828__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1828__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString1828__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1828__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1828__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1828__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1828__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1828__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1828__5)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1828__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1828__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1828__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1828__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString1816() throws Exception {
        Namespace o_toNamespace_literalMutationString1816__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString1816__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString1816__3 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1816__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1816__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1816__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString1816__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1816__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1816__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1816__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString1816__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1816__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1816__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1816__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString1816__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString1816__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1816__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1816__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString1816__1);
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1816__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1816__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1816__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1816__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1816__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1816__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1816__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1816__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1816__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1816__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString1834() throws Exception {
        Namespace o_toNamespace_literalMutationString1834__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString1834__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString1834__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1834__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1834__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1834__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString1834__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1834__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1834__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1834__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString1834__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1834__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1834__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1834__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString1834__9 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1834__9)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__9)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1834__9)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1834__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString1834__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1834__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1834__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1834__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString1834__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1834__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString1834__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1834__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1834__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1834__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1834__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString1822() throws Exception {
        Namespace o_toNamespace_literalMutationString1822__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString1822__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString1822__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1822__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1822__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1822__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString1822__5 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1822__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1822__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1822__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString1822__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1822__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1822__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1822__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString1822__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString1822__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString1822__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1822__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString1822__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1822__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString1822__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1822__3)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1822__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString1822__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString1822__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString1822__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString1822__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString1822__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString1822__7)).getType());
    }
}

