package application.stormlyapp.repositories;

import application.stormlyapp.model.Record;
import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Record, Long> {
}
