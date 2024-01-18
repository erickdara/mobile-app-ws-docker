package com.developer.app.ws.repository;

import com.developer.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}
