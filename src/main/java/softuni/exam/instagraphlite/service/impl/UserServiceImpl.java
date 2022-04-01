package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.DTO.UserSeedDto;
import softuni.exam.instagraphlite.models.entity.Users;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private static final String USER_FILE_PATH = "src/main/resources/files/users.json";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;
    private final Gson gson;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           ValidationUtil validationUtil, PictureService pictureService, Gson gson) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USER_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {

        StringBuilder builder = new StringBuilder();

        //за дебъг!!!
        // UserSeedDto[] userSeedDtos = gson.fromJson(readFromFileContent(), UserSeedDto[].class);

        Arrays.stream(gson.fromJson(readFromFileContent(), UserSeedDto[].class))
                .filter(userSeedDto -> {
                    boolean isValid = validationUtil.isValid(userSeedDto)
                            && !isEntityExist(userSeedDto.getUsername())
                            && pictureService.isEntityExist(userSeedDto.getProfilePicture());
                    builder.append(isValid
                            ? "Successfully imported User: " + userSeedDto.getUsername()
                            : "Invalid user").append(System.lineSeparator());

                    return isValid;
                })
                .map(userSeedDto -> {
                    Users users = modelMapper.map(userSeedDto, Users.class);
                    users.setProfilePictures(pictureService.findByPath(userSeedDto.getProfilePicture()));
                    return users;
                })
                .forEach(userRepository::save);

        return builder.toString();
    }

    @Override
    public boolean isEntityExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public String exportUsersWithTheirPosts() {
        StringBuilder builder = new StringBuilder();
        userRepository.findAllByPostsCountDescThanByUserId()
                .forEach(users -> {
                    builder.append(String.format("""
                            User: %s
                            Post count: %d
                            """,users.getUsername(),users.getPosts().size()));

                    users.getPosts()
                            .forEach(posts -> {
                                builder.append(String.format("""
                                        "==Post Details:"
                                        "----Caption: %s"
                                        "----Picture Size:%.2f"
                                        """,posts.getCaption(),posts.getPictures().getSize()));
                            });
                });
        return builder.toString();
    }



    @Override
    public Users findByUsername(String username) {
        return userRepository
                .findByUsername(username).orElse(null);
    }
}
