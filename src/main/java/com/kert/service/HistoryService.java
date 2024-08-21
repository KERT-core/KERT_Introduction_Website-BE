package com.kert.service;

import com.kert.model.History;
import com.kert.repository.HistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public List<History> getAllHistories() {
        return historyRepository.findAll();
    }

    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).orElse(null);
    }

    public History createHistory(History history) {
        return historyRepository.save(history);
    }

    @Transactional
    public History updateHistory(Long historyId, History historyDetails) {
        Optional<History> historyOptional = historyRepository.findById(historyId);
        if (historyOptional.isPresent()) {
            History existingHistory = historyOptional.get();
            existingHistory.setYear(historyDetails.getYear());
            existingHistory.setMonth(historyDetails.getMonth());
            existingHistory.setDescription(historyDetails.getDescription());
            existingHistory.setUpdatedAt(historyDetails.getUpdatedAt());
            return historyRepository.save(existingHistory);
        }
        return null;
    }

    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }
}
