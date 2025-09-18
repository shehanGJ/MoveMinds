package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.java.moveminds.dto.CategoryDTO;
import com.java.moveminds.dto.requests.FitnessProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramHomeResponse;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.services.FitnessProgramService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class FitnessProgramController {
    private final FitnessProgramService fitnessProgramService;
    Logger log = LoggerFactory.getLogger(FitnessProgramController.class);


    /**
     * Handles HTTP POST requests to create a new fitness program.
     * Consumes multipart/form-data.
     *
     * @param programRequest the request object containing the fitness program details
     * @param files          the list of files to be associated with the fitness program
     * @param principal      the config principal of the authenticated user
     * @return a ResponseEntity containing the created FitnessProgramResponse object if successful,
     * or a BAD_REQUEST status if an error occurs,
     * or an INTERNAL_SERVER_ERROR status if an unexpected error occurs
     */
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<FitnessProgramResponse> createProgram(
            @RequestPart("program") FitnessProgramRequest programRequest,
            @RequestPart("files") List<MultipartFile> files,
            Principal principal) {
        log.info("Received request with program: {}", programRequest);
        log.info("Received files: {}", files.size());
        files.forEach(file -> log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize()));
        try {
            FitnessProgramResponse response = fitnessProgramService.addFitnessProgram(principal, programRequest, files);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error while creating program", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles HTTP GET requests to retrieve fitness programs with optional filtering by category, attribute, or attribute value.
     *
     * @param page             the page number to retrieve, defaults to 0 if not provided
     * @param size             the number of items per page, defaults to 5 if not provided
     * @param sort             the sorting criteria in the format "field,direction" (e.g., "name,asc")
     * @param categoryId       the ID of the category to filter programs by, can be null
     * @param attributeId      the ID of the attribute to filter programs by, can be null
     * @param attributeValueId the ID of the attribute value to filter programs by, can be null
     * @return a ResponseEntity containing a Page of FitnessProgramHomeResponse objects
     */
    @GetMapping()
    public ResponseEntity<Page<FitnessProgramHomeResponse>> getPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "attributeId", required = false) Integer attributeId,
            @RequestParam(value = "attributeValueId", required = false) Integer attributeValueId) {

        // If categoryId is -1, set it to null to indicate no category filter
        if (Objects.equals(categoryId, -1)) {
            categoryId = null;
        }

        // Create a Pageable object based on the provided page, size, and sort parameters
        Pageable pageable = createPageable(page, size, sort);

        Page<FitnessProgramHomeResponse> programs;
        if (categoryId != null && attributeId == null && attributeValueId == null) {
            // Fetch programs by category only
            programs = fitnessProgramService.getAllFitnessProgramsByCategoryId(categoryId, pageable);
        } else if (attributeId != null && attributeValueId == null) {
            // Fetch programs by attribute only
            programs = fitnessProgramService.getAllFitnessProgramsByAttributeId(attributeId, pageable);
        } else if (attributeValueId != null) {
            // Fetch programs by attribute value
            programs = fitnessProgramService.getAllFitnessProgramsByAttributeValue(attributeValueId, pageable);
        } else {
            // Fetch all programs if no filter is applied
            programs = fitnessProgramService.getAllFitnessPrograms(pageable);
        }

        return ResponseEntity.ok(programs);
    }

    /**
     * Handles HTTP GET requests to retrieve a fitness program by its ID.
     *
     * @param id the ID of the fitness program to retrieve
     * @return a ResponseEntity containing the FitnessProgramResponse object if found,
     * or a BAD_REQUEST status if the ID is null
     */
    @GetMapping("/{id}")
    public ResponseEntity<FitnessProgramResponse> getProgramById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        FitnessProgramResponse programResponse = fitnessProgramService.getFitnessProgram(id);
        return ResponseEntity.ok(programResponse);
    }

    /**
     * Handles HTTP GET requests to retrieve all categories with their attributes and values.
     *
     * @return a ResponseEntity containing a list of CategoryDTO objects
     */
    @GetMapping("/with-attributes")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesWithAttributesAndValues() {
        List<CategoryDTO> categories = fitnessProgramService.getAllCategoriesWithAttributesAndValues();
        return ResponseEntity.ok(categories);
    }

    /**
     * Handles HTTP GET requests to retrieve the fitness programs created by the authenticated user.
     *
     * @param page      the page number to retrieve, defaults to 0 if not provided
     * @param size      the number of items per page, defaults to 5 if not provided
     * @param sort      the sorting criteria in the format "field,direction" (e.g., "name,asc"), can be null
     * @param principal the config principal of the authenticated user
     * @return a ResponseEntity containing a Page of FitnessProgramListResponse objects
     */
    @GetMapping("/my-programs")
    public ResponseEntity<Page<FitnessProgramListResponse>> getMyPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", required = false) String sort,
            Principal principal) {
        Pageable pageable = createPageable(page, size, sort);
        Page<FitnessProgramListResponse> programs = fitnessProgramService.getMyFitnessPrograms(principal, pageable);
        return ResponseEntity.ok(programs);
    }


    /**
     * Handles HTTP PUT requests to update an existing fitness program.
     *
     * @param id             the ID of the fitness program to update
     * @param programRequest the request object containing the updated program details
     * @param files          the list of new files to be associated with the program, can be null
     * @param removedImages  the list of image filenames to be removed from the program, can be null
     * @return a ResponseEntity containing the updated FitnessProgramResponse object if successful,
     * or a BAD_REQUEST status if the ID is null or an error occurs,
     * or an INTERNAL_SERVER_ERROR status if an unexpected error occurs
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<FitnessProgramResponse> updateProgram(
            @PathVariable Integer id,
            @RequestPart("program") FitnessProgramRequest programRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "removedImages", required = false) List<String> removedImages) {
        log.info("Updating program with id: {}", id);
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            FitnessProgramResponse response = fitnessProgramService.updateFitnessProgram(id, programRequest, files, removedImages);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error while updating program", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles HTTP DELETE requests to delete a fitness program by its ID.
     *
     * @param id the ID of the fitness program to delete
     * @param principal the config principal of the authenticated user
     * @return a ResponseEntity with status 204 No Content if the deletion is successful
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProgram(@PathVariable("id") Integer id, Principal principal) throws IOException {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        fitnessProgramService.deleteFitnessProgram(id, principal);
        return ResponseEntity.noContent().build(); // Status 204 No Content
    }


    /**
     * Creates a Pageable object based on the provided page, size, and sort parameters.
     *
     * @param page the page number to retrieve
     * @param size the number of items per page
     * @param sort the sorting criteria in the format "field,direction" (e.g., "name,asc"), can be null or empty
     * @return a Pageable object configured with the specified page, size, and sort parameters
     */
    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String sortField = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                return PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                return PageRequest.of(page, size);
            }
        } else {
            return PageRequest.of(page, size, Sort.unsorted());
        }
    }

}
