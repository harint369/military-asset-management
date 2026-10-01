package com.militaryasset.exception;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.militaryasset.exception.BaseNotFoundException;
import com.militaryasset.exception.DuplicateBaseException;
import com.militaryasset.exception.EquipmentTypeNotFoundException;
import com.militaryasset.exception.DuplicateEquipmentTypeException;
import com.militaryasset.exception.InventoryNotFoundException;
import com.militaryasset.exception.PurchaseNotFoundException;
import com.militaryasset.exception.PurchaseValidationException;
import com.militaryasset.exception.PurchaseItemNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(DashboardValidationException.class)
	public ResponseEntity<String> handleDashboardValidation(
	        DashboardValidationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(RepairValidationException.class)
	public ResponseEntity<String> handleRepairValidation(
	        RepairValidationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(ExpenditureNotFoundException.class)
	public ResponseEntity<String> handleExpenditureNotFound(
	        ExpenditureNotFoundException ex) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(ex.getMessage());
	}

	@ExceptionHandler(ExpenditureValidationException.class)
	public ResponseEntity<String> handleExpenditureValidation(
	        ExpenditureValidationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}

	@ExceptionHandler(ExpenditureApprovalException.class)
	public ResponseEntity<String> handleExpenditureApproval(
	        ExpenditureApprovalException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(AssignmentValidationException.class)
	public ResponseEntity<String> handleAssignmentValidation(
	        AssignmentValidationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(AssignmentNotFoundException.class)
	public ResponseEntity<String> handleAssignmentNotFound(
	        AssignmentNotFoundException ex) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(TransferValidationException.class)
	public ResponseEntity<String> handleTransferValidation(
	        TransferValidationException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(TransferNotFoundException.class)
	public ResponseEntity<String> handleTransferNotFound(
	        TransferNotFoundException ex) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(
	        UserNotFoundException ex) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(PurchaseItemNotFoundException.class)
	public ResponseEntity<Map<String, String>> handlePurchaseItemNotFoundException(
	        PurchaseItemNotFoundException exception) {

	    Map<String, String> response = new HashMap<>();
	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(PurchaseValidationException.class)
	public ResponseEntity<Map<String, String>> handlePurchaseValidationException(
	        PurchaseValidationException exception) {

	    Map<String, String> response = new HashMap<>();
	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(PurchaseNotFoundException.class)
	public ResponseEntity<Map<String, String>> handlePurchaseNotFoundException(
	        PurchaseNotFoundException exception) {

	    Map<String, String> response = new HashMap<>();
	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InventoryNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleInventoryNotFoundException(
	        InventoryNotFoundException exception) {

	    Map<String, String> response = new HashMap<>();
	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(BaseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBaseNotFoundException(
            BaseNotFoundException exception) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }
	
	@ExceptionHandler(EquipmentTypeNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleEquipmentTypeNotFoundException(
	        EquipmentTypeNotFoundException exception) {

	    Map<String, String> response = new HashMap<>();

	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(
	            response,
	            HttpStatus.NOT_FOUND
	    );
	}
	
	@ExceptionHandler(DuplicateBaseException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateBaseException(
	        DuplicateBaseException exception) {

	    Map<String, String> response = new HashMap<>();

	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(
	            response,
	            HttpStatus.CONFLICT
	    );
	}
	
	@ExceptionHandler(DuplicateEquipmentTypeException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateEquipmentTypeException(
	        DuplicateEquipmentTypeException exception) {

	    Map<String, String> response = new HashMap<>();

	    response.put("error", exception.getMessage());

	    return new ResponseEntity<>(
	            response,
	            HttpStatus.CONFLICT
	    );
	}
	
	

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
}
