package com.akshy.productservice.service;

import com.akshy.productservice.dto.ProductRequest;
import com.akshy.productservice.dto.ProductResponse;
import com.akshy.productservice.model.Product;
import com.akshy.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder().
                name(productRequest.getName()).
                description(productRequest.getDescription()).
                price(productRequest.getPrice())
                .build();
        productRepository.save(product);

        log.info("Product {} is saved",product.getId());

    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> mapToProductResponse(product)).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .description(product.getDescription())
                .price(product.getPrice())
                .name(product.getName())
                .build();
    }
}
