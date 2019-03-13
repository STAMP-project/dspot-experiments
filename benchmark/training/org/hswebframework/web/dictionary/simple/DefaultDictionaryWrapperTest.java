package org.hswebframework.web.dictionary.simple;


import org.hswebframework.web.dictionary.api.DictionaryInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * TODO ????
 *
 * @author zhouhao
 * @since 
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultDictionaryWrapperTest {
    @Mock
    private DictionaryInfoService dictionaryInfoService;

    @InjectMocks
    private DefaultDictionaryWrapper wrapper = new DefaultDictionaryWrapper();

    @Test
    public void test() {
        TestBean bean = new TestBean();
        wrapper.wrap("test", bean);
        System.out.println(bean);
        Assert.assertNotNull(getDict());
        Assert.assertEquals(getDict().length, 2);
        wrapper.persistent("test", bean);
        wrapper.wrap("test2", new EmptyDictBean());
        wrapper.persistent("test2", new EmptyDictBean());
    }
}

