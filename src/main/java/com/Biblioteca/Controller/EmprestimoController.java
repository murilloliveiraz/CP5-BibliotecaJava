package com.Biblioteca.Controller;


import com.Biblioteca.Model.Usuario;
import com.Biblioteca.Model.Livro;
import com.Biblioteca.Model.Emprestimo;
import com.Biblioteca.Model.StatusLivro;
import com.Biblioteca.Repository.EmprestimoRepository;
import com.Biblioteca.Repository.LivroRepository;
import com.Biblioteca.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarAtivos(Model model) {
        List<Emprestimo> ativos = emprestimoRepository.findByDataDevolucaoRealIsNull();
        model.addAttribute("emprestimos", ativos);
        return "emprestimos/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("emprestimo", new Emprestimo());
        model.addAttribute("livros", livroRepository.findByStatus(StatusLivro.DISPONIVEL));
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "emprestimos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Emprestimo emprestimo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("livros", livroRepository.findByStatus(StatusLivro.DISPONIVEL));
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "emprestimos/form";
        }
        // Busca entidades
        Livro livro = livroRepository.findById(emprestimo.getLivro().getId()).orElseThrow();
        Usuario usuario = usuarioRepository.findById(emprestimo.getUsuario().getId()).orElseThrow();
        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario);
        emprestimo.setDataRetirada(LocalDateTime.now());
        emprestimoRepository.save(emprestimo);
        return "redirect:/emprestimos";
    }

    @PostMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        if (emprestimo.isAtivo()) {
            emprestimo.setDataDevolucaoReal(LocalDateTime.now());
            emprestimoRepository.save(emprestimo);
            // Atualiza status do livro
            emprestimo.getLivro().setStatus(StatusLivro.DISPONIVEL);
            livroRepository.save(emprestimo.getLivro());
        }
        return "redirect:/emprestimos";
    }
}
