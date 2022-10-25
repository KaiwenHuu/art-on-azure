package com.example.demo.entity.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Description;

public interface DescriptionJpaRepository extends JpaRepository<Description, Long> {

}
