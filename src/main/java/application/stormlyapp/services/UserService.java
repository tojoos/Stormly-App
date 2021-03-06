package application.stormlyapp.services;

import application.stormlyapp.model.User;

import java.util.Set;

public interface UserService {
    User save(User user);
    Set<User> findAll();
    User findById(Long id);
    void deleteById(Long id);
    User findByLogin(String login);
    boolean isUserValid(String login, String password);
}
