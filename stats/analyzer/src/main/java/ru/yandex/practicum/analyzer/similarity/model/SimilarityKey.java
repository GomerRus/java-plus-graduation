package ru.yandex.practicum.analyzer.similarity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimilarityKey implements Serializable {
    @Column(name = "event_a_id")
    Long eventAId;

    @Column(name = "event_b_id")
    Long eventBId;
}
