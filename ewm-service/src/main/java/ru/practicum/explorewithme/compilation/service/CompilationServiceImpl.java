package ru.practicum.explorewithme.compilation.service;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public class CompilationServiceImpl implements CompilationService {
    @Override
    public List<CompilationDto> findCompilations() {
        return null;
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        return null;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return null;
    }

    @Override
    public void deleteCompilation(Long compId) {

    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest update) {
        return null;
    }
}
