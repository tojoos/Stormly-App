package application.stormlyapp.services;

import application.stormlyapp.model.Record;

import java.util.Set;

public interface RecordService {
    Record save(Record record);
    Set<Record> findAll();
    Record findById(Long id);
    void deleteById(Long id);
    void fetchData();
}
