package com.militaryasset.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.militaryasset.dto.TransferApprovalRequest;
import com.militaryasset.dto.TransferCreateRequest;
import com.militaryasset.dto.TransferDispatchRequest;
import com.militaryasset.dto.TransferReceiveRequest;
import com.militaryasset.dto.TransferResponse;
import com.militaryasset.service.TransferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferCreateRequest request) {

        return ResponseEntity.ok(
                transferService.createTransfer(request));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> getTransferById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transferService.getTransferById(id));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<List<TransferResponse>> getAllTransfers() {

        return ResponseEntity.ok(
                transferService.getAllTransfers());
    }
    
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> approveTransfer(
            @PathVariable Long id,
            @Valid @RequestBody TransferApprovalRequest request) {

        return ResponseEntity.ok(
                transferService.approveTransfer(
                        id,
                        request.getApprovedByUserId()));
    }
    
    @PatchMapping("/{id}/dispatch")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> dispatchTransfer(
            @PathVariable Long id,
            @Valid @RequestBody TransferDispatchRequest request) {

        return ResponseEntity.ok(
                transferService.dispatchTransfer(
                        id,
                        request.getDispatchedByUserId()));
    }
    
    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> receiveTransfer(
            @PathVariable Long id,
            @Valid @RequestBody TransferReceiveRequest request) {

        return ResponseEntity.ok(
                transferService.receiveTransfer(
                        id,
                        request.getReceivedByUserId()));
    }
    
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> completeTransfer(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transferService.completeTransfer(id));
    }
}
