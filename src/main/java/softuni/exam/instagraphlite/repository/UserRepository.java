package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);

    @Query("select distinct u from Users u join fetch u.posts p order by size(p)desc, u.id")
    List<Users> findAllByPostsCountDescThanByUserId();
}
