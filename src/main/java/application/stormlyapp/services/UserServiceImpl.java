package application.stormlyapp.services;

import application.stormlyapp.model.Record;
import application.stormlyapp.model.User;
import application.stormlyapp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    public UserServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public User save(User user) {
        if(user != null) {
            log.debug("New user added successfully");
            return userRepository.save(user);
        } else {
            log.debug("Couldn't add user, null value");
            return null;
        }
    }

    @Override
    public Set<User> findAll() {
        Set<User> users = new HashSet<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        User userFound = findAll().stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
        if(userFound != null) {
            log.debug("User id: " + id + " deleted successfully.");
            userRepository.deleteById(id);
        } else {
            log.debug("Couldn't delete id: " + id + ". User doesn't exist in database");
        }
    }

}
