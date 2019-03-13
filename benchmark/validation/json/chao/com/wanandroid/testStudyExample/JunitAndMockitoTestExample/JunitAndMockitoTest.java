package json.chao.com.wanandroid.testStudyExample.JunitAndMockitoTestExample;


import android.widget.TextView;
import json.chao.com.wanandroid.rule.MyRuler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JunitAndMockitoTest {
    @Spy
    Person mPerson;

    @Spy
    Person mPerson1;

    @InjectMocks
    Home mHome;

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Rule
    public MyRuler mMyRuler = new MyRuler();

    @Test
    public void WhenAndThenReturnTest() {
        Mockito.when(mPerson.getAge()).thenReturn(18);
        System.out.println(mPerson.getAge());
    }

    @Test
    public void verifyTest() {
        Mockito.when(mPerson.getAge()).thenReturn(25);
        System.out.println(mPerson.getAge());
        Mockito.verify(mPerson, Mockito.after(1000)).getAge();
        System.out.println(mPerson.getAge());
        Mockito.verify(mPerson, Mockito.atLeast(2)).getAge();
        System.out.println(mPerson.getAge());
        Mockito.verify(mPerson, Mockito.atMost(5)).getAge();
        System.out.println(mPerson.getAge());
    }

    @Test
    public void matcherTest() {
        Mockito.when(mPerson.setAge(ArgumentMatchers.contains("1"))).thenReturn(10);
        System.out.println(mPerson.setAge("01"));
        Mockito.when(mPerson.setAge(ArgumentMatchers.anyString())).thenReturn(10);
        System.out.println(mPerson.setAge(""));
        Mockito.when(mPerson.setAge(ArgumentMatchers.any(String.class))).thenReturn(100);
        System.out.println(mPerson.setAge("fdsfsd"));
        Mockito.when(mPerson.setAge(ArgumentMatchers.argThat(( argument) -> argument.contains("20")))).thenReturn(100);
        TextView textView = Mockito.mock(TextView.class);
        textView.setText("11");
        textView.setText("22");
        Mockito.verify(textView, Mockito.times(2)).setText(ArgumentMatchers.anyString());
        // Void?????, ??Void????????doNothing()
        Mockito.doNothing().doThrow(new RuntimeException()).when(textView).setText("11");
        // ?Void?????
        Mockito.when(textView.getText()).thenThrow(new RuntimeException());
    }

    @Test
    public void spyTest() {
        Person person = Mockito.spy(Person.class);
        Mockito.when(person.getAge()).thenReturn(20);
        System.out.println(person.getAge());
        Assert.assertEquals(person.getAge(), 20);
    }

    @Test
    public void inOrderTest() {
        mPerson.setAge("25");
        mPerson.setSex("?");
        mPerson1.setAge("26");
        mPerson1.setSex("?");
        InOrder inOrder = Mockito.inOrder(mPerson, mPerson1);
        inOrder.verify(mPerson).setAge("25");
        inOrder.verify(mPerson).setSex("?");
        inOrder.verify(mPerson1).setAge("26");
        inOrder.verify(mPerson1).setSex("?");
    }

    @Test
    public void injectMockitoTest() {
        Mockito.when(mPerson1.getAge()).thenReturn(20);
        // when(mPerson.getAge()).thenReturn(20);
        Assert.assertEquals(mHome.getAge(), 20);
        System.out.println(mHome.getAge());
    }

    @Test
    public void captorTest() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        mPerson1.setAge("20");
        Mockito.verify(mPerson1).setAge(captor.capture());
        Assert.assertEquals(captor.getValue(), "20");
        System.out.println(captor.getAllValues());
    }

    @Test
    public void continuousInvocation() {
        Mockito.when(mPerson1.getAge()).thenReturn(1, 2, 3);
        // .thenThrow(new NullPointerException());
        System.out.println(mPerson1.getAge());
        System.out.println(mPerson1.getAge());
        System.out.println(mPerson1.getAge());
        System.out.println(mPerson1.getAge());
    }
}

