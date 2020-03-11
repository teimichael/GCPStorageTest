package stu.napls.nabootweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stu.napls.nabootweb.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUuid(String uuid);
}
