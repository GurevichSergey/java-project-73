package hexlet.code.controller;


import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String FULL_LABEL_CONTROLLER_PATH = "/api/labels";

    public static final String ID = "/{id}";

    private final LabelService labelService;


    @Operation(summary = "Get all label")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))))
    @GetMapping
    public List<Label> getAllLabel() {
        return labelService.getAllLabel();
    }

    @Operation(summary = "Return label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "Label not found", content = @Content)
    })
    @GetMapping(ID)
    public Label getLabelById(
            @Parameter(description = "Id of label to be found")
            @PathVariable final long id) {
        return labelService.getLabelById(id);
    }


    @Operation(summary = "Create new label")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "label created", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(
            @Parameter(description = "label data to save")
            @RequestBody @Valid final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Change data of label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "label changed", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "The label with this id is not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)
    })
    @PutMapping(ID)
    public Label updateLabel(
            @Parameter(description = "Id of label data to be changed")
            @PathVariable final long id,
            @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabelById(id, labelDto);
    }

    @Operation(summary = "Delete label")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "The label is deleted"))
    @DeleteMapping(ID)
    public void deleteLabel(
            @Parameter(description = "Id of label to be deleted")
            @PathVariable final long id) {
        labelService.deleteLabelById(id);
    }
}
