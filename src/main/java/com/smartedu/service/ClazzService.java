package com.smartedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Clazz;
import com.smartedu.entity.Student;
import com.smartedu.mapper.ClazzMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClazzService {

    @Resource
    ClazzMapper clazzMapper;

   public List<Clazz> getAllclasses(){
       return  clazzMapper.getAllclasses();
    }

    public List<Clazz> selectAll(Clazz clazz) {
        return clazzMapper.selectAll(clazz);
    }

   public Clazz selectById(Long id){
        return clazzMapper.selectById(id);
    };

    public PageInfo<Clazz> selectPage(Clazz clazz, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Clazz> list =clazzMapper.selectAll(clazz);
        return PageInfo.of(list);
    }

    /** 新增班级，成功返回插入后带主键的对象 */
    public void addClazz(Clazz clazz){
        clazzMapper.insert(clazz);
    };

    /** 更新班级信息，成功返回 true */
    public void updateClazz(Clazz clazz){
        clazzMapper.update(clazz);
    };

    /** 根据 ID 删除班级，成功返回 true */
    public void deleteById(Long id){
        clazzMapper.deleteById(id);
    };

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }


}
