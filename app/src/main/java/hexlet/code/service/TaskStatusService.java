package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    List<TaskStatus> getAllTaskStatus();

    TaskStatus getTaskStatusById(long id);

    void deleteTaskStatusById(long id);

    TaskStatus createNewTaskStatus(TaskStatusDto task);

    TaskStatus updateTaskStatus(long id, TaskStatusDto task);

}
