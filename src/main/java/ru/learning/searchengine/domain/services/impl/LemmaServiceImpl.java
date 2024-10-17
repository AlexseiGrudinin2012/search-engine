package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.LemmaService;
import ru.learning.searchengine.persistance.repositories.LemmaRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    @Override
    public Long getLemmaCount(SiteDto siteDto) {
        return Optional.of(siteDto)
                .map(SiteDto::getId)
                .map(lemmaRepository::countBySiteId)
                .orElse(0L);
    }
}
