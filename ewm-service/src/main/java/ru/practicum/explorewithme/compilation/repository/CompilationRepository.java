package ru.practicum.explorewithme.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
