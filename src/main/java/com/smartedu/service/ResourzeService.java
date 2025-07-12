package com.smartedu.service;

import com.smartedu.entity.Resourze;
import com.smartedu.mapper.ResourzeMapper;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourzeService {

   @Resource
    private ResourzeMapper resourzeMapper;


    public List<Resourze> getAllResources() {
        return resourzeMapper.getAllResources();
    }

    public List<Resourze> selectAll(Resourze resourze) {
        return resourzeMapper.selectAll(resourze);
    }

    public Resourze selectById(Long id) {
        return resourzeMapper.selectById(id);
    }

    public void insert(Resourze resourze) {
        resourzeMapper.insert(resourze);
    }

    public void update(Resourze resourze) {
        resourzeMapper.update(resourze);
    }

    public void deleteById(Long id) {
        resourzeMapper.deleteById(id);
    }
}
