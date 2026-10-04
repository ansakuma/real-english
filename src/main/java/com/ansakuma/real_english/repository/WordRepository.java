package com.ansakuma.real_english.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ansakuma.real_english.entity.User;
import com.ansakuma.real_english.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer>{
    List<Word> findAllByOrderByCreatedAtDesc();//createdAtを使って単語を降順に並べ替えるメソッド。未来（大きい数字）の日付が先に来る。
    List<Word> findByUserOrderByCreatedAtDesc(User user);//userを使って単語を降順に並べ替えるメソッド。
}
    

