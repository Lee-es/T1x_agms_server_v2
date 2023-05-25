package com.example.uxn_common.global.domain_new.person.repository;

import com.example.uxn_common.global.domain_new.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findById(int id);

    Person findByEmail(String email);
}