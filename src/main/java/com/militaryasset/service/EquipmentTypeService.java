package com.militaryasset.service;

import com.militaryasset.entity.EquipmentType;
import com.militaryasset.repository.EquipmentTypeRepository;
import org.springframework.stereotype.Service;
import com.militaryasset.exception.EquipmentTypeNotFoundException;
import com.militaryasset.dto.EquipmentTypeResponse;
import com.militaryasset.dto.EquipmentTypeCreateRequest;
import com.militaryasset.exception.DuplicateEquipmentTypeException;
import com.militaryasset.dto.EquipmentTypeUpdateRequest;

import java.util.List;

@Service
public class EquipmentTypeService {

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public EquipmentTypeResponse getEquipmentTypeById(Long id) {
         EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new EquipmentTypeNotFoundException(
                                "Equipment type not found with id: " + id
                        )
                );
         
         return mapToResponse(equipmentType); 
    }

    public List<EquipmentTypeResponse> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll()
        		.stream()
        		.map(this::mapToResponse)
        		.toList();
    }
    
    
    private EquipmentTypeResponse mapToResponse(EquipmentType equipmentType) {

        EquipmentTypeResponse response = new EquipmentTypeResponse();

        response.setId(equipmentType.getId());
        response.setName(equipmentType.getName());
        response.setCategory(equipmentType.getCategory());
        response.setUnitOfMeasure(equipmentType.getUnitOfMeasure());
        response.setMinimumReserve(equipmentType.getMinimumReserve());
        response.setStatus(equipmentType.getStatus());
        response.setCreatedAt(equipmentType.getCreatedAt());
        response.setUpdatedAt(equipmentType.getUpdatedAt());

        return response;
    }
    
    public EquipmentTypeResponse createEquipmentType(
            EquipmentTypeCreateRequest request) {
    	
    	if (equipmentTypeRepository.existsByName(request.getName())) {
    	    throw new DuplicateEquipmentTypeException(
    	            "Equipment type already exists with name: "
    	                    + request.getName()
    	    );
    	}

        EquipmentType equipmentType = new EquipmentType();

        equipmentType.setName(request.getName());
        equipmentType.setCategory(request.getCategory());
        equipmentType.setUnitOfMeasure(request.getUnitOfMeasure());
        equipmentType.setMinimumReserve(request.getMinimumReserve());
        equipmentType.setStatus(request.getStatus());

        EquipmentType savedEquipmentType =
                equipmentTypeRepository.save(equipmentType);

        return mapToResponse(savedEquipmentType);
    }
    
    public EquipmentTypeResponse updateEquipmentType(
            Long id,
            EquipmentTypeUpdateRequest request) {

        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new EquipmentTypeNotFoundException(
                                "Equipment type not found with id: " + id
                        )
                );

        if (!equipmentType.getName().equals(request.getName())
                && equipmentTypeRepository.existsByName(request.getName())) {

            throw new DuplicateEquipmentTypeException(
                    "Equipment type already exists with name: "
                            + request.getName()
            );
        }

        equipmentType.setName(request.getName());
        equipmentType.setCategory(request.getCategory());
        equipmentType.setUnitOfMeasure(request.getUnitOfMeasure());
        equipmentType.setMinimumReserve(request.getMinimumReserve());
        equipmentType.setStatus(request.getStatus());

        EquipmentType updatedEquipmentType =
                equipmentTypeRepository.save(equipmentType);

        return mapToResponse(updatedEquipmentType);
    }
    
    public EquipmentTypeResponse deactivateEquipmentType(Long id) {

        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new EquipmentTypeNotFoundException(
                                "Equipment type not found with id: " + id
                        )
                );

        equipmentType.setStatus("INACTIVE");

        EquipmentType updatedEquipmentType =
                equipmentTypeRepository.save(equipmentType);

        return mapToResponse(updatedEquipmentType);
    }
}
