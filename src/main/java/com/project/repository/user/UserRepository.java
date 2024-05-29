package com.project.repository.user;

import com.project.entity.concretes.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameEquals(String username);

    //User findByUsername(String username, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    boolean existsByEmail(String email);

    Page<User> findByUserRoleEquals(String userRole, Pageable pageable);

    Page<User> findByUsername(String username, Pageable pageable);

}
