package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private LabelRepository labelRepository;

    @Override
    public List<Label> getAllLabel() {
        return labelRepository.findAll();
    }

    @Override
    public Label getLabelById(final long id) {
        return labelRepository.findById(id).get();
    }

    @Override
    public Label createLabel(final LabelDto labelDto) {
        final Label newLabel = new Label();
        newLabel.setName(labelDto.getName());
        return labelRepository.save(newLabel);
    }

    @Override
    public Label updateLabelById(final long id, final LabelDto labelDto) {
        final Label updateLabel = labelRepository.findById(id).get();
        updateLabel.setName(labelDto.getName());
        return labelRepository.save(updateLabel);
    }

    public void deleteLabelById(final long id) {
        labelRepository.deleteById(id);
    }
}
