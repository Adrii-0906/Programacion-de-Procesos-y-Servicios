package com.ejercicio.dual.service;

import com.ejercicio.dual.dto.ProductoDTO;
import com.ejercicio.dual.model.Producto;
import com.ejercicio.dual.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoDTO> findById(Long id) {
        return productoRepository.findById(id)
                .map(this::mapToDTO);
    }

    public ProductoDTO save(ProductoDTO dto) {
        Producto producto = mapToEntity(dto);
        return mapToDTO(productoRepository.save(producto));
    }

    public Optional<ProductoDTO> update(Long id, ProductoDTO dto) {
        if (!productoRepository.existsById(id)) {
            return Optional.empty();
        }
        Producto producto = mapToEntity(dto);
        producto.setId(id);
        return Optional.of(mapToDTO(productoRepository.save(producto)));
    }

    public boolean delete(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductoDTO mapToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        dto.setStock(producto.getStock());
        return dto;
    }

    private Producto mapToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(dto.getCategoria());
        producto.setStock(dto.getStock());
        return producto;
    }
}
