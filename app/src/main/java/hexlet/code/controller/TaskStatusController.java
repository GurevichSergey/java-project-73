package hexlet.code.controller;


import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import jakarta.validation.Valid;
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

    @Operation(summary = "Get all task status")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))))
    @GetMapping()
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusService.getAllTaskStatus();
    }

    @Operation(summary = "Return task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status is found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(responseCode = "404", description = "Task status not found", content = @Content)
    })
    @GetMapping(ID)
    public TaskStatus getTaskStatusById(
            @Parameter(description = "Id of task status to be found")
            @PathVariable final long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @Operation(summary = "Create new task status")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "task status created", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createNewTaskStatus(
            @Parameter(description = "task status data to save")
            @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Change data of task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task status changed", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(responseCode = "404", description = "The task status with this id is not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatusById(
            @Parameter(description = "Id of task status to be changed")
            @PathVariable final long id,
            @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete task status")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "The task status is deleted"))
    @DeleteMapping(ID)
    public void deleteTaskStatusById(
            @Parameter(description = "Id of task status to be changed")
            @PathVariable final long id) {
        taskStatusService.deleteTaskStatusById(id);
    }

}
