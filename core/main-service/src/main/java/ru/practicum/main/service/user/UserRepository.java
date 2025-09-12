package ru.practicum.main.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.service.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u " +
            " FROM User u WHERE (:ids is NULL OR u.id IN :ids)")
    Page<User> findUsersByIds(List<Long> ids, Pageable pageable);

    Optional<User> findByEmailIgnoreCase(String email);
}
