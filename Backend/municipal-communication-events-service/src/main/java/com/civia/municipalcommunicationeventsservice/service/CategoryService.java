package com.civia.municipalcommunicationeventsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Category;
import com.civia.municipalcommunicationeventsservice.domain.repository.CategoryRepository;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;
import com.civia.municipalcommunicationeventsservice.web.dto.CategoryRequest;

@Service
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public Category findById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
	}

	@Transactional
	public Category create(CategoryRequest request) {
		Category category = Category.builder()
				.name(request.name())
				.description(request.description())
				.active(request.active() == null || request.active())
				.build();
		return categoryRepository.save(category);
	}

	@Transactional
	public Category update(Long id, CategoryRequest request) {
		Category category = findById(id);
		category.setName(request.name());
		category.setDescription(request.description());
		category.setActive(request.active() == null || request.active());
		return categoryRepository.save(category);
	}

	@Transactional
	public void delete(Long id) {
		categoryRepository.delete(findById(id));
	}
}
