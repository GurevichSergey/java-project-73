package hexlet.code.controller;


import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    public static final String FULL_TASK_CONTROLLER_PATH = "/api/tasks";

    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private TaskService taskService;

    @Operation(summary = "Get all task")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))))
    @GetMapping
    public List<Task> getAllTask(@QuerydslPredicate(root = Task.class) Predicate predicate) {
       return taskService.getAllTask(predicate);
    }

    @Operation(summary = "Return task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task is found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping(ID)
    public Task getTaskById(
            @Parameter(description = "Id of task to be found")
            @PathVariable final long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Create new task")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "task created", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(
            @Parameter(description = "task data to save")
            @RequestBody @Valid final TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }
    @Operation(summary = "Change data of task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task changed", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "The task with this id is not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)
    })
    @PutMapping(ID)
    public Task updateTask(
            @Parameter(description = "Id of task data to be changed")
            @PathVariable final long id,
            @RequestBody @Valid final TaskDto taskDto) {
        return taskService.updateTaskById(id, taskDto);
    }

    @Operation(summary = "Delete task")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "The task is deleted"))
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteTask(
            @Parameter(description = "Id of task to be deleted")
            @PathVariable long id) {
        taskService.deleteTaskById(id);
    }
}
