package com.smartedu.controller;

import com.smartedu.common.Result;
import com.smartedu.entity.Question;
import com.smartedu.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    // 查询所有
    @GetMapping("/getAll")
    public Result getAll() {
        return Result.success(questionService.getAllQuestions());
    }

    // 模糊组合查询
    @PostMapping("/selectAll")
    public Result  selectAll(@RequestBody Question question) {
        return Result.success(questionService.selectAll(question));
    }

    // 按 ID 查询
    @GetMapping("/selectById/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(questionService.selectById(id));
    }

    // 添加
    @PostMapping("/add")
    public Result  add(@RequestBody Question question) {
        questionService.insert(question);
        return Result.success();
    }

    // 更新
    @PutMapping("/update")
    public Result update(@RequestBody Question question) {
        questionService.update(question);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        questionService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/random")
    public Result getRandomQuestions(@RequestParam("courseId") Long courseId,
                                     @RequestParam("kgPoint") String KGpoint,
                                     @RequestParam("selectCount") int selectCount,
                                     @RequestParam("shortAnswerCount") int shortAnswerCount) {
        List<Question> list = questionService.getRandomQuestions(courseId,KGpoint,selectCount, shortAnswerCount);
        return Result.success(list);
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result  add(@RequestBody List<Question> questionList) {
        for(Question question : questionList) {
            questionService.insert(question);
        }
        return Result.success();
    }

}
