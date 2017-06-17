package com.devopsbuddy.backend.persistence.repositories;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by root on 09/06/17.
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Returns a User given a username or null if not found.
     *
     * @param username The username.
     * @return a user given a username or null if not found
     */
    User findByUsername(String username);

    /**
     * Returns a user for the given email or null if not found
     *
     * @param email The user's email
     * @return a user for the given email or null if not found
     */
    User findByEmail(String email);

    @Transactional
    @Modifying // Indicates to JPA engine that the content of the @Query annotation will change the database state
    @Query("update User user set user.password =:password where user.id =:userId")
    void updateUserPassword(@Param("userId") int userId, @Param("password") String password);
}
