package application.stormlyapp.bootstrap;

import application.stormlyapp.model.Record;
import application.stormlyapp.model.User;
import application.stormlyapp.services.RecordService;
import application.stormlyapp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final RecordService recordService;

    private final UserService userService;

    public DataLoader(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        recordService.fetchData();
        loadData();
        addDefaultUsers();
        log.debug("Loading bootstrap data");
    }

    private void loadData() {
        //adding custom updated data for hourly
        recordService.save(Record.builder().temperature(1).humidity(0.12).pressure(1002).exposure(0.41).date(LocalDateTime.now()).build());
        recordService.save(Record.builder().temperature(0).humidity(0.18).pressure(1002).exposure(0.40).date(LocalDateTime.now().minusHours(1)).build());
        recordService.save(Record.builder().temperature(-2).humidity(0.26).pressure(1011).exposure(0.42).date(LocalDateTime.now().minusHours(2)).build());
        recordService.save(Record.builder().temperature(-2).humidity(0.32).pressure(1022).exposure(0.31).date(LocalDateTime.now().minusHours(3)).build());
        recordService.save(Record.builder().temperature(-5).humidity(0.41).pressure(1022).exposure(0.20).date(LocalDateTime.now().minusHours(4)).build());

        recordService.save(Record.builder().temperature(1).humidity(0.66).pressure(999).exposure(0.60).date(LocalDateTime.now().minusDays(1)).build());
        recordService.save(Record.builder().temperature(-3).humidity(0.53).pressure(1021).exposure(0.60).date(LocalDateTime.now().minusHours(1).minusDays(1)).build());

        recordService.save(Record.builder().temperature(0).humidity(0.26).pressure(1011).exposure(0.52).date(LocalDateTime.now().minusHours(2).minusDays(2)).build());
        recordService.save(Record.builder().temperature(-2).humidity(0.32).pressure(1022).exposure(0.41).date(LocalDateTime.now().minusHours(3).minusDays(2)).build());

        recordService.save(Record.builder().temperature(5).humidity(0.23).pressure(1011).exposure(0.30).date(LocalDateTime.now().minusHours(4).minusDays(3)).build());
        recordService.save(Record.builder().temperature(2).humidity(0.45).pressure(1002).exposure(0.40).date(LocalDateTime.now().minusDays(3)).build());
        recordService.save(Record.builder().temperature(0).humidity(0.32).pressure(1000).exposure(0.60).date(LocalDateTime.now().minusHours(1).minusDays(3)).build());
        recordService.save(Record.builder().temperature(-2).humidity(0.33).pressure(1010).exposure(0.52).date(LocalDateTime.now().minusHours(2).minusDays(3)).build());

        recordService.save(Record.builder().temperature(2).humidity(0.22).pressure(1012).exposure(0.55).date(LocalDateTime.now().minusHours(3).minusDays(4)).build());
        recordService.save(Record.builder().temperature(1).humidity(0.33).pressure(1006).exposure(0.33).date(LocalDateTime.now().minusHours(4).minusDays(4)).build());
    }

    private void addDefaultUsers() {
        User admin = User.builder().id(1L).email("admin@email.com").login("admin").password("password").build();
        User user = User.builder().id(2L).email("user@email.com").login("user").password("password").build();
        userService.save(admin);
        userService.save(user);
    }
}
