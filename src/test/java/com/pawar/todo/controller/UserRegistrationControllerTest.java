package com.pawar.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.register.controller.UserRegistrationController;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.entity.VerificationToken;
import com.pawar.todo.register.exception.UserAlreadyExistException;
import com.pawar.todo.register.service.MailService;
import com.pawar.todo.register.service.UserService;
import com.pawar.todo.register.service.VerificationTokenService;

@ExtendWith(SpringExtension.class)
public class UserRegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserRegistrationController userRegistrationController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController).build();
    }

    @Test
    public void testRegisterUserAccount_Success() throws Exception {
        // Given
        UserDto userDto = new UserDto("rsp777","rasp@gmail.com","pawar"); // Populate with test data
        User registeredUser = new User("rsp777","rasp@gmail.com","pawar", "Ravindra","Singh","Pawar"); // Populate with test data
        VerificationToken token = new VerificationToken(); // Populate with test data
        
        when(userService.registerNewUserAccount(any(UserDto.class), anySet())).thenReturn(registeredUser);
        when(verificationTokenService.createVerificationToken(any(User.class))).thenReturn(token);
        doNothing().when(mailService).sendMail(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/register-service/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(registeredUser.getUsername()));
    }

    @Test
    public void testRegisterUserAccount_UserAlreadyExists() throws Exception {
        // Given
        UserDto userDto = new UserDto(); // Populate with test data

        when(userService.registerNewUserAccount(any(UserDto.class), anySet())).thenThrow(new UserAlreadyExistException("User already exists"));

        // When & Then
        mockMvc.perform(post("/register-service/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isConflict());
    }

    // Helper method to convert an object into JSON string
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

