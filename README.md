# 秒杀系统项目介绍

## 一、项目概述
本项目是一个基于 Java 的秒杀系统，使用 Spring Boot 作为基础框架，结合 Redis 缓存、MyBatis 进行数据持久化，通过 RabbitMQ 实现异步消息处理，以应对高并发场景下的秒杀需求。系统提供了用户登录、商品秒杀、订单查询等功能，确保在高并发情况下系统的稳定性和数据的一致性。

## 二、项目架构
### 1. 技术栈
- **后端**：Spring Boot、MyBatis、Redis、RabbitMQ
- **数据库**：MySQL
- **代码生成**：MyBatis-Plus Generator
- **API 文档**：Swagger

### 2. 模块划分
- **controller**：处理前端请求，包括用户、商品、订单等相关接口。
- **service**：实现业务逻辑，如商品秒杀、订单处理等。
- **mapper**：数据库操作，使用 MyBatis 进行数据持久化。
- **redis**：Redis 缓存操作，用于存储商品库存、用户信息等。
- **mq**：消息队列相关，使用 RabbitMQ 实现异步消息处理。
- **exception**：全局异常处理，统一处理业务异常和运行时异常。
- **utils**：工具类，如 MD5 加密、代码生成等。

## 三、秒杀功能实现细节

### 1. 初始化
- 在 `SeckillController` 中实现 `InitializingBean` 接口，在系统启动时将商品库存信息加载到 Redis 中，并使用 `ConcurrentHashMap` 存储每个商品的秒杀状态，避免每次请求都访问 Redis。
```java
@Override
public void afterPropertiesSet() throws Exception {
    List<GoodsDTO> goodsList = seckillGoodsService.getSeckillGoodsList();
    if (goodsList == null) {
        return;
    }
    for (GoodsDTO goods : goodsList) {
        redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
        localOverMap.put(goods.getId(), false);
    }
}
```

### 2. 秒杀请求处理
- **用户验证**：从请求的 Cookie 中获取登录令牌，通过 Redis 验证用户信息。
- **URL 验证**：验证秒杀路径的合法性，防止恶意请求。
- **库存检查**：先从本地内存中检查商品是否已售罄，再通过 Redis 扣减库存。
- **重复购买检查**：检查用户是否已经购买过该商品。
- **消息队列处理**：将秒杀请求封装成消息发送到 RabbitMQ 队列，异步处理订单。
```java
@PostMapping("/{path}/seckill")
@ResponseBody
public BaseResponse<Integer> list(@PathVariable("path") String path, @RequestParam("goodsId") long goodsId, HttpServletRequest request) {
    String loginToken = CookieUtils.readLoginToken(request);
    User user = redisService.get(UserKey.getByName, loginToken, User.class);
    if (user == null) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }
    boolean check = seckillOrderService.checkUrl(user, goodsId, path);
    if (!check) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }
    
    Boolean over = localOverMap.get(goodsId);
    if (over) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    Long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
    if (stock == null) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    if (stock < 0) {
        localOverMap.put(goodsId, true);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    SeckillOrder order = seckillOrderService.getSeckillOrderByUIdAndGId(user.getId(), goodsId);
    if (order == null) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    SeckillMessage mm = new SeckillMessage();
    mm.setUser(user);
    mm.setGoodsId(goodsId);
    mqSender.sendSeckillMessage(mm);
    
    return ResultUtils.success(0);
}
```

### 3. 消息队列处理
- 在 `MQReceiver` 中监听 RabbitMQ 队列，接收到秒杀消息后，检查商品库存和用户是否已购买，若满足条件则插入订单。
```java
@RabbitListener(queues = MQConfig.SECKILL_QUEUE)
public void receive(String message){
    logger.info("receive message:" + message);
    SeckillMessage mm = RedisService.stringToBean(message, SeckillMessage.class);
    User user = mm.getUser();
    long goodsId = mm.getGoodsId();

    GoodsDTO goods = seckillGoodsService.getSeckillGoodsDTOByGoodsId(goodsId);
    Integer stock = goods.getStockCount();
    if(stock <= 0){
        return;
    }
    SeckillOrder order = seckillOrderService.getSeckillOrderByUIdAndGId(user.getId(), goodsId);
    if(order != null){
        return;
    }
    seckillOrderService.insert(user,goods);
}
```

### 4. 订单处理
- 在 `SeckillOrderServiceImpl` 中实现订单插入逻辑，使用 `@Transactional` 注解保证数据的一致性。
```java
@Transactional
@Override
public OrderInfo insert(User user, GoodsDTO goodsDTO) {
    int success = seckillGoodsService.reduceStock(goodsDTO.getId());

    if(success == 1){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(LocalDateTime.now());
        orderInfo.setAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsDTO.getId());
        orderInfo.setGoodsName(goodsDTO.getGoodsName());
        orderInfo.setGoodsPrice(goodsDTO.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderService.addOrder(orderInfo);
        logger.info("orderId -->" +orderId);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsDTO.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderMapper.insertSelective(seckillOrder);
        return orderInfo;
    }else {
        setGoodsOver(goodsDTO.getId());
        return null;
    }
}
```

### 5. 秒杀结果查询
- 用户可以通过 `SeckillController` 中的 `/result` 接口查询秒杀结果，根据用户 ID 和商品 ID 从数据库中获取订单信息。
```java
@GetMapping("/result")
@ResponseBody
public BaseResponse<Long> seckillResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
    String loginToken = CookieUtils.readLoginToken(request);
    User user = redisService.get(UserKey.getByName, loginToken, User.class);
    if (user == null) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }

    long result = seckillOrderService.getSeckillResultByUIdAndGId(user.getId(), goodsId);
    return ResultUtils.success(result);
}
```

## 四、项目启动
### 1. 数据库配置
在 `application.yml` 中配置 MySQL 数据库连接信息。

### 2. Redis 配置
确保 Redis 服务已启动，并在 `application.yml` 中配置 Redis 连接信息。

### 3. RabbitMQ 配置
确保 RabbitMQ 服务已启动，并在 `application.yml` 中配置 RabbitMQ 连接信息。

### 4. 启动项目
运行 `com.dyseckill.MyApplication` 类启动 Spring Boot 项目。

## 六、注意事项
- 本项目为练习项目，在实际生产环境中需要根据具体需求进行优化和扩展。
- 确保数据库、Redis、RabbitMQ 服务正常运行，避免出现连接异常。
- 注意代码中的配置信息，如数据库用户名、密码，Redis 地址等，根据实际情况进行修改。