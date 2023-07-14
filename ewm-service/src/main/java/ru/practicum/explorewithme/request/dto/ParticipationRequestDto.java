package ru.practicum.explorewithme.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private Long id;
    @NotNull
    private String created;
    @NotNull
    private Long event;
    @NotNull
    private Long requester;
    @NotEmpty
    @NotBlank
    private String status;
}
