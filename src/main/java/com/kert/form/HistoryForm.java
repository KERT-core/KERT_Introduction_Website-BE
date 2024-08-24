package com.kert.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryForm {

    @NotNull(message = "연도는 필수 항목입니다.")
    @Min(value = 1974, message = "연도는 1974 이상이어야 합니다.")
    private Integer year;

    @NotNull(message = "월은 필수 항목입니다.")
    @Min(value = 1, message = "월은 1 이상이어야 합니다.")
    @Max(value = 12, message = "월은 12 이하여야 합니다.")
    private Integer month;

    @NotNull(message = "제목은 필수 항목입니다.")
    private String content;
}
