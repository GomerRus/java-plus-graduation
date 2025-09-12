package ru.practicum.main.service.compilation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.event.model.Event;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    Long id;

    @Column(nullable = false)
    Boolean pinned;

    @Column(name = "compilation_title", nullable = false, unique = true, length = 50)
    String title;

    @ManyToMany
    @JoinTable(name = "events_compilations",
            joinColumns = {@JoinColumn(name = "compilation_id", referencedColumnName = "compilation_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "event_id")})
    Set<Event> events;
}










