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
        StatusResponseModel statusResponseModel = indexingService.startIndexation();
        return statusResponseModel.isResult()
                ? ResponseEntity.ok(statusResponseModel)
                : ResponseEntity.badRequest().body(statusResponseModel);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<StatusResponseModel> stopIndexation() {
        return ResponseEntity.ok(indexingService.stopIndexation());
    }
}
