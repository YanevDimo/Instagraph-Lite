package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.Pictures;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Pictures, Integer> {
    boolean existsByPath(String path);

    Optional<Pictures> findByPath(String path);

    List<Pictures>findAllBySizeGreaterThanOrderBySize(double size);
}
