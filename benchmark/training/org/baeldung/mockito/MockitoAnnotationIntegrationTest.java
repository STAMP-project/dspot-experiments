package org.baeldung.mockito;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;


// @RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationIntegrationTest {
    @Mock
    private List<String> mockedList;

    @Spy
    private List<String> spiedList = new ArrayList<>();

    // tests
    @Test
    public void whenNotUseMockAnnotation_thenCorrect() {
        final List<String> mockList = Mockito.mock(List.class);
        mockList.add("one");
        Mockito.verify(mockList).add("one");
        Assert.assertEquals(0, mockList.size());
        Mockito.when(mockList.size()).thenReturn(100);
        Assert.assertEquals(100, mockList.size());
    }

    @Test
    public void whenUseMockAnnotation_thenMockIsInjected() {
        mockedList.add("one");
        Mockito.verify(mockedList).add("one");
        Assert.assertEquals(0, mockedList.size());
        Mockito.when(mockedList.size()).thenReturn(100);
        Assert.assertEquals(100, mockedList.size());
    }

    @Test
    public void whenNotUseSpyAnnotation_thenCorrect() {
        final List<String> spyList = Mockito.spy(new ArrayList<String>());
        spyList.add("one");
        spyList.add("two");
        Mockito.verify(spyList).add("one");
        Mockito.verify(spyList).add("two");
        Assert.assertEquals(2, spyList.size());
        Mockito.doReturn(100).when(spyList).size();
        Assert.assertEquals(100, spyList.size());
    }

    @Test
    public void whenUseSpyAnnotation_thenSpyIsInjectedCorrectly() {
        spiedList.add("one");
        spiedList.add("two");
        Mockito.verify(spiedList).add("one");
        Mockito.verify(spiedList).add("two");
        Assert.assertEquals(2, spiedList.size());
        Mockito.doReturn(100).when(spiedList).size();
        Assert.assertEquals(100, spiedList.size());
    }

    @Test
    public void whenNotUseCaptorAnnotation_thenCorrect() {
        final List<String> mockList = Mockito.mock(List.class);
        final ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        mockList.add("one");
        Mockito.verify(mockList).add(arg.capture());
        Assert.assertEquals("one", arg.getValue());
    }

    @Captor
    private ArgumentCaptor<String> argCaptor;

    @Test
    public void whenUseCaptorAnnotation_thenTheSam() {
        mockedList.add("one");
        Mockito.verify(mockedList).add(argCaptor.capture());
        Assert.assertEquals("one", argCaptor.getValue());
    }

    @Mock
    private Map<String, String> wordMap;

    @InjectMocks
    private MyDictionary dic = new MyDictionary();

    @Test
    public void whenUseInjectMocksAnnotation_thenCorrect() {
        Mockito.when(wordMap.get("aWord")).thenReturn("aMeaning");
        Assert.assertEquals("aMeaning", dic.getMeaning("aWord"));
    }
}

