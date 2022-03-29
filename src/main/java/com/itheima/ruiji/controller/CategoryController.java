package com.itheima.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruiji.common.R;
import com.itheima.ruiji.entity.Category;
import com.itheima.ruiji.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    //新增菜品分类方法，接受参数封装Category对象
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        //调用Service层save方法
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    //分类信息分页查询方法
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器对象
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //根据Sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    //删除分类信息方法，接受参数分类信息ID
    public R<String> delete(Long id){
        log.info("删除分类，id为：{}",id);
        //执行MyBatis-plus所提供的删除方法
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    //修改分类信息方法，接受参数，JSON格式需要使用@RequestBody注解
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        //执行MyBatis-plus所提供的修改方法
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    @GetMapping("/list")
    //根据条件查询分类信息
    public R<List<Category>> list (Category category){
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //type属性不为空，按type属性进行查询，1 查询菜品分类，2 查询套餐分类
        categoryLambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //执行查询，使用集合接受
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);
    }
}
