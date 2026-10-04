package com.ansakuma.real_english.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ansakuma.real_english.RealEnglishApplication;
import com.ansakuma.real_english.entity.User;
import com.ansakuma.real_english.entity.Word;
import com.ansakuma.real_english.repository.WordRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class WordController {
    private final RealEnglishApplication realEnglishApplication;
    private final LoginController loginController;
    private final WordRepository wordRepository;//privateはクラス内でしかアクセスできない。finalは変数の値を変更できない。ここでは変数を宣言している。
    //コンストラクタインジェクション。WordRepositoryを注入。
    public WordController(WordRepository wordRepository, LoginController loginController, RealEnglishApplication realEnglishApplication) {
        this.wordRepository = wordRepository;
        this.loginController = loginController;
        this.realEnglishApplication = realEnglishApplication;
    }
    //単語一覧ページを表示する
    @GetMapping("/")
    public String list(Model model) {//Modelはデータをhtmlに渡すためのクラス。
        //createdAtを使って単語を降順に並べ替える。
        List<Word> words = wordRepository.findAllByOrderByCreatedAtDesc();//ordRepositoryクラスのfindAllByOrderByCreatedAtDescメソッドを呼び出している。
        model.addAttribute("words", words);
        return "index";//index.htmlを返す。
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        //ログインユーザーが存在しない場合はログインページにリダイレクトする
        if (loginUser == null) {
            return "redirect:/login";
        }
        //ログインユーザーの単語を日付順に並べ替えて取得する
        List<Word> words = wordRepository.findByUserOrderByCreatedAtDesc(loginUser);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("words", words);
        return "mypage";
    }

    @GetMapping("/words/new")
    public String newWordForm(HttpSession session, Model model) {
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        //ログインユーザーが存在しない場合はログインページにリダイレクトする
        if (loginUser == null) {
            return "redirect:/login";
        }
        //新しい単語を作成する
        model.addAttribute("word", new Word());//new Word()は、Wordクラスのインスタンスを一つ作成する。
        return "word-form";
    }

    //単語を追加する
    @PostMapping("/words")
    public String createWord(Word word, HttpSession session, RedirectAttributes redirectAttributes) {//RedirectAttributesはリダイレクト後にメッセージを表示するためのクラス。
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        //ログインユーザーが存在しない場合はログインページにリダイレクトする
        if (loginUser == null) {
            return "redirect:/login";
        }
        //ログインユーザーを単語に設定する
        word.setUser(loginUser);
        //単語を保存する
        wordRepository.save(word);
        redirectAttributes.addFlashAttribute("message", "追加しました！");//addFlashAttributeは次の画面表示の一回だけ残る、再読み込みでは消える。
        return "redirect:/mypage";
    }
    //単語を削除する
    @PostMapping("/words/{id}/delete")
    public String deleteWord(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {//@PathVariableはURLの変数を取得するためのアノテーション。
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        //単語を取得する
        Word word = wordRepository.findById(id).orElse(null);
        //単語が存在して、かつ、ログインユーザーのidが単語の所有者のidと一致している場合は単語を削除する
        if (word != null && word.getUser().getId().equals(loginUser.getId())) {
            wordRepository.delete(word);
            redirectAttributes.addFlashAttribute("message", "削除しました！");
        }
        return "redirect:/mypage";
    }

    @GetMapping("/words/{id}/edit")
    public String edit(@PathVariable Integer id, HttpSession session, Model model) {
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        //ログインユーザーが存在しない場合はログインページにリダイレクトする
        if (loginUser == null) {
            return "redirect:/login";
        }
        //単語を取得する
        Word word = wordRepository.findById(id).orElse(null);
        //単語が存在して、かつ、ログインユーザーのidが単語の所有者のidと一致している場合は単語を編集する
        if (word != null && word.getUser().getId().equals(loginUser.getId())) {
            model.addAttribute("word", word);
            return "word-form";
        }
        return "redirect:/mypage";
    }

    @PostMapping("/words/{id}/edit")
    public String updateWord(@PathVariable Integer id, Word form, HttpSession session, RedirectAttributes redirectAttributes) {
        //ログインユーザーを取得する
        User loginUser = (User) session.getAttribute("loginUser");
        //ログインユーザーが存在しない場合はログインページにリダイレクトする
        if (loginUser == null) {
            return "redirect:/login";
        }
        //単語を取得する
        Word word = wordRepository.findById(id).orElse(null);
        //単語が存在して、かつ、ログインユーザーのidが単語の所有者のidと一致している場合は単語を編集する
        if (word != null && word.getUser().getId().equals(loginUser.getId())) {
            //単語を更新する
            word.setEnglish(form.getEnglish());
            word.setMeaning(form.getMeaning());
            word.setComment(form.getComment());
            word.setCategory(form.getCategory());
            //単語を保存する
            wordRepository.save(word);
            redirectAttributes.addFlashAttribute("message", "編集しました！");
        }
        return "redirect:/mypage";
    }
    



}
