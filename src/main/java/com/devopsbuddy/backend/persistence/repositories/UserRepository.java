package com.devopsbuddy.backend.persistence.repositories;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by root on 09/06/17.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Returns a User given a username or null if not found.
     *
     * @param username The username.
     * @return
     */
    public User findByUsername(String username);
}
