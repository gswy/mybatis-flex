/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.annotation;

import java.lang.annotation.*;

/**
 * 多对一映射。
 *
 * @author michael
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RelationManyToOne {

    /**
     * 当前实体类的属性。
     *
     * @return 属性名称
     */
    String selfField();

    /**
     * <p>
     * 目标实体类对应的表的 schema 模式。
     *
     * <p>
     * 如果目标实体类没有使用 {@code @Table(schema = "...")} 指定 schema 时，
     * 需要在这里指定对应表的 schema 值。一般关联数据不是 entity 对象，而是 vo、dto
     * 等需要配置此项。
     *
     * @return schema 名称
     */
    String targetSchema() default "";

    /**
     * <p>
     * 目标实体类对应的表名。
     *
     * <p>
     * 如果目标实体类没有使用 {@code @Table(value = "...")} 指定表名时，
     * 需要在这里指定对应表的表名。一般关联数据不是 entity 对象，而是 vo、dto
     * 等需要配置此项。
     *
     * @return 表名
     */
    String targetTable() default "";

    /**
     * 目标实体类的关联属性。
     *
     * @return 属性名称
	 */
    String targetField() default "";

    /**
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源。
     *
     * @return 数据源
	 */
	String dataSource() default "";

}
