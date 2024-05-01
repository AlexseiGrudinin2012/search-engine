package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.statistics.StatisticsResponseDto;
import ru.learning.searchengine.domain.exceptions.StatisticsNotFoundException;
import ru.learning.searchengine.domain.services.StatisticsService;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();
//    private final SitesList sites;

    @Override
    public StatisticsResponseDto getStatistics() {
//        String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
//        String[] errors = {
//                "Ошибка индексации: главная страница сайта не доступна",
//                "Ошибка индексации: сайт не доступен",
//                ""
//        };
//
//        TotalStatisticsModel total = new TotalStatisticsModel();
//        total.setSites(sites.getSites().size());
//        total.setIndexing(true);
//
//        List<DetailedStatisticsItemModel> detailed = new ArrayList<>();
//        List<SiteEntity> sitesList = sites.getSites();
//        for(int i = 0; i < sitesList.size(); i++) {
//            SiteEntity site = sitesList.get(i);
//            DetailedStatisticsItemModel item = new DetailedStatisticsItemModel();
//            item.setName(site.getName());
//            item.setUrl(site.getUrl());
//            int pages = random.nextInt(1_000);
//            int lemmas = pages * random.nextInt(1_000);
//            item.setPages(pages);
//            item.setLemmas(lemmas);
//            item.setStatus(statuses[i % 3]);
//            item.setError(errors[i % 3]);
//            item.setStatusTime(System.currentTimeMillis() -
//                    (random.nextInt(10_000)));
//            total.setPages(total.getPages() + pages);
//            total.setLemmas(total.getLemmas() + lemmas);
//            detailed.add(item);
//        }
//
//        StatisticsResponseModel response = new StatisticsResponseModel();
//        StatisticsDataModel data = new StatisticsDataModel();
//        data.setTotal(total);
//        data.setDetailed(detailed);
//        response.setStatistics(data);
//        response.setResult(true);
//        return response;
        throw new StatisticsNotFoundException("Нет статистики");
    }
}
