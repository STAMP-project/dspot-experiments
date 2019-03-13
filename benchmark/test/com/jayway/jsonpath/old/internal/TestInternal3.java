package com.jayway.jsonpath.old.internal;


import com.jayway.jsonpath.internal.path.PathCompiler;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 *
 */
public class TestInternal3 extends TestBase {
    /* RootPathToken rootToken = new RootPathToken();
    //rootToken.append(new PropertyPathToken("stores"));
    //rootToken.append(new ArrayPathToken(asList(0, 1), ArrayPathToken.Operation.INDEX_SEQUENCE));
    //rootToken.append(new ArrayPathToken(asList(0, 2), ArrayPathToken.Operation.SLICE_BETWEEN));
    //rootToken.append(new FilterPathToken(Filter.filter(Criteria.where("name").is("store_1"))));
    //rootToken.append(new WildcardPathToken());
    rootToken.append(new ScanPathToken());
    rootToken.append(new ArrayPathToken(asList(0), ArrayPathToken.Operation.INDEX_SEQUENCE));
    rootToken.append(new PropertyPathToken("name"));
     */
    @Test
    public void a_root_object_can_be_evaluated() {
        Map<String, Object> result = PathCompiler.compile("$").evaluate(TestBase.DOC, TestBase.DOC, TestBase.CONF).getValue();
        assertThat(result).containsKey("store").hasSize(1);
    }

    @Test
    public void a_definite_array_item_property_can_be_evaluated() {
        String result = PathCompiler.compile("$.store.book[0].author").evaluate(TestBase.DOC, TestBase.DOC, TestBase.CONF).getValue();
        assertThat(result).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_wildcard_array_item_property_can_be_evaluated() {
        List result = PathCompiler.compile("$.store.book[*].author").evaluate(TestBase.DOC, TestBase.DOC, TestBase.CONF).getValue();
        assertThat(result).containsOnly("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");
    }
}

