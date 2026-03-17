package dev.rvg.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;
import dev.rvg.usermanagement.exception.GlobalExceptionHandler;
import dev.rvg.usermanagement.exception.ResourceNotFoundException;
import dev.rvg.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;
    @InjectMocks private UserController userController;

    private static final String BASE_URL = "/api/v1/users";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUser_shouldReturn201_withCreatedUser() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("John", "Doe", "John@gmail.com", "password123");
        UserDto savedUser = new UserDto(1L, "John", "Doe", "John@gmail.com");

        when(userService.createUser(createUserDto)).thenReturn(savedUser);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("John@gmail.com"));
    }

    @Test
    void getUserById_shouldReturn200_whenUserExists() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "Doe", "John@gmail.com");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserById_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User not found with id 99"));

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturn200_withUserList() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "Doe", "John@gmail.com"),
                new UserDto(2L, "Juan", "Perez", "juan@gmail.com")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_shouldReturn200_withUpdatedUser() throws Exception {
        UserDto updateDto = new UserDto(1L, "John", "Lopez", "John@gmail.com");

        when(userService.updateUser(1L, updateDto)).thenReturn(updateDto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Lopez"));
    }

    @Test
    void updateUser_shouldReturn404_whenUserNotFound() throws Exception {
        UserDto updateDto = new UserDto(99L, "John", "Lopez", "John@gmail.com");

        when(userService.updateUser(99L, updateDto))
                .thenThrow(new ResourceNotFoundException("User not found with id 99"));

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturn204_whenUserExists() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}