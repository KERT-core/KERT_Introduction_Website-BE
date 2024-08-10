package com.kert.service;

import com.kert.model.History;
import com.kert.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        History existingHistory = getHistoryById(historyId);
        if (existingHistory != null) {
            existingHistory.setHistory(historyDetails.getHistory()); // 이렇게 관련된 HistoryList를 업데이트합니다.
            return historyRepository.save(existingHistory);
        }
        return null;
    }

    public boolean deleteHistory(Long historyId) {
        if (historyRepository.existsById(historyId)) {
            historyRepository.deleteById(historyId);
            return true;
        }
        return false;
    }
}
