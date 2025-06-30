package ru.aston.intensive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.intensive.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
