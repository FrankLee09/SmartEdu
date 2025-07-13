package com.smartedu.controller;

import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.exception.CustomException;
import com.smartedu.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smartedu.entity.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    @Resource
    FileService fileService;


    private static final String filePath = System.getProperty("user.dir") + "/files/";


//    @PostMapping("/upload")
//    public Result upload(@RequestParam("file")MultipartFile file,@RequestParam("fileUrl")String fileUrl) throws IOException {
//        Result result = fileService.upload(file,fileUrl);
//        return result;
//    }


    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        String fileUrl = System.currentTimeMillis() + "_" + originalFilename;
        String realPath = filePath + fileUrl;
        try {
            FileUtil.writeBytes(file.getBytes(), realPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "文件上传失败");
        }
        String url = "http://localhost:8080/files/download/" + fileUrl;
        return Result.success(url);
    }


    @PostMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        String fileUrl = System.currentTimeMillis() + "_" + originalFilename;
        String realPath = filePath + fileUrl;
        try {
            FileUtil.writeBytes(file.getBytes(), realPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "文件上传失败");
        }
        String url = "http://localhost:8080/files/" + fileUrl;
        return Result.success(url);
    }



    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setContentType("application/octet-stream");
            OutputStream os = response.getOutputStream();
            String realPath = filePath + fileName;
            byte[] bytes = FileUtil.readBytes(realPath);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("500", "文件下载失败");
        }
    }


    @GetMapping("/selectPage")
    public Result selectPage(File file,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<File> pageInfo = fileService.selectPage(file, pageNum, pageSize);
        return Result.success(pageInfo);
    }

        @GetMapping("/getAll")
        public Result getAllFiles() {
            List<File> list = fileService.getAllFiles();
            return Result.success(list);
        }

        @GetMapping("/selectAll")
        public Result selectAll(File file) {
            List<File> list = fileService.selectAll(file);
            return Result.success(list);
        }

        @GetMapping("/selectById/{id}")
        public Result selectById(@PathVariable Long id) {
            File file = fileService.selectById(id);
            return Result.success(file);
        }

        @PostMapping("/add")
        public Result add(@RequestBody File file) {
            fileService.addFile(file);
            return Result.success();
        }

        @PutMapping("/update")
        public Result update(@RequestBody File file) {

            fileService.update(file);
            return Result.success();
        }

    @PutMapping("/updateFile")
    public Result updateFile(@RequestBody File file) {
        fileService.updateFile(file);
        return Result.success();
    }

        @DeleteMapping("/deleteById/{id}")
        public Result deleteById(@PathVariable Long id) {
            fileService.deleteById(id);
            return Result.success();
        }



        @DeleteMapping("/deleteByUrl")
        public Result deleteByUrl(@RequestParam("fileUrl") String fileUrl) {
            String[] parts = fileUrl.split("/");
            String fileNameUrl = parts[parts.length - 1];


            fileService.deleteByUrl(fileNameUrl);

            return Result.success();
        }

}

//            File file =  fileService.selectById(id);
//            String fileUrl = file.getFileUrl();
//            String[] parts = fileUrl.split("/");
//            String fileNameUrl = parts[parts.length - 1];
//            fileService.deleteByUrl(fileNameUrl);
//


//
//        File fileOld = fileService.selectById(file.getId());
//        if(!fileOld.getFileUrl().equals(file.getFileUrl())){
//            String fileUrl = fileOld.getFileUrl();
//            String[] parts = fileUrl.split("/");
//            String fileNameUrl = parts[parts.length - 1];
//            fileService.deleteByUrl(fileNameUrl);
//        }