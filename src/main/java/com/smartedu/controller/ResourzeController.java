package com.smartedu.controller;

import com.smartedu.common.Result;
import com.smartedu.entity.Resourze;
import com.smartedu.service.ResourzeService;
import jakarta.annotation.Resource ;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resourze")

public class ResourzeController {

    @Resource
    private ResourzeService resourzeService;

    @GetMapping("/getAll")
    public Result ResultgetAll() {
        List<Resourze> list  = resourzeService.getAllResources();
        return Result.success(list);
    }

    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Resourze resourze) {
        List<Resourze> list = resourzeService.selectAll(resourze);
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(resourzeService.selectById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Resourze resourze) {
        resourzeService.insert(resourze);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Resourze resourze) {
        resourzeService.update(resourze);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        resourzeService.deleteById(id);
        return Result.success();
    }


}
