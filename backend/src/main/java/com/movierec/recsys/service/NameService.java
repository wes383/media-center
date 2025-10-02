package com.movierec.recsys.service;

import com.movierec.recsys.dto.NameDetailDto;
import com.movierec.recsys.mapper.NameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NameService {

    @Autowired
    private NameMapper nameMapper;

    public NameDetailDto getNameDetails(String nconst) {
        return nameMapper.findNameDetailByNconst(nconst);
    }
}