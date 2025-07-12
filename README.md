### 项目简介
Smartedu 是一个教育领域的综合性服务平台，采用 Spring Boot 框架构建后端服务，Vue框架构建前端。该项目涵盖了学生、教师、课程、考试、任务、文件管理等多个模块，为教育机构或学校提供了一站式的解决方案。通过该平台，教师可以方便地发布任务、管理课程和考试，学生可以提交作业、参加考试，同时系统还提供了成绩查询、学习推荐等功能，以帮助学生更好地掌握知识。此外，平台还集成了AI大模型服务，用于题目评分、知识图谱生成等，提升了教学和学习的效率。

### 功能说明

#### 用户管理
- **登录与注册**：支持学生和教师分别进行登录和注册操作，根据用户角色进行不同的权限验证。
- **密码修改**：学生和教师可以修改自己的登录密码。

#### 课程管理
- **课程信息管理**：教师可以添加、更新、删除和查询课程信息，包括课程描述等。
- **批量操作**：支持批量添加课程信息。

#### 考试管理
- **考试信息管理**：可以添加、更新、删除和查询考试信息。
- **题目关联**：通过考试 ID 可以查询该考试中的所有题目。
- **批量操作**：支持批量添加考试信息。

#### 任务管理
- **任务信息管理**：教师可以添加、更新、删除和查询任务信息。
- **批量操作**：支持批量添加任务信息。

#### 成绩管理
- **任务成绩**：可以添加、查询和统计学生的任务成绩，提供柱状图和折线图展示成绩分布和趋势。
- **考试成绩**：可以添加、更新、删除和查询学生的考试成绩，支持批量删除成绩信息。
- **批量操作**：支持批量添加任务成绩和考试成绩信息。

#### 文件管理
- **文件上传与下载**：用户可以上传文件，并获取文件的下载链接，同时可以根据文件名下载文件。
- **文件信息管理**：可以添加、更新、删除和查询文件信息。
- **批量操作**：支持批量添加文件信息。

#### AI大模型服务集成
- **题目评分**：利用大模型对学生的题目解答进行评分，并记录学生的答题信息。
- **知识图谱生成**：根据输入的知识点内容，生成相应的知识图谱。
- **报告评分**：上传报告文件，通过大模型分析报告并提取得分。
- **学习推荐**：根据学生的知识点掌握情况，为学生提供学习推荐。

#### 提交管理
- **提交信息管理**：可以添加、更新、删除和查询学生的提交信息。
- **批量操作**：支持批量添加提交信息。

#### 班级管理
- **班级信息管理**：可以添加、更新、删除和查询班级信息。
- **批量操作**：支持批量添加班级信息。

### 安装部署指南

#### 环境要求
- **Java**：JDK 1.8 及以上版本
- **数据库**：支持 MyBatis 集成的数据库，如 MySQL
- **开发工具**：IntelliJ IDEA 或 Eclipse 等

#### 步骤
1. **下载项目**：从代码仓库中克隆或下载 Smartedu 项目到本地。
2. **数据库配置**：
    - 创建数据库，并根据项目中的 SQL 脚本初始化数据库表结构。
    - 在项目的配置文件中（如 `application.properties` 或 `application.yml`）配置数据库连接信息，包括数据库地址、用户名和密码。
3. **依赖安装**：使用 Maven 或 Gradle 下载项目的依赖库。在 IDE 中，通常可以通过自动导入功能完成依赖下载。
4. **修改配置**：根据实际情况修改项目中的配置信息，如文件存储路径、大模型服务的 API 地址和密钥等。
5. **启动项目**：运行 `SmarteduServerApplication.java` 类的 `main` 方法，启动 Spring Boot 应用程序。
6. **验证部署**：使用浏览器或 API 测试工具访问项目的接口，验证项目是否正常启动。

### 使用说明

#### 登录与注册
- 学生和教师可以在登录页面输入用户名和密码进行登录。
- 学生可以在注册页面填写个人信息进行注册，教师需要通过教师注册页面进行注册。

#### 课程管理
- 教师登录后，可以在课程管理页面进行课程的添加、更新、删除和查询操作。
- 支持批量导入课程信息，提高管理效率。

#### 考试管理
- 教师可以在考试管理页面添加、更新、删除和查询考试信息。
- 通过考试 ID 可以查看该考试中的所有题目。
- 支持批量导入考试信息。

#### 任务管理
- 教师可以在任务管理页面添加、更新、删除和查询任务信息。
- 支持批量导入任务信息。

#### 成绩管理
- 教师可以在成绩管理页面添加、查询和统计学生的任务成绩和考试成绩。
- 学生可以查看自己的成绩分布和趋势图。
- 支持批量导入和删除成绩信息。

#### 文件管理
- 用户可以在文件管理页面上传和下载文件。
- 支持对文件信息进行添加、更新、删除和查询操作。
- 支持批量导入文件信息。

#### 大模型服务使用
- **题目评分**：教师可以在题目评分页面输入题目信息和学生的解答，调用大模型进行评分。
- **知识图谱生成**：在知识图谱页面输入知识点内容，生成相应的知识图谱。
- **报告评分**：上传报告文件，系统会调用大模型分析报告并提取得分。
- **学习推荐**：学生可以在学习推荐页面查看为自己生成的学习建议。

#### 提交管理
- 学生可以在提交管理页面提交作业或其他信息。
- 教师可以对学生的提交信息进行管理，包括添加、更新、删除和查询操作。
- 支持批量导入提交信息。

#### 班级管理
- 教师可以在班级管理页面添加、更新、删除和查询班级信息。
- 支持批量导入班级信息。

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


### 开发成员及分工
#### 后端开发
李昊然 20237061

郑德宇 20236914

魏煊浦 20237099
#### 前端开发
刘炜彬 20236988
#### 项目测试
赵越 20236706

## 项目运行

第一步 克隆项目  
git clone https://github.com/FrankLee09/SmartEdu.git

第二步 配置数据库连接  
修改 application.properties 或 application.yml 文件

第三步 启动项目  
mvn spring-boot:run
