package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;



import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.FULL_LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTask() throws Exception {
        assertEquals(0, labelRepository.count());
        utils.regDefaultLabel().andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void twiceRegTheSameLabelFail() throws Exception {
        utils.regDefaultLabel().andExpect(status().isCreated());
        utils.regDefaultLabel().andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getLabelById() throws Exception {
        utils.regDefaultLabel();
        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = utils.perform(get(FULL_LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()), TEST_USERNAME)
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    public void getAllLabel() throws Exception {
        utils.regDefaultLabel();

        final  var response = utils.perform(get(FULL_LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labelList = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(1, labelList.size());
    }

    @Test
    public void updateLabel() throws Exception {
        utils.regDefaultLabel();

        final Label label = labelRepository.findAll().get(0);
        final LabelDto labelDto = new LabelDto("task");

        final var request = put(FULL_LABEL_CONTROLLER_PATH + ID, label.getId())
                .content(asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label updatedLabel = labelRepository.findAll().get(0);

        assertEquals(updatedLabel.getName(), "task");
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultLabel();
        assertEquals(1, labelRepository.count());
        final Long labelId = labelRepository.findAll().get(0).getId();

        utils.perform(delete(FULL_LABEL_CONTROLLER_PATH + ID, labelId), TEST_USERNAME)
                        .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteLabelFail() throws Exception {
        utils.regDefaultLabel();
        assertEquals(1, labelRepository.count());
        final Long labelId = labelRepository.findAll().get(0).getId();

        Exception exception = assertThrows(Exception.class,
                () ->  utils.perform(delete(FULL_LABEL_CONTROLLER_PATH + ID, labelId)));

        assertThat(exception.getMessage()).contains("No value present");
    }
}
