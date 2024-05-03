package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.IndexingParsingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndexingParsingServiceImpl implements IndexingParsingService {
    private final PageService pageService;

    private final SiteService siteService;

    public List<SiteDto> getNonIndexingSites() {
        return this.siteService
                .getSiteListByStatuses(SiteStatus.getNonIndexedStatuses());
    }

    @Override
    public void saveAllBySite(SiteDto siteDto) {
        this.siteService.save(siteDto);
    }

    @Override
    public void saveAllBySite(SiteDto siteDto, Set<PageDto> fetchedPages) {
        this.siteService.save(siteDto);
        this.pageService.saveAllBySite(siteDto.getId(), fetchedPages);
    }
}
