package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTask();

    Task getTaskById(long id);

    Task createTask(TaskDto taskDto);

    Task updateTaskById(long id, TaskDto taskDto);

    void deleteTaskById(long id);

}
