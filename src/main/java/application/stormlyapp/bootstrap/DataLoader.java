package application.stormlyapp.bootstrap;

import application.stormlyapp.model.User;
import application.stormlyapp.services.RecordService;
import application.stormlyapp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    }

    private void addDefaultUsers() {
        User admin = User.builder().id(1L).email("admin@email.com").login("admin").password("password").build();
        User user = User.builder().id(2L).email("user@email.com").login("user").password("password").build();
        userService.save(admin);
        userService.save(user);
    }
}
