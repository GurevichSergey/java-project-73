package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String name;

    private String description;

    private Long executorId;

    @NotNull
    private Long taskStatusId;

    private List<Long> labelIds;
}
