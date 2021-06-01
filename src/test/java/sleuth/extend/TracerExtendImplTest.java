package sleuth.extend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TracerExtendImplTest {
    @Test
    public void jsonTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals("5", mapper.writeValueAsString(5));
        assertEquals("\"6\"", mapper.writeValueAsString("6"));
    }
}