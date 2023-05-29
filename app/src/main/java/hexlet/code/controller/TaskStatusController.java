package hexlet.code.controller;


import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;


@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    public static final String FULL_TASK_STATUS_CONTROLLER_PATH = "/api/statuses";

    public static final String ID = "/{id}";


    private final TaskStatusService taskStatusService;

    @GetMapping()
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusService.getAllTaskStatus();
    }

    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable final long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createNewTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    @PutMapping(ID)
    public TaskStatus updateTaskStatusById(@PathVariable final long id,
                                           @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @DeleteMapping(ID)
    public void deleteTaskStatusById(@PathVariable final long id) {
        taskStatusService.deleteTaskStatusById(id);
    }

}
