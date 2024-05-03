package ru.learning.searchengine.presentation.controllers.indexations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.presentation.models.StatusResponseModel;

@RestController()
@RequestMapping("/api")
@RequiredArgsConstructor
public class IndexationsController {
    private final IndexingService indexingService;

    @GetMapping("/startIndexing")
    public ResponseEntity<StatusResponseModel> startIndexation() {
        if (this.indexingService.isStarted()) {
            return ResponseEntity.badRequest().body(
                    StatusResponseModel
                            .builder()
                            .error("Индексация уже запущена")
                            .result(false)
                            .build()
            );
        }
        this.indexingService.start();
        return ResponseEntity.ok(StatusResponseModel.builder().result(true).build());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<StatusResponseModel> stopIndexation() {
        this.indexingService.stop();
        return ResponseEntity.ok(StatusResponseModel.builder().result(true).build());
    }
}
