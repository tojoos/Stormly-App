package application.stormlyapp.services;

import application.stormlyapp.model.Record;
import application.stormlyapp.model.User;
import application.stormlyapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testSave() {
        //given
        User user1 = User.builder().id(10L).build();

        //when
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User savedUser = userService.save(user1);

        //then
        assertEquals(10L, savedUser.getId());
        verify(userRepository,times(1)).save(any());
    }

    @Test
    void testSaveNull() {
        //when
        when(userRepository.save(any(User.class))).thenReturn(null);

        User savedUser = userService.save(null);

        //then
        assertNull(savedUser);
        verify(userRepository,times(0)).save(any());
    }

    @Test
    void findAll() {
        //given
        Set<User> users = new HashSet<>();
        users.add(User.builder().id(1L).build());
        users.add(User.builder().id(2L).build());
        users.add(User.builder().id(3L).build());

        //when
        when(userRepository.findAll()).thenReturn(users);
        Set<User> usersFound = userService.findAll();

        //then
        assertEquals(3, usersFound.size());
        verify(userRepository,times(1)).findAll();
    }

    @Test
    void findById() {
        //given
        User user1 = User.builder().id(20L).build();
        Optional<User> userOpt = Optional.of(user1);

        //when
        when(userRepository.findById(anyLong())).thenReturn(userOpt);
        User foundUser = userService.findById(1L);

        //then
        assertEquals(20L, foundUser.getId());
        verify(userRepository,times(1)).findById(any());
    }

    @Test
    void findByIdNotExisting() {
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        User foundUser = userService.findById(1L);

        //then
        assertNull(foundUser);
        verify(userRepository,times(1)).findById(any());
    }

    @Test
    void deleteById() {
        HashSet<User> users = new HashSet<>();
        users.add(User.builder().id(1L).build());
        users.add(User.builder().id(2L).build());
        users.add(User.builder().id(3L).build());

        //when
        when(userRepository.findAll()).thenReturn(users);

        //when
        userService.deleteById(1L);

        verify(userRepository,times(1)).findAll();
        verify(userRepository,times(1)).deleteById(any());
    }

    @Test
    void deleteByIdNotExisting() {
        HashSet<User> users = new HashSet<>();

        //when
        when(userRepository.findAll()).thenReturn(users);

        //when
        userService.deleteById(1L);

        verify(userRepository,times(1)).findAll();
        verify(userRepository,times(0)).deleteById(any());
    }
}