package application.stormlyapp.services;

import application.stormlyapp.model.Record;
import application.stormlyapp.repositories.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RecordServiceTest {

    @Mock
    RecordRepository recordRepository;

    RecordService recordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recordService = new RecordServiceImpl(recordRepository);
    }

    @Test
    void testSave() {
        //when
        when(recordRepository.save(any(Record.class))).thenReturn(Record.builder().id(1L).build());
        Record savedRecord = recordService.save(Record.builder().id(4L).build());

        //then
        assertNotNull(savedRecord);
    }

    @Test
    void testSaveNull() {
        //when
        when(recordRepository.save(any(Record.class))).thenReturn(Record.builder().id(1L).build());
        Record savedRecord = recordService.save(null);

        //then
        assertNull(savedRecord);
    }

    @Test
    void testFindAll() {
        //given
        Set<Record> recordList = new HashSet<>();
        recordList.add(Record.builder().id(1L).build());
        recordList.add(Record.builder().id(2L).build());
        recordList.add(Record.builder().id(3L).build());

        //when
        when(recordRepository.findAll()).thenReturn(recordList);
        Set<Record> foundRecords = recordService.findAll();

        //then
        assertEquals(3, foundRecords.size());
        verify(recordRepository,times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        //when
        when(recordRepository.findAll()).thenReturn(new HashSet<>());
        Set<Record> foundRecords = recordService.findAll();

        //then
        assertEquals(0, foundRecords.size());
        verify(recordRepository,times(1)).findAll();
    }

    @Test
    void testFindById() {
        //given
        Record record = Record.builder().id(9L).build();

        //when
        when(recordRepository.findById(any())).thenReturn(Optional.of(record));
        Record foundRecord = recordService.findById(1L);

        //then
        assertNotNull(foundRecord);
        assertEquals(9L, foundRecord.getId());
        verify(recordRepository,times(1)).findById(any());
    }

    @Test
    void testFindByIdNotFound() {
        //when
        when(recordRepository.findById(any())).thenReturn(Optional.empty());
        Record foundRecord = recordService.findById(1L);

        //then
        assertNull(foundRecord);
        verify(recordRepository, times(1)).findById(any());
    }

    @Test
    void testDeleteById() {
        HashSet<Record> records = new HashSet<>();
        records.add(Record.builder().id(1L).build());
        records.add(Record.builder().id(2L).build());
        records.add(Record.builder().id(3L).build());

        //when
        when(recordRepository.findAll()).thenReturn(records);

        //then
        recordService.deleteById(2L);

        verify(recordRepository, times(1)).findAll();
        verify(recordRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testFetchData() {
        //todo fetch data tests - no file, no records etc
    }
}