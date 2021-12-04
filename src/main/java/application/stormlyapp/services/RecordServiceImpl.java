package application.stormlyapp.services;

import application.stormlyapp.exceptions.NotFoundException;
import application.stormlyapp.model.Record;
import application.stormlyapp.repositories.RecordRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<Record> recordsToAvg = new LinkedList<>();
        List<Record> recordsHourly = new LinkedList<>();

        Set<Record> sortedRecords = findAll().stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        int currentHour = 1;
        for(Record record : sortedRecords) {
            if(record.getDate().isAfter(dateTime.minusHours(5))) {
                if(record.getDate().isAfter(dateTime.minusHours(currentHour))) {
                    recordsToAvg.add(record);
                } else  {
                    if(recordsToAvg.size()>0)
                        recordsHourly.add(calculateAverageOfRecords(recordsToAvg));
                    recordsToAvg.clear();
                    recordsToAvg.add(record);
                    currentHour++;
                }
            }
        }
        if(recordsToAvg.size()>0)
            recordsHourly.add(calculateAverageOfRecords(recordsToAvg));
        recordsHourly.sort(Comparator.comparing(Record::getDate));
        return recordsHourly;
    }

    @Override
    public Record calculateAverageOfRecords(List<Record> records) {
    	double sumTemp = 0;
    	double sumPressure = 0;
    	double sumHumidity = 0;
    	double sumExposure = 0;

    	for(Record record : records) {
    		sumTemp += record.getTemperature();
    		sumPressure += record.getPressure();
    		sumHumidity += record.getHumidity();
    		sumExposure += record.getExposure();
    	}

    	int number = records.size();
        return Record.builder().date(records.get(number/2).getDate()).temperature(sumTemp/number).pressure(sumPressure/number).humidity(sumHumidity/number).exposure(sumExposure/number).build();
    }

    @Override
    public List<Record> findByDateDaily(LocalDateTime dateTime) {
        List<Record> recordsHourly = new LinkedList<>();
        List<Record> recordsDaily = new LinkedList<>();

        Set<Record> sortedRecords = findAll().stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        int currentHour = 1;
        for(Record record : sortedRecords) {
            if(record.getDate().isAfter(dateTime.minusDays(5))) {
                if(record.getDate().isAfter(dateTime.minusDays(currentHour))) {
                    recordsHourly.add(record);
                } else  {
                    if(recordsHourly.size()>0)
                        recordsDaily.add(calculateAverageOfRecords(recordsHourly));
                    recordsHourly.clear();
                    recordsHourly.add(record);
                    currentHour++;
                }
            }
        }
        if(recordsHourly.size()>0)
            recordsDaily.add(calculateAverageOfRecords(recordsHourly));
        recordsDaily.sort(Comparator.comparing(Record::getDate));
        return recordsDaily;
    }

    @Override
    public String getFormattedDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }


}
