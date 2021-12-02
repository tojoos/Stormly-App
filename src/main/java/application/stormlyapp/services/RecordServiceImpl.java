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
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Record findByDateTime(LocalDateTime dateTime) {
        Set<Record> records = findAll();
        Set<Record> sortedRecords = records.stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for(Record record : sortedRecords) {
            if(dateTime.isAfter(record.getDate()) || dateTime.isEqual(record.getDate())) {
                return record;
            }
        }
        throw new NotFoundException("Not data in database for given datetime: " + dateTime);
    }

    @Override
    public List<Record> findByDateHourly(LocalDateTime dateTime) {
        List<Record> records = new LinkedList<>();
        for(int i=0; i<5; i++) {
            records.add(findByDateTime(dateTime.minusHours(i)));
        }
        return records;
    }
    
//    public Record calculateAverageOfRecords(List<Record> records) {
//    	double sumTemp = 0;
//    	double sumPressure = 0;
//    	double sumHumidity = 0;
//    	double sumExposure = 0;
//
//    	for(Record record : records) {
//    		sumTemp += record.getTemperature();
//    		sumPressure += record.getPressure();
//    		sumHumidity += record.getHumidity();
//    		sumExposure += record.getExposure();
//    	}
//    	
//    	int number = records.size();
//    	Record avgRecord = Record.builder().temperature(sumTemp/number).pressure(sumPressure/number).humidity(sumHumidity/number).exposure(sumExposure/number).build();
//    	return avgRecord;
//    }
//    
//    public List<Record> findByDateDaily(LocalDateTime dateTime) {
//    	List<Record> recordsDaily = new LinkedList<>(); 
//    	List<Record> recordsWeekly = new LinkedList<>(); 
//
//    	int currentDay = 1;
//    	for(Record record : findAll()) {
//    		if(dateTime.isBefore(record.getDate().minusDays(5))) {
//    			if(dateTime.isAfter(record.getDate().minusDays(currentDay))) {
//    				recordsDaily.add(record);
//    			} else {
//    				recordsWeekly.add(calculateAverageOfRecords(recordsWeekly));
//    				recordsDaily.clear();
//    				currentDay++;
//    			}
//    		}
//    	}
//    	
//    }
}
