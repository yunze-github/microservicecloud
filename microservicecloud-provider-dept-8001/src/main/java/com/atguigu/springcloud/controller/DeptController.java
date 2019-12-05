package com.atguigu.springcloud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.springcloud.entities.Dept;
import com.atguigu.springcloud.service.DeptService;


@RestController
@RequestMapping("dept")
public class DeptController {

	@Autowired
	private DeptService deptService;
	
	/**
	 * 1.盘点eureka中注册微服务，自动注入DiscoveryClient对象
	 * 2.Controller中提供盘点eureka中注册的dept部门微服务访问接口
	 * 3.provider微服务启动类上添加服务发现注解@EnableDiscoveryClient
	 * 4.consumer微服务提供一个对外访问的服务发现api
	 */
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	public boolean add(@RequestBody Dept dept)
	{
		return deptService.add(dept);
		
	}
	
	@RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
	public Dept get(@PathVariable Long id)
	{
		return deptService.get(id);
	}
	
	@GetMapping(value = "/list")
	public List<Dept> list()
	{
		return deptService.list();
	}
}
