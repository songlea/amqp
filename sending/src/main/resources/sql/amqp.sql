-- 用于登录与注册的用户表
CREATE TABLE `amqp`.`amqp_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(32) NOT NULL COMMENT '用户名(唯一)',
  `email` VARCHAR(128) NOT NULL COMMENT '邮箱地址(用来找回密码或登录)',
  `password` VARCHAR(64) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '用户表';