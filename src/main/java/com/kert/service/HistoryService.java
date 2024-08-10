package com.kert.service;

import com.kert.model.History;
import com.kert.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {
    @Autowired
    private HistoryRepository historyRepository;

    public List<History> getAllHistories() {
        return historyRepository.findAll();
    }

    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).orElse(null);
    }

    public History createHistory(History history) {
        return historyRepository.save(history);
    }

    public History updateHistory(Long historyId, History historyDetails) {
        Optional<History> historyOptional = historyRepository.findById(historyId);
        if (historyOptional.isPresent()) {
            History existingHistory = historyOptional.get();
            existingHistory.setYear(historyDetails.getYear());
            existingHistory.setMonth(historyDetails.getMonth());
            existingHistory.setDescription(historyDetails.getDescription());
            existingHistory.setCreatedAt(historyDetails.getCreatedAt());
            existingHistory.setUpdatedAt(historyDetails.getUpdatedAt());
            return historyRepository.save(existingHistory);
        }
        return null;
    }

    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }
}
