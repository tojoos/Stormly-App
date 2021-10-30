package application.stormlyapp.repositories;

import application.stormlyapp.model.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class RecordRepositoryTest {

    @Autowired
    RecordRepository recordRepository;

    @BeforeEach
    void setUp() {
        recordRepository.save(Record.builder().id(1L).temperature(20).humidity(0.5).pressure(1000)
                .date(LocalDateTime.of(2020,10,10,5,0,0)).build());
        recordRepository.save(Record.builder().id(2L).temperature(20).humidity(0.5).pressure(1000)
                .date(LocalDateTime.of(2021,8,10,5,0,0)).build());
        recordRepository.save(Record.builder().id(3L).temperature(20).humidity(0.5).pressure(1000)
                .date(LocalDateTime.of(2021,9,4,15,10,0)).build());
        recordRepository.save(Record.builder().id(9L).temperature(20).humidity(0.5).pressure(1000)
                .date(LocalDateTime.of(2020,11,10,1,10,0)).build());
    }

    @Test
    void testFindAll() {
        //then
        Set<Record> foundRecords = new HashSet<>();
        recordRepository.findAll().forEach(foundRecords::add);
        assertEquals(4, foundRecords.size());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteById() {
        //then
        recordRepository.deleteById(1L);
        assertEquals(3, recordRepository.findAll().spliterator().estimateSize());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteByIdNotFound() {
        //then
        assertThrows(EmptyResultDataAccessException.class, () -> recordRepository.deleteById(99L));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testSave() {
        //then
        Record recordToSave = Record.builder().id(9L).temperature(12).humidity(0.5).pressure(1001).date(LocalDateTime.now()).build();
        recordRepository.save(recordToSave);
        assertEquals(5, recordRepository.findAll().spliterator().estimateSize());
    }
}
