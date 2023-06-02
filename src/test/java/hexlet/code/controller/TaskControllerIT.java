package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
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

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.FULL_TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTask() throws Exception {
        assertEquals(0, taskRepository.count());
        utils.regDefaultTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());
    }

    @Test
    public void twiceRegTheSameTaskFail() throws Exception {
        utils.regDefaultTask().andExpect(status().isCreated());
        utils.regDefaultTask().andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getTaskById() throws Exception {
        utils.regDefaultTask();
        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = utils.perform(get(FULL_TASK_CONTROLLER_PATH + ID, expectedTask.getId()),
                TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getAuthor().getEmail(), TEST_USERNAME);
        assertEquals(expectedTask.getTaskStatus().getName(), "in process");
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(task.getName(), "new task");
    }

    @Test
    public void getAllTask() throws Exception {
        utils.regDefaultTask();
        final var response = utils.perform(get(FULL_TASK_CONTROLLER_PATH),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(task).hasSize(1);
    }

    @Test
    public void getTaskByFilter() throws Exception {
        utils.regDefaultTask();
        final Long taskStatusId = taskRepository.findAll().get(0).getTaskStatus().getId();
        final Long labelsId = taskRepository.findAll().get(0).getLabels().get(0).getId();
        final var response = utils.perform(get(
                FULL_TASK_CONTROLLER_PATH + "?taskStatus=" + taskStatusId + "&labelsId=" + labelsId),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(task).hasSize(1);
        assertThat(taskStatusId).isEqualTo(task.get(0).getTaskStatus().getId());
        assertThat(labelsId).isEqualTo(task.get(0).getLabels().get(0).getId());
    }

    @Test
    public void getTaskByFilterFail() throws Exception {
        utils.regDefaultTask();
        final Long taskStatusId = taskRepository.findAll().get(0).getTaskStatus().getId();
        final var response = utils.perform(get(FULL_TASK_CONTROLLER_PATH + "?taskStatus=101"),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(task).isEmpty();
    }


    @Test
    public void updateTask() throws Exception {
        utils.regDefaultTask();
        final Task task = taskRepository.findAll().get(0);
        final List<Long> labelsIds = task.getLabels().stream()
                .map(Label::getId)
                .toList();

        final var newTaskDto = new TaskDto(
                "really new task",
                "chillout",
                task.getExecutor().getId(),
                task.getTaskStatus().getId(),
                labelsIds
        );

        final var request = put(FULL_TASK_CONTROLLER_PATH + ID, task.getId())
                .content(TestUtils.asJson(newTaskDto))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task updatedTask = taskRepository.findAll().get(0);

        assertEquals(1, taskRepository.count());
        assertEquals(updatedTask.getName(), "really new task");
        assertEquals(updatedTask.getDescription(), "chillout");
        assertEquals(updatedTask.getLabels().get(0).getName(), "Bug");
    }

    @Test
    public void updateTask2() throws Exception {
        utils.regDefaultTask();

        final Task task = taskRepository.findAll().get(0);

        final var newTaskDto = new TaskDto(
                "really new task",
                null,
                null,
                task.getTaskStatus().getId(),
                null
        );

        final var request = put(FULL_TASK_CONTROLLER_PATH + ID, task.getId())
                .content(TestUtils.asJson(newTaskDto))
                .contentType(MediaType.APPLICATION_JSON);

        var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().is(200))
                .andReturn()
                .getResponse();

        final Task updatedTask = taskRepository.findAll().get(0);

        assertNull(updatedTask.getExecutor());
        assertNull(updatedTask.getDescription());
        assertThat(updatedTask.getLabels()).isEmpty();
    }

    @Test
    public void deleteTask() throws Exception {
        utils.regDefaultTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());
        final Task task = taskRepository.findAll().get(0);

        utils.perform(delete(FULL_TASK_CONTROLLER_PATH + ID, task.getId()), TEST_USERNAME)
                        .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }
}
