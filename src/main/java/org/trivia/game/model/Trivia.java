package org.trivia.game.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Trivia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer triviaId;

    private String question;

    @Transient
    private String[] possibleAnswers;

    @Column(name = "correct_answer")
    private String correctAnswer;

    private Integer answerAttempts;
}
