package com.smartedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.mapper.FileMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.smartedu.entity.File;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {


    @Resource
    private FileMapper fileMapper;

    public List<File> getAllFiles() {
        return fileMapper.getAllFiles();
    }

    public List<File> selectAll(File file) {
        return fileMapper.selectAll(file);
    }

    public File selectById(Long id) {
        return fileMapper.selectById(id);
    }

    public void addFile(File file) {
        fileMapper.insert(file);
    }


    public void updateFile(File file) {
        File fileOld = this.selectById(file.getId());
        if (StringUtils.hasText(fileOld.getFileUrl()) &&
                StringUtils.hasText(file.getFileUrl()) &&
                !fileOld.getFileUrl().equals(file.getFileUrl())) {

            String fileUrl = fileOld.getFileUrl();
            String[] parts = fileUrl.split("/");
            String fileNameUrl = parts[parts.length - 1];
            this.deleteByUrl(fileNameUrl);
        }
        fileMapper.update(file);
    }

    public void update(File file) {
        fileMapper.update(file);
    }

    public void deleteById(Long id) {
        File file = this.selectById(id);

        String fileUrl = file.getFileUrl();
        String[] parts = fileUrl.split("/");
        String fileNameUrl = parts[parts.length - 1];
        this.deleteByUrl(fileNameUrl);

        fileMapper.deleteById(id);
    }

    public void deleteByUrl(String fileUrl) {

        String DirPath = System.getProperty("user.dir") + "\\files\\" + fileUrl;
        if (DirPath != null) {
            java.io.File localFile = new java.io.File(DirPath);
            if (localFile.exists()) {
                localFile.delete();
            }
        }
    }

    public PageInfo<File> selectPage(File file, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<File> list = fileMapper.selectAll(file);
        return PageInfo.of(list);
    }


}


