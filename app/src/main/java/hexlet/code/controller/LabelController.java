package hexlet.code.controller;


import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String FULL_LABEL_CONTROLLER_PATH = "/api/labels";

    public static final String ID = "/{id}";

    private final LabelService labelService;

    @GetMapping
    public List<Label> getAllLabel() {
        return labelService.getAllLabel();
    }

    @GetMapping(ID)
    public Label getLabelById(@PathVariable final long id) {
        return labelService.getLabelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @PutMapping(ID)
    public Label updateLabel(@PathVariable final long id,
                             @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabelById(id, labelDto);
    }

    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final long id) {
        labelService.deleteLabelById(id);
    }
}
