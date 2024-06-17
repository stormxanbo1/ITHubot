package com.example.ITHubot.Models;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user_scores")
public class UserScore {

    @Id
    @Column(name = "user_id")
    private Long userId;

    private Integer totalScore;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
