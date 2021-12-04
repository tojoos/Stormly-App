package application.stormlyapp.services;

import application.stormlyapp.exceptions.NotFoundException;
import application.stormlyapp.model.Record;
import application.stormlyapp.repositories.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RecordServiceTest {

    @Mock
    RecordRepository recordRepository;

    RecordService recordService;

    List<Record> records;
    Record record1, record2, record3, record4, record5, record6;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        record1 = Record.builder().date(LocalDateTime.of(2020,12,5,10,0,0)).build();
        record2 = Record.builder().date(LocalDateTime.of(2020,12,5,10,30,0)).build();
        record3 = Record.builder().date(LocalDateTime.of(2020,12,5,11,30,0)).build();
        record4 = Record.builder().date(LocalDateTime.of(2020,12,6,10,0,0)).build();
        record5 = Record.builder().date(LocalDateTime.of(2020,12,7,10,0,0)).build();
        record6 = Record.builder().date(LocalDateTime.of(2020,12,8,10,0,0)).build();
        records = new ArrayList<>();
        records.add(record1);
        records.add(record2);
        records.add(record3);
        records.add(record4);
        records.add(record5);
        records.add(record6);

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
        when(recordRepository.save(any(Record.class))).thenReturn(null);
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
    void testFetchData() throws FileNotFoundException {
        //given
        String FILE_URL = "src/main/resources/data.txt";
        BufferedReader br = new BufferedReader(new FileReader(FILE_URL));
        long linesToRead = br.lines().count();

        //when
        when(recordRepository.save(any(Record.class))).thenReturn(Record.builder().id(1L).build());

        //then
        recordService.fetchData();

        verify(recordRepository,times((int) linesToRead)).save(any(Record.class));
    }

    @Test
    void testFindAllBeforeDate() {
        //given
        Long amount1 = 14L;
        TemporalUnit unit1 = ChronoUnit.DAYS;
        Long amount2 = 2L;
        TemporalUnit unit2 = ChronoUnit.YEARS;
        Set<Record> sampleRecords = new HashSet<>();
        sampleRecords.add(Record.builder().id(1L).temperature(20).humidity(0.5).pressure(1000)
                    .date(LocalDateTime.of(2020,10,10,5,0,0)).build());
        sampleRecords.add(Record.builder().id(2L).temperature(20).humidity(0.5).pressure(1000)
                    .date(LocalDateTime.of(2021,10,29,5,0,0)).build());
        sampleRecords.add(Record.builder().id(2L).temperature(20).humidity(0.5).pressure(1000)
                .date(LocalDateTime.of(2021,10,28,11,15,0)).build());
        sampleRecords.add(Record.builder().id(3L).temperature(20).humidity(0.5).pressure(1000)
                    .date(LocalDateTime.of(2021,9,4,15,10,0)).build());
        sampleRecords.add(Record.builder().id(9L).temperature(20).humidity(0.5).pressure(1000)
                    .date(LocalDateTime.of(2020,11,10,1,10,0)).build());

        //when
        when(recordRepository.findAll()).thenReturn(sampleRecords);

        //then
        Set<Record> foundRecords1 = recordService.findAllBeforeDate(amount1, unit1);
        Set<Record> foundRecords2 = recordService.findAllBeforeDate(amount2, unit2);
        assertEquals(0, foundRecords1.size());
        assertEquals(5, foundRecords2.size());
    }

    @Test
    void testFindByDate() {
        //given

        //when
        when(recordRepository.findAll()).thenReturn(records);

        Record recordFound1 = recordService.findByDateTime(LocalDateTime.of(2020,12,5,10,15,0));
        Record recordFound2 = recordService.findByDateTime(LocalDateTime.of(2020,12,7,10,15,0));
        Record recordFound3 = recordService.findByDateTime(LocalDateTime.of(2020,12,5,10,30,0));

        //then
        assertEquals(recordFound1, record1);
        assertEquals(recordFound2, record5);
        assertEquals(recordFound3, record2);
    }

    @Test
    void testFindByDateNotFound() {
        //given
        List<Record> records = new ArrayList<>();

        //when
        when(recordRepository.findAll()).thenReturn(records);

        //then
        assertThrows(NotFoundException.class, () -> recordService.findByDateTime(LocalDateTime.now()));
    }

    @Test
    void testCalculateAverageOfRecords() {
        //given
        List<Record> recordList = new LinkedList<>();
        recordList.add(record1);
        recordList.add(record2);
        recordList.add(record3);

        //when
        Record avgRecord = recordService.calculateAverageOfRecords(recordList);

        //then
        double avgTemp = (record1.getTemperature() + record2.getTemperature() + record3.getTemperature())/3;
        double avgHumidity = (record1.getHumidity() + record2.getHumidity() + record3.getHumidity())/3;
        assertEquals(avgTemp, avgRecord.getTemperature());
        assertEquals(avgHumidity, avgRecord.getHumidity());
    }

    @Test
    void testFindByDateHourly() {
        //given
        List<Record> records = new LinkedList<>();
        records.add(Record.builder().temperature(19).humidity(0.12).pressure(1002).exposure(800).date(LocalDateTime.now().minusMinutes(5)).build());
        records.add(Record.builder().temperature(18).humidity(0.18).pressure(1005).exposure(600).date(LocalDateTime.now().minusMinutes(15)).build());
        records.add(Record.builder().temperature(19).humidity(0.38).pressure(1012).exposure(700).date(LocalDateTime.now().minusMinutes(30)).build());
        records.add(Record.builder().temperature(18.7).humidity(0.48).pressure(1022).exposure(670).date(LocalDateTime.now().minusHours(1).minusMinutes(15)).build());
        records.add(Record.builder().temperature(18.5).humidity(0.26).pressure(1011).exposure(520).date(LocalDateTime.now().minusHours(2).minusMinutes(30)).build());
        records.add(Record.builder().temperature(16).humidity(0.12).pressure(1015).exposure(460).date(LocalDateTime.now().minusHours(3).minusMinutes(56)).build());
        records.add(Record.builder().temperature(17).humidity(0.32).pressure(1022).exposure(410).date(LocalDateTime.now().minusHours(3).minusMinutes(35)).build());
        records.add(Record.builder().temperature(15).humidity(0.41).pressure(1021).exposure(300).date(LocalDateTime.now().minusHours(4).minusMinutes(25)).build());

        //when
        when(recordRepository.findAll()).thenReturn(records);

        List<Record> recordsHourly = recordService.findByDateHourly(LocalDateTime.now());

        //then
        assertEquals(5, recordsHourly.size());
        assertEquals((0.32 + 0.12)/2, recordsHourly.get(1).getHumidity());
        assertEquals((double)(19 + 18 + 19)/3, recordsHourly.get(4).getTemperature());
    }

    @Test
    void testFindByDateDaily() {
        //given
        List<Record> records = new LinkedList<>();
        records.add(Record.builder().temperature(11).humidity(0.12).pressure(1002).exposure(800).date(LocalDateTime.now().minusHours(5)).build());
        records.add(Record.builder().temperature(14).humidity(0.18).pressure(1005).exposure(600).date(LocalDateTime.now().minusHours(7)).build());
        records.add(Record.builder().temperature(14.5).humidity(0.38).pressure(1012).exposure(700).date(LocalDateTime.now().minusHours(15)).build());
        records.add(Record.builder().temperature(18.7).humidity(0.48).pressure(1022).exposure(670).date(LocalDateTime.now().minusDays(1).minusHours(5)).build());
        records.add(Record.builder().temperature(18.5).humidity(0.26).pressure(1011).exposure(520).date(LocalDateTime.now().minusDays(2).minusHours(3)).build());
        records.add(Record.builder().temperature(16).humidity(0.12).pressure(1015).exposure(460).date(LocalDateTime.now().minusDays(2).minusHours(5)).build());
        records.add(Record.builder().temperature(17).humidity(0.32).pressure(1022).exposure(410).date(LocalDateTime.now().minusDays(3).minusHours(6)).build());
        records.add(Record.builder().temperature(15).humidity(0.61).pressure(1021).exposure(300).date(LocalDateTime.now().minusDays(4).minusHours(15)).build());
        records.add(Record.builder().temperature(15).humidity(0.41).pressure(1021).exposure(300).date(LocalDateTime.now().minusDays(4).minusHours(17)).build());

        //when
        when(recordRepository.findAll()).thenReturn(records);

        List<Record> recordsDaily = recordService.findByDateDaily(LocalDateTime.now());

        //then
        assertEquals(5, recordsDaily.size());
        assertEquals((0.61 + 0.41)/2, recordsDaily.get(0).getHumidity());
        assertEquals((11 + 14 + 14.5)/3, recordsDaily.get(4).getTemperature());
    }

    @Test
    void testGetFormattedDate() {
        //given
        Record record = Record.builder().date(LocalDateTime.of(2020,10,10,9,30,0)).build();

        //when
        String formattedDate = recordService.getFormattedDate(record.getDate());

        //then
        assertEquals("09:30 10/10/2020", formattedDate);
    }

}
