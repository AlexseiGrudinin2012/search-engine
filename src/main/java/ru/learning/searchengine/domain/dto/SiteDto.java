package ru.learning.searchengine.domain.dto;

import lombok.Builder;
import lombok.Data;
import ru.learning.searchengine.domain.enums.SiteStatus;

import java.util.Date;

@Data
@Builder
public class SiteDto {

    private Long id;

    private SiteStatus status;

    private Date statusTime;

    private String lastError;

    private String url;

    private String name;
}
