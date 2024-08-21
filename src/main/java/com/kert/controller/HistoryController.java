package com.kert.controller;

import com.kert.form.HistoryForm;
import com.kert.model.History;
import com.kert.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/histories")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<History> createHistory(@Valid @RequestBody HistoryForm historyForm) {
        History history = new History();
        history.setYear(historyForm.getYear());
        history.setMonth(historyForm.getMonth());
        history.setDescription(historyForm.getDescription());
        History createdHistory = historyService.createHistory(history);
        return ResponseEntity.ok(createdHistory);
    }

    @GetMapping
    public List<History> getAllHistories() {
        return historyService.getAllHistories();
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<History> getHistoryById(@PathVariable Long historyId) {
        History history = historyService.getHistoryById(historyId);
        if (history == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{historyId}")
    public ResponseEntity<History> updateHistory(@PathVariable Long historyId, @Valid @RequestBody HistoryForm historyForm) {
        History historyDetails = new History();
        historyDetails.setYear(historyForm.getYear());
        historyDetails.setMonth(historyForm.getMonth());
        historyDetails.setDescription(historyForm.getDescription());
        History updatedHistory = historyService.updateHistory(historyId, historyDetails);
        if (updatedHistory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedHistory);
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long historyId) {
        historyService.deleteHistory(historyId);
        return ResponseEntity.noContent().build();
    }
}
