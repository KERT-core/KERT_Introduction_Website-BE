package com.kert.controller;

import com.kert.model.History;
import com.kert.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/histories")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @PostMapping
    public ResponseEntity<History> createHistory(@RequestBody History history) {
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
    public ResponseEntity<History> updateHistory(@PathVariable Long historyId, @RequestBody History historyDetails) {
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
