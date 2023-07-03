package ru.practicum.explorewithme.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInDto {

    @NotEmpty
    @NotBlank
    private String email;
    @NotEmpty
    @NotBlank
    private String name;
}
