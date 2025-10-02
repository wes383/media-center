package com.movierec.recsys.controller.me;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.dto.MyWatchHistoryDto;
import com.movierec.recsys.dto.PaginatedResponseDto;
import com.movierec.recsys.dto.WatchHistoryRequestDto;
import com.movierec.recsys.model.me.MyWatchHistory;
import com.movierec.recsys.service.me.MyWatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/history")
public class MyWatchHistoryController {

    @Autowired
    private MyWatchHistoryService myWatchHistoryService;

    @PostMapping
    public ApiResponse<MyWatchHistory> addOrUpdateHistory(@RequestBody WatchHistoryRequestDto requestDto) {
        MyWatchHistory updatedHistory = myWatchHistoryService.addOrUpdateHistory(requestDto);
        return ApiResponse.success(updatedHistory);
    }

    @GetMapping
    public ApiResponse<PaginatedResponseDto<MyWatchHistoryDto>> getMyHistory(
            @RequestParam(required = false) String titleType,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Float minRating,
            @RequestParam(defaultValue = "added_at") String sortBy,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {

        PaginatedResponseDto<MyWatchHistoryDto> result = myWatchHistoryService.getMyHistory(
                titleType, genre, startYear, minRating, sortBy, limit, offset);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{tconst}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String tconst) {
        myWatchHistoryService.deleteHistory(tconst);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tconst}")
    public ResponseEntity<?> getHistoryByTconst(@PathVariable String tconst) {
        MyWatchHistory history = myWatchHistoryService.getHistoryByTconst(tconst);
        if (history != null) {
            return ResponseEntity.ok(history);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}