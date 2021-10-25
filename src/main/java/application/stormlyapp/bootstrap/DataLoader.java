package application.stormlyapp.bootstrap;

import application.stormlyapp.services.RecordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecordService recordService;

    public DataLoader(RecordService recordService) {
        this.recordService = recordService;
    }

    @Override
    public void run(String... args) {
        loadData();
    }

    private void loadData() {
        recordService.fetchData();
    }
}
