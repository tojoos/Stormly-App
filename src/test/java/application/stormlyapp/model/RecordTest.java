package application.stormlyapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordTest {

    Record record;

    @BeforeEach
    void setUp() {
        record = new Record();
    }

    @Test
    void testBasicGettersAndSetters() {
        Long id = 2L;
        double temp = 28.9;
        record.setTemperature(temp);
        record.setId(id);

        assertEquals(2L, record.getId());
        assertEquals(28.9, record.getTemperature());
    }

    @Test
    void testGetFormattedDate() {
        LocalDateTime date = LocalDateTime.of(2020,5,11,12,0,0);
        record.setDate(date);
        assertEquals("11-05-2020 12:00:00", record.getFormattedDate());
    }
}
