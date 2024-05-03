package ru.learning.searchengine.domain.dto;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@Builder
public class PageDto {

    private Long id;

    private SiteDto site;

    private String path;

    private Integer code;

    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageDto pageDto = (PageDto) o;

        Long siteId = this.site == null ? null : this.site.getId();
        Long thatSiteId = pageDto.getSite() == null ? null : pageDto.getSite().getId();
        return Objects.equals(siteId, thatSiteId)
                && StringUtils.equalsIgnoreCase(path, pageDto.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(site, path);
    }

    @Override
    public String toString() {
        return this.path;
    }
}
