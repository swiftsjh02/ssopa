package com.example.demo.repository.Member;

import com.example.demo.entity.Member.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}

