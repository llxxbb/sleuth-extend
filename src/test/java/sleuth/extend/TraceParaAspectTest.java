package sleuth.extend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraceParaAspectTest {

    @Test
    void mergeTags() {
        // all zero
        String[] strings = TraceParaAspect.mergeTags(new String[0], new String[0]);
        assertEquals(0, strings.length);

        // copy from one
        String[] one = new String[2];
        one[0] = "lxb";
        one[1] = "hello";
        strings = TraceParaAspect.mergeTags(one, new String[0]);
        assertEquals(2, strings.length);
        assertEquals("lxb", strings[0]);
        assertEquals("hello", strings[1]);

        // copy from des
        strings = TraceParaAspect.mergeTags(new String[0], one);
        assertEquals(2, strings.length);
        assertEquals("lxb", strings[0]);
        assertEquals("hello", strings[1]);

        // copy all
        strings = TraceParaAspect.mergeTags(one, one);
        assertEquals(4, strings.length);
        assertEquals("lxb", strings[0]);
        assertEquals("hello", strings[1]);
        assertEquals("lxb", strings[2]);
        assertEquals("hello", strings[3]);
    }
}