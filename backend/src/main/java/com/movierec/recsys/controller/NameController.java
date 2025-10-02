package com.movierec.recsys.controller;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.dto.NameDetailDto;
import com.movierec.recsys.service.NameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/names")
public class NameController {

    @Autowired
    private NameService nameService;

    @GetMapping("/{nconst}")
    public ApiResponse<NameDetailDto> getNameById(@PathVariable String nconst) {
        NameDetailDto nameDetail = nameService.getNameDetails(nconst);
        return ApiResponse.success(nameDetail);
    }
}