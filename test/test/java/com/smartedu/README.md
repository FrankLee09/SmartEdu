# SmartEdu 单元测试指南

本文档提供了如何运行单元测试并查看测试覆盖率报告的指南。

## 测试框架与工具

- JUnit 5：Java单元测试框架
- Mockito：模拟框架，用于模拟依赖
- JaCoCo：代码覆盖率工具

## 测试类说明

### Controller层测试
- `WebControllerTest`：测试用户登录、注册和密码更新功能
- `StudentControllerTest`：测试学生相关的CRUD操作
- `CourseControllerTest`：测试课程相关的CRUD操作

### Service层测试
- `UserServiceTest`：测试用户服务的基本功能
- `StudentServiceTest`：测试学生服务的完整功能
- `CourseServiceTest`：测试课程服务的功能
- `FileServiceTest`：测试文件服务的功能

### Entity层测试
- `EntityTest`：测试所有实体类的getter/setter方法

### 其他测试
- `ResultTest`：测试Result通用返回类
- `CustomExceptionTest`：测试自定义异常类

## 运行测试

### 使用IDE运行

1. 在IntelliJ IDEA中，右键点击`src/test/java`目录，选择"Run All Tests"
2. 或者右键点击特定的测试类，选择"Run Test"

### 使用Maven运行

在命令行中，进入项目根目录，执行：

```bash
mvn test
```

## 查看测试覆盖率报告

### 生成测试覆盖率报告

运行测试后，JaCoCo会自动生成测试覆盖率报告。

使用Maven生成报告：

```bash
mvn test jacoco:report
```

### 查看报告

生成的报告位于：`target/site/jacoco/index.html`

在浏览器中打开此文件，可以查看详细的测试覆盖率信息，包括：

- 类覆盖率
- 方法覆盖率
- 行覆盖率
- 分支覆盖率

## 测试覆盖率要求

- 核心功能（如用户注册、登录、数据查询等）：100%覆盖
- 辅助功能（如工具类方法）：至少80%覆盖

## 编写测试的最佳实践

1. 测试类命名：`[被测试类名]Test`
2. 测试方法命名：`test[方法名]_[测试场景]`
3. 使用`@DisplayName`注解提供可读性更好的测试描述
4. 每个测试方法应该只测试一个功能点
5. 使用`@BeforeEach`进行测试数据准备
6. 使用Mockito模拟外部依赖
7. 使用断言验证测试结果

## 测试类型

1. 正常情况测试：验证方法在正确输入下的输出
2. 异常情况测试：验证方法对非法输入的处理
3. 边界条件测试：如空字符串、极值等

## 常用断言方法

- `assertEquals(expected, actual)`：验证期望值等于实际值
- `assertTrue(condition)`：验证条件为true
- `assertFalse(condition)`：验证条件为false
- `assertNull(object)`：验证对象为null
- `assertNotNull(object)`：验证对象不为null
- `assertThrows(Exception.class, executable)`：验证代码抛出指定异常 