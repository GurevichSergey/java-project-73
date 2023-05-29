package hexlet.code.service;

import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private TaskStatusRepository taskStatusRepository;

    @Override
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus getTaskStatusById(final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Override
    public TaskStatus createNewTaskStatus(final TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = fromDto(taskStatusDto);
        return taskStatusRepository.save(newTaskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(final long id, final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();
        merge(taskStatus, taskStatusDto);
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteTaskStatusById(final long id) {
        taskStatusRepository.deleteById(id);
    }

    private void merge(final TaskStatus taskStatus, final TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = fromDto(taskStatusDto);
        taskStatus.setName(newTaskStatus.getName());
    }

    private TaskStatus fromDto(final TaskStatusDto taskStatusDto) {
        return TaskStatus.builder()
                .name(taskStatusDto.getName())
                .build();
    }
}
