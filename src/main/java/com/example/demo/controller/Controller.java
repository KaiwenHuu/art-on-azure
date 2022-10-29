package com.example.demo.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.cacheservice.CacheService;
import com.example.demo.entity.Description;
import com.example.demo.entity.jdbc.Template;
import com.example.demo.entity.jparepository.DescriptionJpaRepository;
import com.example.demo.keyvaultservice.KeyVaultService;
import com.example.demo.storageservice.StorageService;

@RestController
public class Controller {

	@Autowired
	private Template template;

	@Autowired
	private DescriptionJpaRepository descriptionRepository;

	private StorageService storageService;

	private KeyVaultService keyVaultService = new KeyVaultService();

	private CacheService cacheService;

	@RequestMapping("/")
	public String home() {
		return "Hello from Azure App Service in the staging slot! We just implemented Cache Service!";
	}

	@GetMapping("/posts")
	public ModelAndView getPosts(ModelMap model) {
		ModelAndView modelAndView = new ModelAndView();
		List<Description> descriptions = descriptionRepository.findAll();

		model.addAttribute("descriptions", descriptions);
		modelAndView.setViewName("posts");
		return modelAndView;
	}

	// @GetMapping("/posts")
	// public ModelAndView getPostsFromCache(ModelMap model) {
	// 	ModelAndView modelAndView = new ModelAndView();
	// 	String cacheKey = keyVaultService.getSecret("cacheKey");
	// 	cacheService = new CacheService(cacheKey);
	// 	List<Object> descriptions = cacheService.getFromCache("*");

	// 	model.addAttribute("descriptions", descriptions);
	// 	modelAndView.setViewName("posts");
	// 	return modelAndView;
	// }

	@RequestMapping("/new-art")
	public ModelAndView addArtPage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addArt");
		return modelAndView;
	}

	@RequestMapping("/edit-post")
	public ModelAndView editPostPage(@RequestParam Long id, ModelMap model) {
		ModelAndView modelAndView = new ModelAndView();
		Description description = descriptionRepository.findById(id).get();
		model.addAttribute("description", description);
		modelAndView.setViewName("editPost");
		return modelAndView;
	}

	@RequestMapping("/errormessage")
	public ModelAndView errorPage(@RequestParam("message") String message, ModelMap model) {
		ModelAndView modelAndView = new ModelAndView();
		model.addAttribute("message", message);
		modelAndView.setViewName("error");
		return modelAndView;
	}

	@PostMapping("/posts")
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("caption") String caption,
			@RequestParam("location") String location, ModelMap model, RedirectAttributes redirectAttributes)
			throws IOException {

		String fileName = file.getOriginalFilename();
		Description description = new Description(caption, fileName, location);
		try {
			List<Long> ids = template.getIds("descriptions");
			Long id = findFirstMissingPositive(ids);
			description.setId(id);
			descriptionRepository.save(description);
			try {
				String sastoken = keyVaultService.getSecret("storagesastoken");
				String cacheKey = keyVaultService.getSecret("cacheKey");
				try {
					cacheService = new CacheService(cacheKey);
					cacheService.insertCache(id.toString(), description);
					try {
						storageService = new StorageService(sastoken);
						storageService.uploadFile(file, id);
					} catch (Exception e) {
						ModelAndView modelAndView = new ModelAndView();
						model.addAttribute("message", "could not access storage service" + e.getMessage());
						modelAndView.setViewName("error");
						return modelAndView;
					}
				} catch (Exception e) {
					ModelAndView modelAndView = new ModelAndView();
					model.addAttribute("message", "could not access cache" + e.getMessage());
					modelAndView.setViewName("error");
					return modelAndView;
				}
			} catch (Exception e) {
				ModelAndView modelAndView = new ModelAndView();
				model.addAttribute("message", "could not access keyvault" + e.getMessage());
				modelAndView.setViewName("error");
				return modelAndView;
			}
		} catch (Exception e) {
			ModelAndView modelAndView = new ModelAndView();
			model.addAttribute("message", "could not access database" + e.getMessage());
			modelAndView.setViewName("error");
			return modelAndView;
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addArtResult");
		return modelAndView;
	}

	@DeleteMapping("posts/{id}")
	public ResponseEntity<Object> deletePostById(@PathVariable Long id) throws IOException {
		try {
			descriptionRepository.deleteById(id);
			try {
				String sastoken = keyVaultService.getSecret("storagesastoken");
				String cacheKey = keyVaultService.getSecret("cacheKey");
				try {
					storageService = new StorageService(sastoken);
					storageService.deleteFile(id);
					try {
						cacheService = new CacheService(cacheKey);
						cacheService.deleteFromCacheById(id.toString());
					} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
					}
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping("posts/{id}")
	public ModelAndView updatePost(@PathVariable Long id, @RequestParam("file") MultipartFile file,
			@RequestParam("caption") String caption, @RequestParam("location") String location, ModelMap model,
			RedirectAttributes redirectAttributes) throws IOException {
		try {
			descriptionRepository.deleteById(id);
			String fileName = file.getOriginalFilename();
			Description description = new Description(caption, fileName, location);
			description.setId(id);
			descriptionRepository.save(description);
			try {
				String sastoken = keyVaultService.getSecret("storagesastoken");
				String cacheKey = keyVaultService.getSecret("cacheKey");

				try {
					storageService = new StorageService(sastoken);
					storageService.deleteFile(id);
					storageService.uploadFile(file, id);
					try {
						cacheService = new CacheService(cacheKey);
						cacheService.deleteFromCacheById(id.toString());
						cacheService.insertCache(id.toString(), description);
					} catch (Exception e) {
						ModelAndView modelAndView = new ModelAndView();
						model.addAttribute("message", "could not access cache" + e.getMessage());
						modelAndView.setViewName("error");
						return modelAndView;
					}
				} catch (Exception e) {
					ModelAndView modelAndView = new ModelAndView();
					model.addAttribute("message", "could not access storage service" + e.getMessage());
					modelAndView.setViewName("error");
					return modelAndView;
				}
			} catch (Exception e) {
				ModelAndView modelAndView = new ModelAndView();
				model.addAttribute("message", "could not access keyvault" + e.getMessage());
				modelAndView.setViewName("error");
				return modelAndView;
			}
		} catch (Exception e) {
			ModelAndView modelAndView = new ModelAndView();
			model.addAttribute("message", "could not access data base" + e.getMessage());
			modelAndView.setViewName("error");
			return modelAndView;
		}
		ModelAndView modelAndView = new ModelAndView();
		List<Description> descriptions = descriptionRepository.findAll();
		model.addAttribute("descriptions", descriptions);
		modelAndView.setViewName("posts");
		return modelAndView;
	}

	private Long findFirstMissingPositive(List<Long> nums) {
		Set<Long> set = new HashSet<Long>(nums);
		for (Long i = 1l; i <= nums.size(); i++) {
			if (!set.contains(i)) {
				return i;
			}
		}
		return (long) (nums.size() + 1);
	}

}
