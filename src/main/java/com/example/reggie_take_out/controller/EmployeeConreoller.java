package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeConreoller {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")//登录成功后把employee对象的id存到session表示登录成功，想获取当前用户的话随时都能获取出来request.getSession()
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee  employee){
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp==null){
            return R.error("登录失败");
        }
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name){
        Page<Employee> pageInfo=new Page<>(page,pageSize);//分页构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();//条件构造器
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);//eq()等值查询，like()相似度查询
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("更新成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        return R.success(emp);
    }
}
