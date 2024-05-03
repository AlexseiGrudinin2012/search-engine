package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.LemmaService;
import ru.learning.searchengine.persistance.repositories.LemmaRepository;

@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    @Override
    public Long getLemmaCount(SiteDto siteDto) {
        return siteDto == null || siteDto.getId() == null ?
                0L :
                this.lemmaRepository.countBySiteId(siteDto.getId());
    }
}
