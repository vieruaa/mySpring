package com.example.mySpring.service;

import com.example.mySpring.models.Image;
import com.example.mySpring.models.Product;
import com.example.mySpring.models.User;
import com.example.mySpring.repositories.ProductRepository;
import com.example.mySpring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public List<Product> listProducts(String title) {
        if(title != null) return productRepository.findByTitle(title);
        return productRepository.findAll();
    }

    public void saveProduct(Principal principal, MultipartFile[] files, Product product) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        Image image;
        for (MultipartFile file: files) {
            image = toImageEntity(file);
            image.setPreviewImage(file == files[0]);
            product.addImageToProduct(image);
        }
        log.info("Saving new product. Title: {}; Author email: {}", product.getTitle(), product.getUser().getEmail());
        Product productFromDB = productRepository.save(product);
        productFromDB.setPreviewImageId(productFromDB.getImages().get(0).getId());
        productRepository.save(product);
    }

    public User getUserByPrincipal(Principal principal) {
        if(principal==null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    private Image toImageEntity(MultipartFile file) throws IOException{
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public Product getProductById(Long id){
        return productRepository.findById(id).orElse(null);
    }

}
