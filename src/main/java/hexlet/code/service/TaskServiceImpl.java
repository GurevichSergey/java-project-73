package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;

    private UserRepository userRepository;

    private TaskStatusRepository taskStatusRepository;

    private LabelRepository labelRepository;

    private UserService userService;

    @Override
    public List<Task> getAllTask(Predicate predicate) {
        return StreamSupport
                .stream(taskRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(final long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTaskById(final long id, final TaskDto taskDto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(final long id) {
        taskRepository.deleteById(id);
    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setAuthor(newTask.getAuthor());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
    }

    private Task fromDto(final TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        final User executor = taskDto.getExecutorId() == null ? null
                : userRepository.findById(taskDto.getExecutorId()).get();
        final List<Label> labels = taskDto.getLabelsIds() == null ? null
                : labelRepository.findAllById(taskDto.getLabelsIds());
        final TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).get();

        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .author(author)
                .executor(executor)
                .taskStatus(taskStatus)
                .labels(labels)
                .build();
    }
}
