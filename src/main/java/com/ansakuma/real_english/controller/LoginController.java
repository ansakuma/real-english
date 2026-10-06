package com.ansakuma.real_english.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ansakuma.real_english.entity.User;
import com.ansakuma.real_english.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    private final UserRepository userRepository;
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //ログインページを表示する
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    //ログイン処理を行う
    @PostMapping("/login")
    public String login(String username,String password, HttpSession session, Model model) {
        User user = userRepository.findByUsername(username);
        //ユーザー名が存在し、かつ、パスワードが一致している場合
        if (user != null && user.getPassword().equals(password)) {
            //ログインユーザーをセッションに保存する
            session.setAttribute("loginUser", user);
            return "redirect:/";//redirectを使うときはデータを登録したり、ログイン認証したりといった裏側の処理が成功したあと
        }
        //ユーザー名またはパスワードが間違っている場合
        model.addAttribute("error", "ユーザー名またはパスワードが間違っています");
        return "login";
    }

    //ログアウト処理を行う
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();//セッションを無効化する
        return "redirect:/";
    }


    //ユーザー登録ページを表示する
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    //ユーザー登録処理を行う
    @PostMapping("/register")
    public String register(User user, Model model, String passwordConfirm, RedirectAttributes redirectAttributes) {
        //ユーザー名やパスワードがnull、または、空文字の場合
        if (user.getUsername() == null || user.getUsername().isBlank()
            || user.getPassword() == null || user.getPassword().isBlank()) {
        model.addAttribute("error", "ユーザー名とパスワードを入力してください");
        return "register";
        }
        //ユーザー名がすでに使われている場合
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            return "register";
        }
        //パスワードとパスワード確認が一致していない場合
        if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "パスワードと確認用パスワードが一致していません");
            return "register";
        }
        //ユーザーを保存する
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "ユーザー登録が完了しました");
        return "redirect:/";
    }
}
