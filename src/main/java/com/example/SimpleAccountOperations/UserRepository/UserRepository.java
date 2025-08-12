package com.example.SimpleAccountOperations.UserRepository;

import com.example.SimpleAccountOperations.UserInfo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Boolean existsByEmail(String email);

    Optional<UserInfo> findByEmail(String email);

    UserInfo getUserInfosByEmail(String email);

    List<UserInfo> findByLastName(String lastName);
}
