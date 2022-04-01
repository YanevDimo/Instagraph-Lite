package softuni.exam.instagraphlite.service;

import softuni.exam.instagraphlite.models.entity.Pictures;

import java.io.IOException;

public interface PictureService {
    boolean areImported();
    String readFromFileContent() throws IOException;
    String importPictures() throws IOException;

    boolean isEntityExist(String path);

    String exportPictures();

    Pictures findByPath(String profilePicture);

}
