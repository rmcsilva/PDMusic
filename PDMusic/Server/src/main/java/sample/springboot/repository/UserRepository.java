package sample.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.springboot.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByUsername(String username);
    User findByUsername(String username);
}
