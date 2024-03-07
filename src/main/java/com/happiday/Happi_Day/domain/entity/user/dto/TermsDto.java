package com.happiday.Happi_Day.domain.entity.user.dto;

import lombok.Getter;

@Getter
public class TermsDto {
    private String termsService;
    private String termsPrivacy;

    public TermsDto(String termsService, String termsPrivacy) {
        this.termsService = termsService;
        this.termsPrivacy = termsPrivacy;
    }
}
