# Leaf

> There are no two identical leaves in the world.
>
> 世界上没有两片完全相同的树叶。
>
> ​								— 莱布尼茨

[优化版中文文档](./README_CN_thomas.md) | [中文文档](./README_CN.md) | [English Document](./README.md)

*你的start是我前进的动力*

## Introduction

Leaf 最早期需求是各个业务线的订单ID生成需求。在美团早期，有的业务直接通过DB自增的方式生成ID，有的业务通过redis缓存来生成ID，也有的业务直接用UUID这种方式来生成ID。以上的方式各自有各自的问题，因此我们决定实现一套分布式ID生成服务来满足需求。具体Leaf 设计文档见：[ leaf 美团分布式ID生成服务 ](https://tech.meituan.com/MT_Leaf.html )

目前Leaf覆盖了美团点评公司内部金融、餐饮、外卖、酒店旅游、猫眼电影等众多业务线。在4C8G VM基础上，通过公司RPC方式调用，QPS压测结果近5w/s，TP999 1ms。

<mark>考虑到各个业务应用都调用同一个ID生成服务，对ID生成服务的可靠性，可用性有极高的要求，在美团Leaf的基础上封装了starter，便于将ID生成功能嵌入到各业务应用中，新增支持oracle数据库。</mark>

与[feature/spring-boot-starter](https://github.com/Meituan-Dianping/Leaf/blob/feature/spring-boot-starter/README_CN.md)不同的是，只需yml配置无需注解。新增配置项auto-init-biz-tags支持启动是初始化segment，缩短ID首次生成耗时；新增配置项data-source-name支持多数据源应用使用segment；新增配置项manageable配合base-path支持暴露管理api。

## Quick Start

### leaf-spring-boot-starter

#### 引入依赖

```xml
<dependency>
     <groupId>xyz.hellothomas</groupId>
     <artifactId>leaf-spring-boot-starter</artifactId>
     <version>1.0.2</version>
 </dependency>
```

https://github.com/Meituan-Dianping/Leaf/blob/feature/spring-boot-starter/README_CN.md

#### yml配置

```yml
leaf:
  name: com.sankuai.leaf.opensource.test #leaf服务名，snowflakeId注册zk时使用
  base-path: /leaf #管理api的basePath,默认/leaf
  segment:
    enabled: true #开启使用segmentId，默认false
    data-source-name: dataSource1 #segment使用的数据源，单数据源可不配置
    auto-init-biz-tags: bizTag1,bizTag2 #启动时需自动初始化的bizTag,多个以逗号分隔
    manageable: true #暴露管理api,默认false
  snowflake:
    enable: false #开启使用snowflakeId，默认false
    zk-address: 127.0.0.1:2181 #zk地址
    port: 8080 #服务注册端口
    manageable: true #暴露管理api,默认false
```

#### api使用

```java
@Autowired
private IDGen idGen;

// key: bizTag
idGen.get(key)

// 同时使用segmentId和snowflakeId时需指定类型注入
@Autowired
private SegmentIDGenImpl segmentIdGen;

@Autowired
private SnowflakeIDGenImpl snowflakeIdGen;
```

#### 管理api

http://localhost:8080/leaf/segment/cache

http://localhost:8080/leaf/segment/db

http://localhost:8080/leaf/segment/add-biz-tag?bizTag=test1&maxId=1&step=5&&description=myTest

http://localhost:8080leaf/segment/remove-biz-tag?bizTag=test1

#### 创建数据表

如果使用号段模式，需要建立DB表，脚本在依赖包leaf-core/resources/scripts目录下，mysql -> leaf_alloc-mysql.sql，oracle -> leaf_alloc-oracle.sql

```sql
-- leaf_alloc-mysql.sql
CREATE TABLE LEAF_ALLOC (
  BIZ_TAG VARCHAR(128) NOT NULL DEFAULT '',
  MAX_ID BIGINT NOT NULL DEFAULT '1',
  STEP INT NOT NULL,
  DESCRIPTION VARCHAR(256) DEFAULT NULL,
  UPDATE_TIME DATETIME(3) NOT NULL DEFAULT NOW(3),
  PRIMARY KEY (BIZ_TAG)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_bin COMMENT='LEAF分配表';

-- leaf_alloc-oracle.sql
CREATE TABLE LEAF_ALLOC (
  BIZ_TAG VARCHAR2(128 char) NOT NULL DEFAULT '',
  MAX_ID NUMBER(20,0) NOT NULL DEFAULT '1',
  STEP NUMBER(11,0) NOT NULL,
  DESCRIPTION VARCHAR2(256 char) DEFAULT NULL,
  UPDATE_TIME TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (BIZ_TAG)
);
COMMENT ON TABLE LEAF_ALLOC IS 'LEAF分配表';
```

### Leaf Server

我们提供了一个基于spring boot的HTTP服务来获取ID


#### 配置介绍

Leaf 提供两种生成的ID的方式（号段模式和snowflake模式），你可以同时开启两种方式，也可以指定开启某种方式（默认两种方式为关闭状态）。

| 配置项                          | 含义                                       | 默认值 |
| ------------------------------- | ------------------------------------------ | ------ |
| leaf.name                       | leaf 服务名                                |        |
| leaf.base-path                  | 管理api的basePath                          | /leaf  |
| leaf.segment.enable             | 是否开启号段模式                           | false  |
| leaf.segment.data-source-name   | segment使用的数据源，单数据源可不配置      |        |
| leaf.segment.auto-init-biz-tags | 启动时需自动初始化的bizTag，多个以逗号分隔 |        |
| leaf.segment.manageable         | 是否暴露管理api                            | false  |
| leaf.snowflake.enable           | 是否开启snowflake模式                      | false  |
| leaf.snowflake.zk-address       | snowflake模式下的zk地址                    |        |
| leaf.snowflake.port             | snowflake模式下的服务注册端口              |        |
| leaf.snowflake.manageable       | 是否暴露管理api                            | false  |

#### 号段模式

如果使用号段模式，需要建立DB表，并配置数据源（比如spring.datasource.url，spring.datasource.username，spring.datasource.password）

如果不想使用该模式配置leaf.segment.enable=false即可。

##### 创建数据表

见leaf-spring-boot-starter中创建数据表

##### 配置相关数据项

见leaf-spring-boot-starter中yml配置

#### Snowflake模式

算法取自twitter开源的snowflake算法。

如果不想使用该模式配置leaf.snowflake.enable=false即可。

##### 配置zookeeper地址

见leaf-spring-boot-starter中yml配置
#### 运行Leaf Server

##### 打包服务

```shell
git clone git@gitee.com:hellothomas/Leaf.git
//按照上面指引在工程里面配置好yml
cd leaf
mvn clean install -DskipTests
cd leaf-server
```

##### 运行服务

*注意:首先得先配置好数据库表或者zk地址*
###### mvn方式

```shell
mvn spring-boot:run
```

###### 脚本方式

```shell
sh deploy/run.sh
```
##### 测试

```shell
#segment 
curl http://localhost:8080/api/segment/get/leaf-segment-test
#snowflake
curl http://localhost:8080/api/snowflake/get/test
```

##### 监控页面

号段模式：http://localhost:8080/cache

http://localhost:8080/db

##### 管理页面

号段模式：http://localhost:8080/add-biz-tag?bizTag=test1&maxId=1&step=5&&description=myTest

http://localhost:8080/remove-biz-tag?bizTag=test1

### Leaf Core

当然，为了追求更高的性能，需要通过RPC Server来部署Leaf 服务，那仅需要引入leaf-core的包，把生成ID的API封装到指定的RPC框架中即可。

### 注意事项
注意现在leaf使用snowflake模式的情况下 其获取ip的逻辑直接取首个网卡ip【特别对于会更换ip的服务要注意】避免浪费workId
