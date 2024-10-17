package org.trivia.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trivia.game.model.Trivia;

public interface TriviaRepository extends JpaRepository<Trivia, Integer> {
}
