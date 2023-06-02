package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.utils.TestUtils.TEST_TASK_STATUS_2;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;


import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static hexlet.code.controller.TaskStatusController.FULL_TASK_STATUS_CONTROLLER_PATH;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @BeforeEach
    public void clear2() {
        utils.tearDown();
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        utils.regDefaultTaskStatus().andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void twiceRegTheSameTaskStatusFail() throws Exception {
        utils.regDefaultTaskStatus().andExpect(status().isCreated());
        utils.regDefaultTaskStatus().andExpect(status().isUnprocessableEntity());
    }


    @Test
    public void getTaskStatusById() throws Exception {
        utils.regDefaultTaskStatus();
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.perform(get(FULL_TASK_STATUS_CONTROLLER_PATH + ID,
                expectedTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
        assertEquals(expectedTaskStatus.getName(), "in process");
    }

    @Test
    public void getAllTaskStatus() throws  Exception {
        utils.regDefaultTaskStatus();

        final var response = utils.perform(get(FULL_TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        utils.regDefaultTaskStatus();

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final var updateRequest = put(FULL_TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId())
                .content(asJson(new TaskStatusDto(TEST_TASK_STATUS_2)))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatusUpdated = taskStatusRepository.findAll().get(0);

        assertEquals(taskStatusRepository.count(), 1);
        assertEquals(taskStatusUpdated.getName(), TEST_TASK_STATUS_2);
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        utils.regDefaultTaskStatus();
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        assertEquals(taskStatusRepository.count(), 1);

        utils.perform(delete(FULL_TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(taskStatusRepository.count(), 0);
    }
}
