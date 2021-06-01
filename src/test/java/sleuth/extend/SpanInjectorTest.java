package sleuth.extend;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpanInjectorTest {

    @Test
    public void addTagThirdPartTest() {
        // data
        ClassForTest classForTest = new ClassForTest();
        // init
        TracerExtend tracer = mock(TracerExtend.class);

        String[] parts = new String[2];
        parts[0] = "b";
        // err
        parts[1] = "err";
        SpanInjector.addTag(parts, null, "a", classForTest, tracer);
        verify(tracer, times(0)).addTag(any(), any(), any());
        // ok
        parts[1] = "c";
        SpanInjector.addTag(parts, null, "a", classForTest, tracer);
        verify(tracer).addTag(null, "a", classForTest.b.c);
    }

    @Test
    public void addTagSecondPartTest() {
        // data
        ClassForTest classForTest = new ClassForTest();
        // init
        TracerExtend tracer = mock(TracerExtend.class);
        // err
        String[] parts = new String[1];
        parts[0] = "err";
        SpanInjector.addTag(parts, null, "a", classForTest, tracer);
        verify(tracer, times(0)).addTag(any(), any(), any());
        // ok
        parts[0] = "b";
        SpanInjector.addTag(parts, null, "a", classForTest, tracer);
        verify(tracer).addTag(null, "a", classForTest.b);
    }

    @Test
    public void addTagFirstPartTest() {
        // data
        ClassForTest classForTest = new ClassForTest();
        // init
        TracerExtend tracer = mock(TracerExtend.class);
        // call
        SpanInjector.addTag(null, null, "a", classForTest, tracer);
        // verify
        verify(tracer).addTag(null, "a", classForTest);
    }

    @Test
    public void getValueTest() {
        // data
        ClassForTest testee = new ClassForTest();
        testee.publicString = "publicString";
        testee.setPrivateString("my test");
        // check
        Object value = SpanInjector.getValue(testee, "publicString", "myPath");
        assertEquals("publicString", value);
        value = SpanInjector.getValue(testee, "privateString", "myPath");
        assertEquals("my test", value);
        value = SpanInjector.getValue(testee, "b", "myPath");
        assertNotNull(value);
        value = SpanInjector.getValue(testee, "notFound", "myPath");
        assertNull(value);
    }


    @Test
    void getNextParts() {
        String[] parts = new String[5];
        parts[0] = "a";
        parts[1] = "b";
        parts[2] = "c";
        parts[3] = "d";
        parts[4] = "e";
        String[] nextParts = SpanInjector.getNextParts(parts);
        assertNotNull(nextParts);
        assertEquals(4, nextParts.length);
        assertEquals("b", nextParts[0]);
    }
}