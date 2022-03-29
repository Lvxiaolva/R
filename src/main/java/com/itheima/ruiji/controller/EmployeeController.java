package com.itheima.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruiji.common.R;
import com.itheima.ruiji.entity.Employee;
import com.itheima.ruiji.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    //后台登录方法，接收前台请求和参数，封装Employee对象，JSON格式需要用@RequestBody注解，
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //获取用户密码
        String password = employee.getPassword();
        //密码使用MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //创建LambadaQueryWrqpper条件构造器
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //通过条件构造器设置Username为页面提交的用户名
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());

//        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
//        employeeQueryWrapper.eq("Username",employee.getUsername());

        //通过Username查询数据库，获取Employee对象
        Employee one = employeeService.getOne(employeeLambdaQueryWrapper);
        //数据库查不到，Employee对象为空，登录失败
        if (one == null) {
            return R.error("登录失败");
        }
        // 密码错误，登陆失败
        if (!one.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //用户状态为0-禁用，登陆失败
        if (one.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //获取Session对象，把Id存入Session中，key是"employee"，value是id
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    //后台登出方法，接受前台登出请求
    public R<String> logout(HttpServletRequest request){
        //获取Session对象，通过key("employee")清空Session对象
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    //新增用户方法，接受前台请求和参数，参数封装为Employee对象，JSON格式需要@RequestBody
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //默认设置初始密码为123456，使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //调用IService中save方法，把传入的Employee插入到数据库中
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    //员工分页查询方法，接受参数每页显示条数、页码数、员工姓名
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //创建Page对象
        Page pageInfo = new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //判断参数员工姓名是否为空，如果不为空，根据输入的用户名进行模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //按照更新时间进行排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    //员工信息修改方法，接受前台请求和参数，参数封装Employee对象，JSON格式需要@RequestBody注解
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        //执行业务层中的updateById方法
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    //根据Id查询方法，接受参数id,路径传参需要@PathVariable
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        //调用Service中的getById方法，获取一个Employee对象
        Employee employee = employeeService.getById(id);
        //对象不为空，查询成功
        if(employee != null){
            return R.success(employee);
        }
        //否则查询失败
        return R.error("没有查询到对应员工信息");
    }
}


