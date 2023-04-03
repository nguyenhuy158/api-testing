package com.example.springcommerce.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {
    @Autowired
    private ProductService service;

    @RequestMapping(value = "/products")
    public String showProductList(
            @RequestParam(name = "keyword", required = false) Optional<String> keyword,
            Model model) {
        List<Product> products;

        if (keyword.isPresent()) {
            products = service.search(keyword.get());
        } else {
            products = service.listAll();
        }
        model.addAttribute("listProducts", products);
        return "products";
    }

    @GetMapping("/products/new")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Add New Product");
        return "product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra) {
        service.save(product);
        ra.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Product user = service.get(id);
            model.addAttribute("product", user);
            model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");

            return "product_form";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "The product ID " + id + " has been deleted.");
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }
}
