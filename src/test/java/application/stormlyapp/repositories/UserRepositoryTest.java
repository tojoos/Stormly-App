package application.stormlyapp.repositories;

import application.stormlyapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder().login("login1").password("password1").email("sample1@email.com").build());
        userRepository.save(User.builder().login("login2").password("password2").email("sample1@email.com").build());
        userRepository.save(User.builder().login("login3").password("password3").email("sample1@email.com").build());
        userRepository.save(User.builder().login("login9").password("password9").email("sample1@email.com").build());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testSave() {
        //given
        User user1 = User.builder().login("log").password("password").email("email@email.com").build();

        assertEquals(4, userRepository.findAll().spliterator().estimateSize());

        //when
        userRepository.save(user1);

        //then
        assertEquals(5, userRepository.findAll().spliterator().estimateSize());
    }

    @Test
    void testFindAll() {
        //when
        assertEquals(4, userRepository.findAll().spliterator().estimateSize());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindById() {
        //given
        User user1 = User.builder().id(5L).login("log").password("password").email("email@email.com").build();
        userRepository.save(user1);

        //when
        User foundUser = userRepository.findById(5L).orElse(null);

        //then
        assert foundUser != null;
        assertEquals(5L, foundUser.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteById() {
        assertEquals(4, userRepository.findAll().spliterator().estimateSize());
        //when
        userRepository.deleteById(1L);

        //then
        assertEquals(3, userRepository.findAll().spliterator().estimateSize());
    }
}