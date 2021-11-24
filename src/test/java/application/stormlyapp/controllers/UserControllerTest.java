package application.stormlyapp.controllers;

import application.stormlyapp.model.User;
import application.stormlyapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    UserService userService;

    UserController userController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void showLoginForm() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/loginForm"));

        verifyNoInteractions(userService);
    }

    @Test
    void showRegisterForm() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerForm"));

        verifyNoInteractions(userService);
    }

    @Test
    void processValidMatchingPassRegisterForm() throws Exception {
        User user = User.builder().login("sampleLogin").email("sample@email.com").password("samplePassword").confirmedPassword("samplePassword").build();

        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "sampleLogin")
                        .param("password", "samplePassword")
                        .param("confirmedPassword", "samplePassword")
                        .param("email", "sample@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/login"));

        verify(userService,times(1)).save(any());
    }

    @Test
    void processValidNotMatchingPassRegisterForm() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "sampleLogin")
                        .param("password", "samplePassword")
                        .param("confirmedPassword", "otherPassword")
                        .param("email", "sample@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerForm"));

        verifyNoInteractions(userService);
    }

    @Test
    void processInvalid_Blank_RegisterForm() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "      ")
                        .param("password", "samplePassword")
                        .param("confirmedPassword", "samplePassword")
                        .param("email", "sample@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerForm"));

        verifyNoInteractions(userService);
    }

    @Test
    void processInvalid_tooShort_RegisterForm() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "login")
                        .param("password", "11")
                        .param("confirmedPassword", "11")
                        .param("email", "sample@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerForm"));

        verifyNoInteractions(userService);
    }

    @Test
    void processInvalid_wrongEmailFormat_RegisterForm() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "login")
                        .param("password", "password")
                        .param("confirmedPassword", "password")
                        .param("email", "sample@@@@@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerForm"));

        verifyNoInteractions(userService);
    }


    @Test
    void processCorrectLoginForm() throws Exception {

        when(userService.isUserValid(any(),any())).thenReturn(true);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "user")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/control-panel"));
    }

    @Test
    void processIncorrectLoginForm() throws Exception {
        when(userService.isUserValid(any(),any())).thenReturn(false);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("login", "user")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/loginForm"));
    }

    @Test
    void showControlPanel() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptySet());

        mockMvc.perform(get("/control-panel"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/control-panelPage"))
                .andExpect(model().attributeExists("users"));

        verify(userService,times(1)).findAll();
    }
}