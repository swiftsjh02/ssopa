package com.example.demo.entity.Member;

import javax.persistence.*;

@Entity
public class Attendance {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
