package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.DTO.PictureSeedDto;
import softuni.exam.instagraphlite.models.entity.Pictures;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PictureServiceImpl implements PictureService {

    private static final String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";

    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public PictureServiceImpl(PictureRepository pictureRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {

        StringBuilder builder = new StringBuilder();

        // за дебъг !!!
        // PictureSeedDto[] pictureSeedDtos = gson.fromJson(readFromFileContent(), PictureSeedDto[].class);

        Arrays.stream(gson.fromJson(readFromFileContent(), PictureSeedDto[].class))
                .filter(pictureSeedDto -> {
                    boolean isValid = validationUtil.isValid(pictureSeedDto)
                            && !isEntityExist(pictureSeedDto.getPath());
                    builder.append(isValid
                            ? String.format("Successfully imported pictures, with %.2f", pictureSeedDto.getSize())
                            : "Invalid picture").append(System.lineSeparator());
                    return isValid;
                })
                .map(pictureSeedDto -> modelMapper.map(pictureSeedDto, Pictures.class))
                .forEach(pictureRepository::save);
        return builder.toString();
    }

    @Override
    public boolean isEntityExist(String path) {
        return pictureRepository.existsByPath(path);
    }

    @Override
    public String exportPictures() {
        StringBuilder builder = new StringBuilder();

        pictureRepository.findAllBySizeGreaterThanOrderBySize(30000D)
                .forEach(pictures -> {
                    builder.append(String.format("%.2f - %s", pictures.getSize(), pictures.getPath()))
                            .append(System.lineSeparator());
                });

        return builder.toString();
    }

    @Override
    public Pictures findByPath(String path) {
        return pictureRepository.findByPath(path).orElse(null);
    }
}
