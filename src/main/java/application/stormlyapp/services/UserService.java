package application.stormlyapp.services;

import application.stormlyapp.model.User;

import java.util.Set;

public interface UserService {
    User save(User record);
    Set<User> findAll();
    User findById(Long id);
    void deleteById(Long id);
}
