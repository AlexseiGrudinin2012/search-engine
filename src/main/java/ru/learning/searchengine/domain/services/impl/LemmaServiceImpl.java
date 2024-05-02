package ru.learning.searchengine.domain.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.LemmaService;
import ru.learning.searchengine.persistance.repositories.LemmaRepository;

@Service
public class LemmaServiceImpl implements LemmaService {


    private final LemmaRepository lemmaRepository;

    @Autowired
    public LemmaServiceImpl(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public Long getLemmaCount(SiteDto siteDto) {
        return siteDto == null || siteDto.getId() == null ?
                0L :
                this.lemmaRepository.countBySiteId(siteDto.getId());
    }
}
