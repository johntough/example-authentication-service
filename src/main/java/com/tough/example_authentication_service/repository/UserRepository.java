package com.tough.example_authentication_service.repository;

import com.tough.example_authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByIdpSubject(String idpSubject);
}