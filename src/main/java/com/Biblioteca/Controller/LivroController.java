package com.Biblioteca.Controller;

import com.Biblioteca.Model.Livro;
import com.Biblioteca.Model.StatusLivro;
import com.Biblioteca.Repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping
    public String listar(Model model) {
        List<Livro> livros = livroRepository.findAll();
        model.addAttribute("livros", livros);
        return "livros/listar";
    }

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        List<Livro> disponiveis = livroRepository.findByStatus(StatusLivro.DISPONIVEL);
        model.addAttribute("livros", disponiveis);
        model.addAttribute("tipo", "disponíveis");
        return "livros/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("livro", new Livro());
        return "livros/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Livro livro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "livros/form";
        }
        livroRepository.save(livro);
        return "redirect:/livros";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        model.addAttribute("livro", livro);
        return "livros/form";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        livroRepository.deleteById(id);
        return "redirect:/livros";
    }
}
