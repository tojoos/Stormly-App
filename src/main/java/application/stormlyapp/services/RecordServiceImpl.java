package application.stormlyapp.services;

import application.stormlyapp.exceptions.NotFoundException;
import application.stormlyapp.model.Record;
import application.stormlyapp.repositories.RecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecordServiceImpl implements RecordService {

    private final String FILE_URL = "src/main/resources/data.txt";

    private final RecordRepository recordRepository;

    public RecordServiceImpl(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public Record save(Record record) {
        if(record != null) {
            log.debug("Record successfully saved");
            return recordRepository.save(record);
        } else {
            log.debug("Can't save null record.");
            return null;
        }
    }

    @Override
    public Set<Record> findAll() {
        HashSet<Record> records = new HashSet<>();
        recordRepository.findAll().forEach(records::add);
        return records;
    }

    @Override
    public Record findById(Long id) {
        return recordRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        Record foundRecord = findAll().stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
        if(foundRecord != null) {
            log.debug("Record id: " + id + " deleted successfully.");
            recordRepository.deleteById(id);
        } else {
            log.debug("Couldn't delete Record id: " + id + ". Record doesn't exist in database");
            throw new NotFoundException("For ID: " + id + " record was not found.");
        }
    }

    @Override
    public Set<Record> findAllBeforeDate(Long amount, TemporalUnit units) {
        Set<Record> matchingRecords = new HashSet<>();
        for(Record record : this.findAll()) {
            if(record.getDate().isAfter(LocalDateTime.now().minus(amount, units))) {
                matchingRecords.add(record);
            }
        }
        return matchingRecords;
    }

    @Override
    public void fetchData() {
        List<Record> records = new ArrayList<>();
        int recordCount = 0;
        int importedRecordsCount = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_URL));
            String line;
            double temperature, humidity, pressure, exposure;
            LocalDateTime date;

            while((line = br.readLine()) != null) {
                String[] strings = line.split(" ");
                if(strings.length == 5) {
                    date = LocalDateTime.parse(strings[0]);
                    temperature = Double.parseDouble(strings[1]);
                    humidity = Double.parseDouble(strings[2]);
                    pressure = Double.parseDouble(strings[3]);
                    exposure = Double.parseDouble(strings[4]);
                    importedRecordsCount++;
                    records.add(Record.builder().temperature(temperature).humidity(humidity).pressure(pressure).exposure(exposure).date(date).build());
                } else {
                    log.error("Wrong data input.. skipping record");
                }
                recordCount++;
            }
            records.forEach(this::save);
            log.debug("Successfully imported: " + importedRecordsCount + "/" + recordCount + " records.");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
