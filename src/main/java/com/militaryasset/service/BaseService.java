package com.militaryasset.service;

import com.militaryasset.entity.Base;
import com.militaryasset.repository.BaseRepository;
import org.springframework.stereotype.Service;
import com.militaryasset.dto.BaseCreateRequest;
import com.militaryasset.dto.BaseUpdateRequest;
import com.militaryasset.exception.BaseNotFoundException;
import com.militaryasset.exception.DuplicateBaseException;
import com.militaryasset.dto.BaseResponse;

import java.util.List;

@Service
public class BaseService {

    private final BaseRepository baseRepository;

    public BaseService(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public BaseResponse getBaseById(Long id) {

        Base base = baseRepository.findById(id)
                .orElseThrow(() ->
                        new BaseNotFoundException(
                                "Base not found with id: " + id
                        )
                );

        return mapToResponse(base);
    }

    public List<BaseResponse> getAllBases() {

        return baseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    public BaseResponse createBase(BaseCreateRequest request) {

        if (baseRepository.existsByCode(request.getCode())) {
            throw new DuplicateBaseException(
                    "Base already exists with code: " + request.getCode()
            );
        }
        
        if (baseRepository.existsByName(request.getName())) {
            throw new DuplicateBaseException(
                    "Base already exists with name: " + request.getName()
            );
        }

        Base base = new Base();

        base.setName(request.getName());
        base.setCode(request.getCode());
        base.setLocation(request.getLocation());
        base.setStatus(request.getStatus());

        Base savedBase = baseRepository.save(base);

        return mapToResponse(savedBase);
    }
    
    public BaseResponse updateBase(Long id, BaseUpdateRequest request) {

        Base base = findBaseEntityById(id);

        if (!base.getCode().equals(request.getCode())
                && baseRepository.existsByCode(request.getCode())) {

            throw new DuplicateBaseException(
                    "Base already exists with code: " + request.getCode()
            );
        }
        
        if (!base.getName().equals(request.getName())
                && baseRepository.existsByName(request.getName())) {

            throw new DuplicateBaseException(
                    "Base already exists with name: " + request.getName()
            );
        }

        base.setName(request.getName());
        base.setCode(request.getCode());
        base.setLocation(request.getLocation());
        base.setStatus(request.getStatus());

        Base updatedBase = baseRepository.save(base);
        
        return mapToResponse(updatedBase); 
    }
    
    public BaseResponse deactivateBase(Long id) {

        Base base = findBaseEntityById(id);

        base.setStatus("INACTIVE");

        Base deactivatedBase = baseRepository.save(base);
        return mapToResponse(deactivatedBase); 
    }
    
    private Base findBaseEntityById(Long id) {

        return baseRepository.findById(id)
                .orElseThrow(() ->
                        new BaseNotFoundException(
                                "Base not found with id: " + id
                        )
                );
    }
    
    
    private BaseResponse mapToResponse(Base base) {

        BaseResponse response = new BaseResponse();

        response.setId(base.getId());
        response.setName(base.getName());
        response.setCode(base.getCode());
        response.setLocation(base.getLocation());
        response.setStatus(base.getStatus());
        response.setCreatedAt(base.getCreatedAt());
        response.setUpdatedAt(base.getUpdatedAt());

        return response;
    }
}
