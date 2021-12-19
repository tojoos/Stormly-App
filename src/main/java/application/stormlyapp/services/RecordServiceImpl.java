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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordServiceImpl implements RecordService {

    private final String FILE_URL = "src\\main\\resources\\data.txt";

    private final RecordRepository recordRepository;

    private final double EXPOSURE_LIMIT_AT_NIGHT = 0.15;
    private final double EXPOSURE_LIMIT_AT_NIGHT_CLOUDY = 0.10;
    private final double EXPOSURE_LIMIT_AT_DAY_CLOUDY = 0.30;
    private final double EXPOSURE_LIMIT_AT_DAY_PARTLY_CLOUDY = 0.50;
    private final double HUMIDITY_LIMIT_RAINY = 0.70;
    private final double TEMPERATURE_LIMIT_SNOWY = 3.0;

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
        HashSet<Record> records = new LinkedHashSet<>();
        recordRepository.findAll().forEach(records::add);

        return records.stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
        Set<Record> matchingRecords = new LinkedHashSet<>();
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
                    humidity = Double.parseDouble(strings[1]);
                    temperature = Double.parseDouble(strings[2]);
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
        for(Record record : findAll()) {
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

        int currentHour = 1;
        for(Record record : findAll()) {
            if(record.getDate().isAfter(dateTime.minusHours(5))) {
                if(record.getDate().isAfter(dateTime.minusHours(currentHour))) {
                    recordsToAvg.add(record);
                } else  {
                    if(recordsToAvg.size()>0
                            && recordsToAvg.get(recordsToAvg.size()-1).getDate().isAfter(dateTime.minusHours(currentHour))) {
                        recordsHourly.add(calculateAverageOfRecords(recordsToAvg));
                        recordsToAvg.clear();
                    } else {
                        recordsHourly.add(Record.builder().date(dateTime.minusHours(currentHour)).temperature(-50).build());
                    }
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
        return Record.builder().date(records.get(0).getDate()).temperature(sumTemp/number).pressure(sumPressure/number).humidity(sumHumidity/number).exposure(sumExposure/number).build();
    }

    @Override
    public List<Record> findByDateDaily(LocalDateTime dateTime) {
        List<Record> recordsHourly = new LinkedList<>();
        List<Record> recordsDaily = new LinkedList<>();

        int currentDays = 1;
        for(Record record : findAll()) {
            if(record.getDate().isAfter(dateTime.minusDays(5))) {
                if(record.getDate().isAfter(dateTime.minusDays(currentDays))) {
                    recordsHourly.add(record);
                } else  {
                    if(recordsHourly.size()>0
                            && recordsHourly.get(recordsHourly.size()-1).getDate().isAfter(dateTime.minusDays(currentDays))) {
                        recordsDaily.add(calculateAverageOfRecords(recordsHourly));
                        recordsHourly.clear();
                    } else {
                        recordsDaily.add(Record.builder().date(dateTime.minusDays(currentDays)).temperature(-50).build());
                    }
                    recordsHourly.add(record);
                    currentDays++;
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

    @Override
    public String getIconBasedOnRecord(Record record) {
        if(record.getTemperature() == -50)
            return "mdi mdi-help-circle-outline";

        if(record.getDate().getHour() <= 5 && record.getDate().getHour() >= 22 ) {
            if(record.getExposure() < EXPOSURE_LIMIT_AT_NIGHT_CLOUDY) {
                return "mdi mdi-weather-night-partly-cloudy";
            } else {
                return "mdi mdi-weather-night";
            }
        } else {
            if(record.getExposure() < EXPOSURE_LIMIT_AT_DAY_PARTLY_CLOUDY) {
                if(record.getHumidity() > HUMIDITY_LIMIT_RAINY) {
                    if(record.getTemperature() < TEMPERATURE_LIMIT_SNOWY) {
                        return "mdi mdi-weather-snowy-heavy";
                    } else {
                        return "mdi mdi-weather-rainy";
                    }
                } else {
                    if(record.getExposure() < EXPOSURE_LIMIT_AT_DAY_CLOUDY) {
                        return "mdi mdi-weather-cloudy";
                    } else {
                        return "mdi mdi-weather-partly-cloudy";
                    }
                }
            } else {
                if(record.getHumidity() > HUMIDITY_LIMIT_RAINY) {
                    if(record.getTemperature() < TEMPERATURE_LIMIT_SNOWY) {
                        return "mdi mdi-weather-snowy-heavy";
                    } else {
                        return "mdi mdi-weather-rainy";
                    }
                } else {
                    return "mdi mdi-weather-sunny";
                }
            }
        }
    }

    @Override
    public List<Record> findAllForGivenDay(LocalDateTime date) {
        List<Record> recordsOfGivenDay = new LinkedList<>();
        for(Record record : findAll()) {
            if(record.getDate().getDayOfYear() == date.getDayOfYear() &&
               record.getDate().getYear() == date.getYear()) {
                recordsOfGivenDay.add(record);
            }
        }
        if(recordsOfGivenDay.size() != 0)
            Collections.reverse(recordsOfGivenDay);
        return recordsOfGivenDay.size() == 0 ? null : recordsOfGivenDay;
    }

    @Override
    public String calculateSunrise(LocalDateTime date) {
        List<Record> records = findAllForGivenDay(date);
        if(records != null) {
            for(Record record : records) {
                if (record.getDate().getHour() < 9 && record.getDate().getHour() > 4) {
                    if (record.getExposure() >= EXPOSURE_LIMIT_AT_NIGHT) {
                        return record.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String calculateSunset(LocalDateTime date) {
        List<Record> records = findAllForGivenDay(date);
        if(records != null) {
            for(Record record : records) {
                if (record.getDate().getHour() > 14 && record.getDate().getHour() < 23) {
                    if (record.getExposure() <= EXPOSURE_LIMIT_AT_NIGHT) {
                        return record.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
                    }
                }
            }
        }
        return null;
    }
}
