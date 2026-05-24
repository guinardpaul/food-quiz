package com.guinardsolutions.foodquiz.api.mapper;

import com.guinardsolutions.foodquiz.api.dto.QuizResultDto;
import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;

public class QuizResultMapper {

    public QuizResultDto toDto(QuizResultResponse response) {
        return new QuizResultDto(response.score(), response.total());
    }
}
