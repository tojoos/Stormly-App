package application.stormlyapp.repositories;

import application.stormlyapp.model.Record;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecordRepository extends CrudRepository<Record, Long> {
    Optional<Record> findRecordByDate(LocalDateTime date);
    Optional<Record> findAllByDate(LocalDateTime date);
}
