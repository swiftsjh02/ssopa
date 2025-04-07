package com.example.demo.entity.Member;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Appointment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Member organizer;

    private String location;

    private LocalDate dueDate;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<Attendance> attendances = new ArrayList<>();
}
