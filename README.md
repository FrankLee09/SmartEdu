# SmartEdu 项目说明文档

## 项目概述

SmartEdu 是一个智能教育相关的后端项目，提供了丰富的教育业务接口，涵盖资源管理、考试管理、学生答题、文件处理、知识图谱、推荐系统等多个方面。项目采用 Spring Boot 框架构建，通过 RESTful API 与前端交互，支持多种业务场景开发。

## 项目结构

controller：负责处理 HTTP 请求，调用服务层方法并返回处理结果  
service：实现具体的业务逻辑，调用数据访问层进行数据操作  
entity：定义实体类，用于表示数据库中的表结构  
mapper：数据访问层，负责与数据库交互  
common：包含通用工具类和配置类等

## 环境要求

Java 8 或更高版本  
Spring Boot 框架  
数据库（支持 MySQL、Oracle 等）
(本项目数据库使用Kingbase金仓数据库)

## 接口文档

资源管理接口（路径前缀 /resourze）  
获取所有资源：GET /resourze/getAll  
条件查询资源：POST /resourze/selectAll  
根据 ID 查询资源：GET /resourze/selectById/{id}  
添加资源：POST /resourze/add  
更新资源：PUT /resourze/update  
删除资源：DELETE /resourze/delete/{id}

考试管理接口（路径前缀 /exam）  
获取所有考试：GET /exam/getAll  
条件查询考试：POST /exam/selectAll  
根据 ID 查询考试：GET /exam/selectById/{id}  
查询考试题目：GET /exam/selectQuestionsById/{id}  
添加考试：POST /exam/add  
更新考试：PUT /exam/update  
删除考试：DELETE /exam/delete/{id}  
批量添加考试：POST /exam/addJson

文件管理接口（路径前缀 /files）  
上传文件（方式一）：POST /files/upload  
上传文件（方式二）：POST /files/uploadFile  
下载文件：GET /files/download/{fileName}  
分页查询文件：GET /files/selectPage  
获取所有文件：GET /files/getAll  
条件查询文件：GET /files/selectAll  
根据 ID 查询文件：GET /files/selectById/{id}  
添加文件信息：POST /files/add  
更新文件信息：PUT /files/update  
更新文件（含删除旧文件）：PUT /files/updateFile  
删除文件（按 ID）：DELETE /files/deleteById/{id}  
删除文件（按 URL）：DELETE /files/deleteByUrl?fileUrl=xxx

班级管理接口（路径前缀 /class）  
获取所有班级：GET /class/getAll  
根据 ID 查询班级：GET /class/selectById/{id}  
分页查询班级：GET /class/selectPage  
条件查询班级：GET /class/selectAll  
添加班级：POST /class/add  
删除班级（按 ID）：DELETE /class/deleteById/{id}  
批量删除班级：DELETE /class/deleteBatch  
更新班级：PUT /class/update  
批量添加班级：POST /class/addJson

任务管理接口（路径前缀 /task）  
获取所有任务：GET /task/getAll  
根据 ID 查询任务：GET /task/selectById/{id}  
分页查询任务：GET /task/selectPage  
条件查询任务：GET /task/selectAll  
添加任务：POST /task/add  
删除任务（按 ID）：DELETE /task/deleteById/{id}  
批量删除任务：DELETE /task/deleteBatch  
更新任务：PUT /task/update  
批量添加任务：POST /task/addJson

Dify 相关接口（路径前缀 /dify）  
生成知识图谱：POST /dify/knowledgeGraph  
题目评分：POST /dify/questionScore  
报告评分：POST /dify/reportScore?file=xxx&userId=xxx  
获取学习推荐：GET /dify/recommend/{studentId}

提交记录接口（路径前缀 /submission）  
获取所有提交记录：GET /submission/getAll  
条件查询提交记录：POST /submission/selectAll  
根据 ID 查询提交记录：GET /submission/selectById/{id}  
分页查询提交记录：GET /submission/selectPage  
添加提交记录：POST /submission/add  
删除提交记录：DELETE /submission/deleteById/{id}  
更新提交记录：PUT /submission/update  
批量添加提交记录：POST /submission/addJson

## 项目运行

第一步 克隆项目  
git clone https://github.com/your-repo/smartedu.git

第二步 配置数据库连接  
修改 application.properties 或 application.yml 文件

第三步 启动项目  
mvn spring-boot:run


