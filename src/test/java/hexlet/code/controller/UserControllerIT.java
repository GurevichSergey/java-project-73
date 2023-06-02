package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.repository.UserRepository;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static hexlet.code.controller.UserController.FULL_USER_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, userRepository.count());
        utils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(
                get(FULL_USER_CONTROLLER_PATH + ID, expectedUser.getId()), expectedUser.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    public void getUserByIdFail() throws Exception {
        utils.regDefaultUser();
        final User user = userRepository.findAll().get(0);
        Exception exception = assertThrows(
                Exception.class, () -> utils.perform(get(FULL_USER_CONTROLLER_PATH + ID, user.getId())));

        assertTrue(exception.getMessage().contains("No value present"));
    }

    @Test
    public void getAllUsers() throws Exception {
        utils.regDefaultUser();
        final var response = utils.perform(get(FULL_USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(users).hasSize(1);
    }


    @Test
    public void twiceRegTheSameUserFail() throws Exception {
        utils.regDefaultUser().andExpect(status().isCreated());
        utils.regDefaultUser().andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void login() throws Exception {
        utils.regDefaultUser();
        final LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = post("/api" + LOGIN)
                .content(TestUtils.asJson(loginDto))
                .contentType(MediaType.APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = post("/api" + LOGIN).content(TestUtils.asJson(loginDto))
                .contentType(MediaType.APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser() throws Exception {
        utils.regDefaultUser();
        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        final var userDto = new UserDto("new Name", "new lastName", TestUtils.TEST_USERNAME_2, "new pass");

        final var updateRequest = put(FULL_USER_CONTROLLER_PATH + ID, userId)
                .content(TestUtils.asJson(userDto))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(updateRequest, TestUtils.TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TestUtils.TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TestUtils.TEST_USERNAME_2).orElse(null));
    }
    @Test
        public void updateUserFail() throws Exception {
        utils.regDefaultUser();
        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        final var userDto = new UserDto("new Name", "new lastName", TestUtils.TEST_USERNAME_2, "new pass");

        final var updateRequest = put(FULL_USER_CONTROLLER_PATH + ID, userId)
                .content(TestUtils.asJson(userDto))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(updateRequest, TestUtils.TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TestUtils.TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TestUtils.TEST_USERNAME_2).orElse(null));
    }

    @Test
    public void deleteUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        utils.perform(delete(FULL_USER_CONTROLLER_PATH + ID, userId), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
        assertNull(userRepository.findByEmail(TestUtils.TEST_USERNAME).orElse(null));
    }

    @Test
    public void deleteUserFails() throws Exception {
        utils.regDefaultUser();
        utils.regUser(new UserDto(
                "fname",
                "lname",
                TestUtils.TEST_USERNAME_2,
                "pwd"
        ));

        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        utils.perform(delete(FULL_USER_CONTROLLER_PATH + ID, userId), TestUtils.TEST_USERNAME_2)
                .andExpect(status().isForbidden());

        assertEquals(2, userRepository.count());
    }
}
