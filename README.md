# 小小 - 定制化代码生成项目

## 项目介绍

这是基于 React + Spring Boot + Vert.x 响应式编程的 **定制化代码生成项目** 。

### 调研

网上有一些代码生成器项目，比如前端 [Ant Design Pro](https://pro.ant.design/zh-CN) 中后台项目脚手架，能够让用户交互式地创建指定的项目；还有后端 MyBatis X 插件，能够让用户通过界面来创建 CRUD 重复代码。但这些项目都是开发者提前制作好了代码生成器，然后让你 **根据他们设置好的规则** 生成代码（或者拉取特定位置的代码），生成后的代码通常还要再自己二次修改，不够灵活。

还有很多所谓的代码生成项目，其实本质上是一个现成的项目模板，让你通过编写对应的配置文件来使用项目，或者还是基于预设的程序来生成特定代码。

## 技术选型

暂定的核心技术如下，实际开发中还会引入新技术

### 前端

- React 开发框架 + 组件库 + 代码编辑器
- 前端工程化：ESLint + Prettier + TypeScript

### 后端

- Java Spring Boot + MySQL + MyBatis Plus
- Java 命令行应用开发
- FreeMarker 模板引擎
- Vert.x 响应式编程
- Caffeine + Redis 多级缓存
- 分布式任务调度系统
- 多种设计模式
- 多种系统设计的巧思
- 对象存储

### 代码生成器的核心原理

一句话：参数 + 模板文件 = 生成的完整代码

比如参数：

```java
author = 小小
```

模板文件代码：

```java
-----------
author ${作者}
-----------
```

将参数注入到模板文件中，得到生成的完整代码：

```java
-----------
author 小小
-----------
```

如果想要使用这套模板生成其他的代码，只需要改变参数的值即可，而不需要改变模板文件。

------
