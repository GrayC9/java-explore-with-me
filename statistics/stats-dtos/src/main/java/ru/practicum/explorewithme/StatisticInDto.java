package ru.practicum.explorewithme;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticInDto {

    @NotEmpty(message = "Название приложения не может быть пустым")
    private String app;
    @NotEmpty(message = "URI не может быть путсым")
    private String uri;
    @NotEmpty(message = "Необходимо указать IP пользователя")
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Необходимо указать время отправления запроса")
    private LocalDateTime timestamp;
}
