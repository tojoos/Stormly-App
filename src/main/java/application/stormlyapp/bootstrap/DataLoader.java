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
        loadData();
        addDefaultUsers();
        log.debug("Loading bootstrap data");
    }

    private void loadData() {
        recordService.fetchData();

        //adding custom updated data for hourly
        recordService.save(Record.builder().temperature(19).humidity(0.12).pressure(1002).exposure(800).date(LocalDateTime.now()).build());
        recordService.save(Record.builder().temperature(18).humidity(0.18).pressure(1002).exposure(600).date(LocalDateTime.now().minusHours(1)).build());
        recordService.save(Record.builder().temperature(18.5).humidity(0.26).pressure(1011).exposure(520).date(LocalDateTime.now().minusHours(2)).build());
        recordService.save(Record.builder().temperature(17).humidity(0.32).pressure(1022).exposure(410).date(LocalDateTime.now().minusHours(3)).build());
        recordService.save(Record.builder().temperature(15).humidity(0.41).pressure(1022).exposure(300).date(LocalDateTime.now().minusHours(4)).build());


    }

    private void addDefaultUsers() {
        User admin = User.builder().id(1L).email("admin@email.com").login("admin").password("password").build();
        User user = User.builder().id(2L).email("user@email.com").login("user").password("password").build();
        userService.save(admin);
        userService.save(user);
    }
}
