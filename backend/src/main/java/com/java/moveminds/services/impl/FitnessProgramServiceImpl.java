package com.java.moveminds.services.impl;

import com.java.moveminds.entities.*;
import com.java.moveminds.exceptions.*;
import com.java.moveminds.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.java.moveminds.dto.AttributeDTO;
import com.java.moveminds.dto.AttributeValueDTO;
import com.java.moveminds.dto.CategoryDTO;
import com.java.moveminds.dto.requests.FitnessProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramHomeResponse;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.services.FitnessProgramService;
import com.java.moveminds.services.ImageUploadService;
import com.java.moveminds.services.LogService;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FitnessProgramServiceImpl implements FitnessProgramService {

    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final CategoryEntityRepository categoryRepository;
    private final UserEntityRepository userRepository;
    private final LocationEntityRepository locationRepository;
    private final ProgramImageEntityRepository programImageRepository;
    private final ImageUploadService imageUploadService;
    private final ProgramAttributeEntityRepository programAttributeRepository;
    private final AttributeValueEntityRepository attributeValueRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;

    /**
     * Adds a new fitness program based on the provided request and files.
     *
     * @param principal             the config principal of the authenticated user
     * @param fitnessProgramRequest the request object containing the details of the fitness program to be added
     * @param files                 the list of files to be associated with the fitness program, can be null
     * @return a FitnessProgramResponse object containing the ID of the newly created fitness program
     * @throws IOException                     if an I/O error occurs during file upload
     * @throws ProgramAlreadyExistsException   if a fitness program with the same name already exists
     * @throws IllegalArgumentException        if the program is neither online nor offline, or both
     * @throws CategoryNotFoundException       if the specified category is not found
     * @throws UserNotFoundException           if the authenticated user is not found
     * @throws LocationNotFoundException       if the specified location is not found
     * @throws AttributeValueNotFoundException if a specified attribute value is not found
     */
    @Override
    @Transactional
    public FitnessProgramResponse addFitnessProgram(Principal principal, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files) throws IOException {

        // Validate user role - only instructors and admins can create programs
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (user.getRole() != com.java.moveminds.enums.Roles.INSTRUCTOR && user.getRole() != com.java.moveminds.enums.Roles.ADMIN) {
            throw new UnauthorizedException("Only instructors and admins can create programs");
        }

        // Check if program with the same name already exists
        Optional<FitnessProgramEntity> existingProgram = fitnessProgramRepository.findByName(fitnessProgramRequest.getName());
        if (existingProgram.isPresent()) {
            throw new ProgramAlreadyExistsException("A program with the name '" + fitnessProgramRequest.getName() + "' already exists.");
        }

        if ((fitnessProgramRequest.getLocationId() == null && fitnessProgramRequest.getYoutubeUrl() == null) ||
                (fitnessProgramRequest.getLocationId() != null && fitnessProgramRequest.getYoutubeUrl() != null)) {
            throw new IllegalArgumentException("The program must be either online or offline, but not both.");
        }

        // Create program
        FitnessProgramEntity fitnessProgramEntity = new FitnessProgramEntity();
        fitnessProgramEntity.setDuration(fitnessProgramRequest.getDuration());
        fitnessProgramEntity.setName(fitnessProgramRequest.getName());
        fitnessProgramEntity.setPrice(fitnessProgramRequest.getPrice());
        fitnessProgramEntity.setDifficultyLevel(fitnessProgramRequest.getDifficultyLevel());
        fitnessProgramEntity.setDescription(fitnessProgramRequest.getDescription());
        fitnessProgramEntity.setYoutubeUrl(fitnessProgramRequest.getYoutubeUrl());

        // Category, user, location
        CategoryEntity category = categoryRepository.findById(fitnessProgramRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        fitnessProgramEntity.setCategory(category);

        fitnessProgramEntity.setUser(user);

        if (fitnessProgramRequest.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(fitnessProgramRequest.getLocationId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found"));
            fitnessProgramEntity.setLocation(location);
        }

        FitnessProgramEntity savedProgram = fitnessProgramRepository.saveAndFlush(fitnessProgramEntity);

        // Images
        if (files != null && !files.isEmpty()) {
            List<ProgramImageEntity> programImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = imageUploadService.uploadImage(file);
                String imageUrl = "/uploads/" + fileName;

                ProgramImageEntity programImage = new ProgramImageEntity();
                programImage.setFitnessProgram(savedProgram);
                programImage.setImageUrl(imageUrl);
                programImages.add(programImage);
                programImageRepository.save(programImage);
            }
            savedProgram.setProgramImages(programImages);
        }

        // Specific attributes
        if (fitnessProgramRequest.getSpecificAttributes() != null && !fitnessProgramRequest.getSpecificAttributes().isEmpty()) {
            List<ProgramAttributeEntity> programAttributes = new ArrayList<>();
            for (FitnessProgramRequest.SpecificAttribute attribute : fitnessProgramRequest.getSpecificAttributes()) {
                ProgramAttributeEntity programAttributeEntity = new ProgramAttributeEntity();

                AttributeValueEntity attributeValueEntity = attributeValueRepository.findById(attribute.getAttributeValue())
                        .orElseThrow(() -> new AttributeValueNotFoundException("Attribute value not found"));

                programAttributeEntity.setFitnessProgram(savedProgram);
                programAttributeEntity.setAttributeValue(attributeValueEntity);

                programAttributes.add(programAttributeEntity);
                programAttributeRepository.save(programAttributeEntity);
            }
            savedProgram.setProgramAttributes(programAttributes);
        }

        logService.log(principal, "Adding a fitness program");

        return new FitnessProgramResponse(savedProgram.getId());
    }

    /**
     * Retrieves the fitness programs created by the authenticated user.
     *
     * @param principal the config principal of the authenticated user
     * @param pageable  the pagination information
     * @return a Page of FitnessProgramListResponse objects
     * @throws UserNotFoundException if the authenticated user is not found
     */
    @Override
    @Transactional
    public Page<FitnessProgramListResponse> getMyFitnessPrograms(Principal principal, Pageable pageable) {
        Page<FitnessProgramEntity> programs;
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        programs = fitnessProgramRepository.findAllByUserId(user.getId(), pageable);

        logService.log(principal, "Overview of my fitness programs");

        return programs.map(this::getFitnessProgramListResponse);
    }

    /**
     * Retrieves all active fitness programs with pagination.
     * Only returns programs that have been activated by admin.
     *
     * @param pageable the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    @Transactional
    public Page<FitnessProgramHomeResponse> getAllFitnessPrograms(Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findByIsActive(true, pageable);
        logService.log(null, "Overview of all active fitness programs");
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves a specific fitness program by its ID.
     *
     * @param id the ID of the fitness program to retrieve
     * @return a FitnessProgramResponse object containing the details of the fitness program
     * @throws ProgramNotFoundException if the fitness program with the specified ID is not found
     */
    @Override
    public FitnessProgramResponse getFitnessProgram(Integer id) {
        FitnessProgramEntity programEntity = fitnessProgramRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program with ID " + id + " not found."));

        // Create a new FitnessProgramResponse
        FitnessProgramResponse response = new FitnessProgramResponse();

        // Set the fields in the response
        response.setId(programEntity.getId());
        response.setName(programEntity.getName());
        response.setDescription(programEntity.getDescription());
        response.setDuration(programEntity.getDuration());
        response.setPrice(programEntity.getPrice());
        response.setDifficultyLevel(programEntity.getDifficultyLevel());
        response.setYoutubeUrl(programEntity.getYoutubeUrl());
        response.setInstructorName(this.generateInstructorName(programEntity.getUser()));
        response.setInstructorId(programEntity.getUser().getId());

        // Map the location
        if (programEntity.getLocation() != null) {
            response.setLocationId(programEntity.getLocation().getId());
            response.setLocationName(programEntity.getLocation().getName());
        }

        // Map the category
        if (programEntity.getCategory() != null) {
            response.setCategoryId(programEntity.getCategory().getId());
            response.setCategoryName(programEntity.getCategory().getName());
        }

        // Map the specific attributes
        List<AttributeDTO> specificAttributes = programEntity
                .getProgramAttributes()
                .stream()
                .map(this::getAttributeDTO)
                .collect(Collectors.toList());
        response.setSpecificAttributes(specificAttributes);

        List<String> imageUrls = programEntity
                .getProgramImages()
                .stream()
                .map(ProgramImageEntity::getImageUrl)
                .collect(Collectors.toList());
        response.setImages(imageUrls);

        logService.log(null, "View fitness programs with ID " + id);

        return response;
    }

    /**
     * Updates an existing fitness program based on the provided request and files.
     *
     * @param programId             the ID of the fitness program to update
     * @param fitnessProgramRequest the request object containing the updated details of the fitness program
     * @param files                 the list of new files to be associated with the fitness program, can be null
     * @param removedImages         the list of image filenames to be removed from the fitness program, can be null
     * @return a FitnessProgramResponse object containing the ID of the updated fitness program
     * @throws IOException                     if an I/O error occurs during file upload or deletion
     * @throws ProgramNotFoundException        if the fitness program with the specified ID is not found
     * @throws CategoryNotFoundException       if the specified category is not found
     * @throws LocationNotFoundException       if the specified location is not found
     * @throws AttributeValueNotFoundException if a specified attribute value is not found
     * @throws ImageUploadException            if an error occurs during image upload
     */
    @Override
    @Transactional
    public FitnessProgramResponse updateFitnessProgram(Integer programId, FitnessProgramRequest fitnessProgramRequest, List<MultipartFile> files, List<String> removedImages) throws IOException {
        FitnessProgramEntity fitnessProgramEntity = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program with ID " + programId + " not found."));

        // Update basic program details
        fitnessProgramEntity.setName(fitnessProgramRequest.getName());
        fitnessProgramEntity.setDescription(fitnessProgramRequest.getDescription());
        fitnessProgramEntity.setDifficultyLevel(fitnessProgramRequest.getDifficultyLevel());
        fitnessProgramEntity.setDuration(fitnessProgramRequest.getDuration());
        fitnessProgramEntity.setPrice(fitnessProgramRequest.getPrice());
        fitnessProgramEntity.setYoutubeUrl(fitnessProgramRequest.getYoutubeUrl());

        // Update category
        if (fitnessProgramRequest.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(fitnessProgramRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            fitnessProgramEntity.setCategory(category);
        }

        // Update location
        if (fitnessProgramRequest.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(fitnessProgramRequest.getLocationId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found"));
            fitnessProgramEntity.setLocation(location);
        } else {
            fitnessProgramEntity.setLocation(null);
        }

        // Image removals
        if (removedImages != null && !removedImages.isEmpty()) {
            for (String imageUrl : removedImages) {
                ProgramImageEntity imageEntity = programImageRepository.findByImageUrl(imageUrl)
                        .orElseThrow(ImageUploadException::new);
                programImageRepository.delete(imageEntity);
                try {
                    this.imageUploadService.deleteImageFile(imageUrl);
                } catch (IOException ignored) {
                }
            }
        }

        // New images
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = imageUploadService.uploadImage(file);
                String imageUrl = "/uploads/" + fileName;

                ProgramImageEntity programImage = new ProgramImageEntity();
                programImage.setFitnessProgram(fitnessProgramEntity);
                programImage.setImageUrl(imageUrl);
                programImageRepository.saveAndFlush(programImage);
            }
        }

        // Update attributes - modify if exists, add new if not
        List<ProgramAttributeEntity> currentAttributes = fitnessProgramEntity.getProgramAttributes();
        List<ProgramAttributeEntity> updatedAttributes = new ArrayList<>();

        for (FitnessProgramRequest.SpecificAttribute attribute : fitnessProgramRequest.getSpecificAttributes()) {
            AttributeValueEntity attributeValueEntity = attributeValueRepository.findById(attribute.getAttributeValue())
                    .orElseThrow(() -> new AttributeValueNotFoundException("Attribute value not found"));

            Optional<ProgramAttributeEntity> existingAttribute = currentAttributes.stream()
                    .filter(attr -> attr.getAttributeValue().getAttribute().getId().equals(attribute.getAttributeName()))
                    .findFirst();

            if (existingAttribute.isPresent()) {
                // If attribute exists, update its value
                ProgramAttributeEntity existingAttr = existingAttribute.get();
                existingAttr.setAttributeValue(attributeValueEntity);
                updatedAttributes.add(existingAttr);
            } else {
                // If attribute doesn't exist, create a new one
                ProgramAttributeEntity newAttribute = new ProgramAttributeEntity();
                newAttribute.setFitnessProgram(fitnessProgramEntity);
                newAttribute.setAttributeValue(attributeValueEntity);
                updatedAttributes.add(newAttribute);
            }
        }

        // Remove attributes that were not part of the update request
        List<ProgramAttributeEntity> attributesToRemove = currentAttributes.stream()
                .filter(attr -> updatedAttributes.stream().noneMatch(updated -> updated.getAttributeValue().getAttribute().getId().equals(attr.getAttributeValue().getAttribute().getId())))
                .collect(Collectors.toList());

        programAttributeRepository.deleteAll(attributesToRemove);

        // Set updated attributes
        fitnessProgramEntity.setProgramAttributes(updatedAttributes);

        logService.log(null, "Updating fitness programs with ID " + programId);

        fitnessProgramRepository.saveAndFlush(fitnessProgramEntity);

        return new FitnessProgramResponse(fitnessProgramEntity.getId());
    }

    /**
     * Retrieves all fitness programs filtered by a specific attribute value with pagination.
     *
     * @param attributeValueId the ID of the attribute value to filter by
     * @param pageable         the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeValue(Integer attributeValueId, Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findDistinctByProgramAttributes_AttributeValue_Id(attributeValueId, pageable);
        logService.log(null, "View fitness programs with attribute values with ID " + attributeValueId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all fitness programs filtered by a specific attribute ID with pagination.
     *
     * @param attributeId the ID of the attribute to filter by
     * @param pageable    the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByAttributeId(Integer attributeId, Pageable pageable) {
        List<AttributeValueEntity> attributeValues = attributeValueRepository.findByAttributeId(attributeId);
        List<Integer> attributeValueIds = attributeValues
                .stream()
                .map(AttributeValueEntity::getId)
                .collect(Collectors.toList());
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findDistinctByProgramAttributes_AttributeValue_IdIn(attributeValueIds, pageable);
        logService.log(null, "View fitness program are attribute are their ID " + attributeId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all fitness programs filtered by a specific category ID with pagination.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable   the pagination information
     * @return a Page of FitnessProgramHomeResponse objects
     */
    @Override
    public Page<FitnessProgramHomeResponse> getAllFitnessProgramsByCategoryId(Integer categoryId, Pageable pageable) {
        Page<FitnessProgramEntity> programs = fitnessProgramRepository.findAllByCategoryId(categoryId, pageable);
        logService.log(null, "View fitness programs by category with ID " + categoryId);
        return programs.map(this::getFitnessProgramHomeResponse);
    }

    /**
     * Retrieves all categories with their associated attributes and attribute values.
     * Only categories with non-empty attributes are included in the result.
     *
     * @return a list of CategoryDTO objects with their attributes and attribute values
     */
    @Override
    public List<CategoryDTO> getAllCategoriesWithAttributesAndValues() {
        List<CategoryEntity> categories = categoryRepository.findAllWithProgramsAndAttributesAndValues();
        logService.log(null, "Overview of all categories with attributes and values");

        return categories.stream()
                .map(this::convertToCategoryDTO)
                .filter(categoryDTO -> !categoryDTO.getAttributes().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFitnessProgram(Integer programId, Principal principal) throws IOException {
        FitnessProgramEntity program = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program with ID " + programId + " not found."));

        UserEntity currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!program.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to delete this program.");
        }

        List<ProgramImageEntity> programImages = program.getProgramImages();

        for (ProgramImageEntity imageEntity : programImages) {
            imageUploadService.deleteImageFile(imageEntity.getImageUrl());
        }

        logService.log(principal, "Deleting a fitness program with an ID " + programId);

        fitnessProgramRepository.delete(program);
    }


    /**
     * Converts a CategoryEntity to a CategoryDTO.
     *
     * @param categoryEntity the CategoryEntity to convert
     * @return the converted CategoryDTO
     */
    private CategoryDTO convertToCategoryDTO(CategoryEntity categoryEntity) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryEntity.getId());
        categoryDTO.setName(categoryEntity.getName());
        categoryDTO.setDescription(categoryEntity.getDescription());

        List<AttributeDTO> attributes = categoryEntity.getAttributes().stream()
                .map(this::convertToAttributeDTO)
                .filter(attributeDTO -> !attributeDTO.getValues().isEmpty())
                .collect(Collectors.toList());

        categoryDTO.setAttributes(attributes);

        return categoryDTO;
    }

    /**
     * Converts an AttributeEntity to an AttributeDTO.
     *
     * @param attributeEntity the AttributeEntity to convert
     * @return the converted AttributeDTO
     */
    private AttributeDTO convertToAttributeDTO(AttributeEntity attributeEntity) {
        AttributeDTO attributeDTO = modelMapper.map(attributeEntity, AttributeDTO.class);

        attributeDTO.setValues(
                attributeEntity.getAttributeValues().stream()
                        .filter(value -> !value.getProgramAttributes().isEmpty())
                        .map(value -> new AttributeValueDTO(value.getId(), value.getName()))
                        .collect(Collectors.toList())
        );

        return attributeDTO;
    }

    /**
     * Converts a ProgramAttributeEntity to an AttributeDTO.
     *
     * @param programAttribute the ProgramAttributeEntity to convert
     * @return the converted AttributeDTO
     */
    private AttributeDTO getAttributeDTO(ProgramAttributeEntity programAttribute) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setId(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getId()
        );
        attributeDTO.setName(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getName()
        );
        attributeDTO.setDescription(
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getDescription()
        );

        List<AttributeValueDTO> attributeValues =
                programAttribute
                        .getAttributeValue()
                        .getAttribute()
                        .getAttributeValues()
                        .stream()
                        .map(value -> new AttributeValueDTO(value.getId(), value.getName()))
                        .collect(Collectors.toList());
        attributeDTO.setValues(attributeValues);

        attributeDTO.setSelectedValue(
                new AttributeValueDTO(
                        programAttribute
                                .getAttributeValue()
                                .getId(),
                        programAttribute
                                .getAttributeValue()
                                .getName())
        );

        return attributeDTO;
    }

    /**
     * Converts a FitnessProgramEntity to a FitnessProgramListResponse.
     *
     * @param program the FitnessProgramEntity to convert
     * @return the converted FitnessProgramListResponse
     */
    private FitnessProgramListResponse getFitnessProgramListResponse(FitnessProgramEntity program) {
        FitnessProgramListResponse programResponse = new FitnessProgramListResponse();
        programResponse.setId(program.getId());
        programResponse.setName(program.getName());
        programResponse.setDescription(program.getDescription());
        programResponse.setPrice(program.getPrice());
        programResponse.setDuration(program.getDuration());
        programResponse.setDifficultyLevel(program.getDifficultyLevel());
        programResponse.setYoutubeUrl(program.getYoutubeUrl());

        if (program.getLocation() != null) {
            programResponse.setLocationName(program.getLocation().getName());
        }

        return programResponse;
    }

    /**
     * Converts a FitnessProgramEntity to a FitnessProgramHomeResponse.
     *
     * @param program the FitnessProgramEntity to convert
     * @return the converted FitnessProgramHomeResponse
     */
    private FitnessProgramHomeResponse getFitnessProgramHomeResponse(FitnessProgramEntity program) {
        FitnessProgramHomeResponse programResponse = new FitnessProgramHomeResponse();
        programResponse.setId(program.getId());
        programResponse.setName(program.getName());
        programResponse.setDescription(program.getDescription());
        programResponse.setPrice(program.getPrice());
        programResponse.setDuration(program.getDuration());
        programResponse.setDifficultyLevel(program.getDifficultyLevel());
        programResponse.setInstructorId(program.getUser().getId());
        programResponse.setInstructorName(generateInstructorName(program.getUser()));
        
        if (program.getLocation() != null) {
            programResponse.setLocationName(program.getLocation().getName());
        }

        List<String> imageUrls = program
                .getProgramImages()
                .stream()
                .map(ProgramImageEntity::getImageUrl)
                .collect(Collectors.toList());
        programResponse.setImages(imageUrls);

        return programResponse;
    }

    private String generateInstructorName(UserEntity user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();

        String instructorName;

        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            instructorName = firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            instructorName = firstName;
        } else if (lastName != null && !lastName.isEmpty()) {
            instructorName = lastName;
        } else {
            instructorName = username;
        }

        return instructorName;
    }
}