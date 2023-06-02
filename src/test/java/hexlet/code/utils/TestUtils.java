package hexlet.code.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;

import hexlet.code.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static hexlet.code.controller.LabelController.FULL_LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.FULL_TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.FULL_TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.FULL_USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



@Component
public class TestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public static final String TEST_USERNAME = "email@email.com";

    public static final String TEST_USERNAME_2 = "email2@email.com";

    public static final String TEST_TASK_STATUS = "in process";

    public static final String TEST_TASK_STATUS_2 = "new";
    public static final String TEST_LABEL = "Bug";
    public static final String TEST_LABEL_2 = "feature";

    public void tearDown() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    private final UserDto testRegistrationDto = new UserDto(
            "fname",
            "lname",
            TEST_USERNAME,
            "pwd"
    );

    private final TaskStatusDto testTaskStatusDto = new TaskStatusDto(TEST_TASK_STATUS);

    private final LabelDto testLabelDto = new LabelDto(TEST_LABEL);

    private TaskDto testTaskDtoDefault() throws Exception {
        regDefaultUser();
        regDefaultTaskStatus();
        regDefaultLabel();
        final Long authorId = getUserByEmail(testRegistrationDto.getEmail()).getId();
        final Long executorId = getUserByEmail(testRegistrationDto.getEmail()).getId();
        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS).get().getId();
        final Long labelIds = labelRepository.findAll().get(0).getId();

        return new TaskDto(
                "new task",
                "Fix bug",
                executorId,
                taskStatusId,
                List.of(labelIds)
        );
    }


    public TaskStatusDto getTestTaskStatusDto() {
        return testTaskStatusDto;
    }

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public TaskDto getTestTaskDto() throws Exception {
        return testTaskDtoDefault();
    }

    public LabelDto getTestLabelDto() throws Exception {
        return testLabelDto;
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regDefaultTaskStatus() throws Exception {
        return regStatus(testTaskStatusDto);
    }

    public ResultActions regDefaultTask() throws Exception {
        return regTask(testTaskDtoDefault());
    }

    public ResultActions regDefaultLabel() throws Exception {
        return regLabel(testLabelDto);
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        final var request = post(FULL_USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions regStatus(final TaskStatusDto taskStatusDto) throws Exception {
        final var request = post(FULL_TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions regLabel(final LabelDto labelDto) throws Exception {
        final var request = post(FULL_LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions regTask(final TaskDto taskDto) throws Exception {
        final var request = post(FULL_TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException  {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> typeReference)
            throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }
}
