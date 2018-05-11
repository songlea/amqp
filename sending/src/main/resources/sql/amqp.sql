-- 创建用于登录与注册的用户表
CREATE TABLE `amqp`.`amqp_user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` VARCHAR(32) NOT NULL COMMENT '用户名(唯一)',
  `email` VARCHAR(128) NOT NULL COMMENT '邮箱地址(用来找回密码)',
  `password` VARCHAR(64) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '用户表';

-- 文章表
CREATE TABLE `amqp`.`amqp_article` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(64) NULL COMMENT '文章标题',
  `content` LONGTEXT NULL COMMENT '文章内容',
  `user_id` INT(11) NULL COMMENT '发布文章的userId',
  `create_time` DATETIME NULL COMMENT '发布时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '文章';