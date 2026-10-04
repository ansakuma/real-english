package com.ansakuma.real_english.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ansakuma.real_english.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findByUsername(String username);//usernameを使ってユーザーを検索するメソッド。Userは戻り値でユーザーのエンティティクラス（設計図）を返す。

}
