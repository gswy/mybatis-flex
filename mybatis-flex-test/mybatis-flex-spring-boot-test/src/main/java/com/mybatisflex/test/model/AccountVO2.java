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

package com.mybatisflex.test.model;

import com.mybatisflex.annotation.As;

/**
 * @author 王帅
 * @since 2023-06-30
 */
public class AccountVO2 extends IdEntity<Long> {

    private Integer age;
    @As("account_name")
    @As("1_account_name")
    private String userName;
    private UserVO4 user;

    @Override
    @As("account_id")
    public void setId(Long id) {
        super.setId(id);
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserVO4 getUser() {
        return user;
    }

    public void setUser(UserVO4 user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AccountVO2{" +
                "id=" + id +
                ", age=" + age +
                ", userName='" + userName + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

}