package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.DTO.PostSeedRootDto;
import softuni.exam.instagraphlite.models.entity.Posts;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PostServiceImpl implements PostService {

    private static final String POST_FILE_PATH = "src/main/resources/files/posts.xml";

    private final PostRepository postRepository;
    private final UserService userService;
    private final PictureService pictureService;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public PostServiceImpl(PostRepository postRepository, UserService userService,
                           PictureService pictureService,
                           ModelMapper modelMapper, ValidationUtil validationUtil,
                           XmlParser xmlParser) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.pictureService = pictureService;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POST_FILE_PATH));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
            //за дебъг
     //  PostSeedRootDto postSeedRootDto = xmlParser.fromFile(POST_FILE_PATH, PostSeedRootDto.class);

        xmlParser.fromFile(POST_FILE_PATH, PostSeedRootDto.class)
                .getPost()
                .stream()
                .filter(postSeedDto -> {
                    boolean isValid = validationUtil.isValid(postSeedDto)
                            && userService.isEntityExist(postSeedDto.getUser().getName())
                            && pictureService.isEntityExist(postSeedDto.getPicture().getPath());

                    builder.append(isValid
                            ? "Successfully import post, made by "+postSeedDto.getUser().getName()
                            : "Invalid post").append(System.lineSeparator());

                    return isValid;
                })
                .map(postSeedDto -> {
                    Posts posts = modelMapper.map(postSeedDto,Posts.class);
                    posts.setUser(userService.findByUsername(postSeedDto.getUser().getName()));
                    posts.setPictures(pictureService.findByPath(postSeedDto.getPicture().getPath()));
                    return posts;
                })
                .forEach(postRepository::save);
        return builder.toString();
    }
}
