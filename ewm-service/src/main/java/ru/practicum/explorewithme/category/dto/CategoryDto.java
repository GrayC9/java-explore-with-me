package ru.practicum.explorewithme.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия категории должна быть от 1 до 50")
    private String name;
}
