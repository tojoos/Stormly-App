package application.stormlyapp.services;

import application.stormlyapp.model.Record;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Set;

public interface RecordService {
    Record save(Record record);
    Set<Record> findAll();
    Record findById(Long id);
    void deleteById(Long id);
    void fetchData();
    Set<Record> findAllBeforeDate(Long amount, TemporalUnit units);
    Record findByDateTime(LocalDateTime dateTime);
    List<Record> findByDateHourly(LocalDateTime dateTime);
    Record calculateAverageOfRecords(List<Record> records);
    List<Record> findByDateDaily(LocalDateTime dateTime);
    String getFormattedDate(LocalDateTime date);
}
