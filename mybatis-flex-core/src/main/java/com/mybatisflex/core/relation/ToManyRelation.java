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
package com.mybatisflex.core.relation;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.*;

import java.lang.reflect.Field;
import java.util.*;

class ToManyRelation<SelfEntity> extends AbstractRelation<SelfEntity> {

    protected String mapKeyField;
    protected FieldWrapper mapKeyFieldWrapper;
    protected String orderBy;
    protected long limit = 0;
    protected String selfFieldSplitBy;


    public ToManyRelation(String selfField, String targetSchema, String targetTable, String targetField, String valueField,
                          String joinTable, String joinSelfColumn, String joinTargetColumn,
                          String dataSource, Class<SelfEntity> selfEntityClass, Field relationField,
                          String extraCondition, String[] selectColumns) {
        super(selfField, targetSchema, targetTable, targetField, valueField,
            joinTable, joinSelfColumn, joinTargetColumn,
            dataSource, selfEntityClass, relationField,
            extraCondition, selectColumns
        );
    }

    /**
     * 构建查询目标对象的 QueryWrapper
     *
     * @param targetValues 条件的值
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper buildQueryWrapper(Set<Object> targetValues) {
        if (StringUtil.isNotBlank(selfFieldSplitBy) && CollectionUtil.isNotEmpty(targetValues)) {
            Set<Object> newTargetValues = new HashSet<>();
            for (Object targetValue : targetValues) {
                if (targetValue == null) {
                    continue;
                }
                if (!(targetValue instanceof String)) {
                    throw FlexExceptions.wrap("split field only support String type, but current type is: \"" + targetValue.getClass().getName() + "\"");
                }
                String[] splitValues = ((String) targetValue).split(selfFieldSplitBy);
                for (String splitValue : splitValues) {
                    //优化分割后的数据类型(防止在数据库查询时候出现隐式转换)
                    newTargetValues.add(ConvertUtil.convert(splitValue, targetFieldWrapper.getFieldType()));
                }
            }
            targetValues = newTargetValues;
        }
        return super.buildQueryWrapper(targetValues);
    }


    @Override
    public void customizeQueryWrapper(QueryWrapper queryWrapper) {
        if (StringUtil.isNotBlank(orderBy)) {
            queryWrapper.orderBy(orderBy);
        }

        if (limit > 0) {
            queryWrapper.limit(limit);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void join(List<SelfEntity> selfEntities, List<?> targetObjectList, List<Row> mappingRows) {
        selfEntities.forEach(selfEntity -> {
            Object selfValue = selfFieldWrapper.get(selfEntity);
            if (selfValue != null) {
                selfValue = selfValue.toString();
                Set<String> targetMappingValues = new HashSet<>();
                if (mappingRows != null) {
                    for (Row mappingRow : mappingRows) {
                        if (selfValue.equals(String.valueOf(mappingRow.getIgnoreCase(joinSelfColumn)))) {
                            Object joinValue = mappingRow.getIgnoreCase(joinTargetColumn);
                            if (joinValue != null) {
                                targetMappingValues.add(joinValue.toString());
                            }
                        }
                    }
                } else {
                    if (StringUtil.isNotBlank(selfFieldSplitBy)) {
                        String[] splitValues = ((String) selfValue).split(selfFieldSplitBy);
                        targetMappingValues.addAll(Arrays.asList(splitValues));
                    } else {
                        targetMappingValues.add((String) selfValue);
                    }
                }

                if (targetMappingValues.isEmpty()) {
                    return;
                }

                Class<?> fieldType = relationFieldWrapper.getFieldType();
                //map
                if (Map.class.isAssignableFrom(fieldType)) {
                    Class<?> wrapType = getMapWrapType(fieldType);
                    Map map = (Map) ClassUtil.newInstance(wrapType);
                    for (Object targetObject : targetObjectList) {
                        Object targetValue = targetFieldWrapper.get(targetObject);
                        if (targetValue != null && targetMappingValues.contains(targetValue.toString())) {
                            Object keyValue = mapKeyFieldWrapper.get(targetObject);
                            Object needKeyValue = ConvertUtil.convert(keyValue, relationFieldWrapper.getKeyType());
                            map.put(needKeyValue, targetObject);
                        }
                    }
                    relationFieldWrapper.set(map, selfEntity);
                }
                //集合
                else {
                    Class<?> wrapType = MapperUtil.getCollectionWrapType(fieldType);
                    Collection collection = (Collection) ClassUtil.newInstance(wrapType);
                    for (Object targetObject : targetObjectList) {
                        Object targetValue = targetFieldWrapper.get(targetObject);
                        if (targetValue != null && targetMappingValues.contains(targetValue.toString())) {
                            if (onlyQueryValueField) {
                                //仅绑定某个字段
                                collection.add(FieldWrapper.of(targetObject.getClass(), valueField).get(targetObject));
                            } else {
                                collection.add(targetObject);
                            }
                        }
                    }
                    relationFieldWrapper.set(collection, selfEntity);
                }
            }
        });
    }

    public void setMapKeyField(String mapKeyField) {
        this.mapKeyField = mapKeyField;
        if (StringUtil.isNotBlank(mapKeyField)) {
            this.mapKeyFieldWrapper = FieldWrapper.of(targetEntityClass, mapKeyField);
        } else {
            if (Map.class.isAssignableFrom(relationFieldWrapper.getFieldType())) {
                throw FlexExceptions.wrap("Please config mapKeyField for map field: " + relationFieldWrapper.getField());
            }
        }
    }

    public static Class<? extends Map> getMapWrapType(Class<?> type) {
        if (ClassUtil.canInstance(type.getModifiers())) {
            return (Class<? extends Map>) type;
        }

        return HashMap.class;
    }

}
