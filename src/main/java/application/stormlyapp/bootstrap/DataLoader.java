package application.stormlyapp.bootstrap;

import application.stormlyapp.services.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final RecordService recordService;

    public DataLoader(RecordService recordService) {
        this.recordService = recordService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadData();
        log.debug("Loading bootstrap data");
    }

    private void loadData() {
        recordService.fetchData();
    }
}
