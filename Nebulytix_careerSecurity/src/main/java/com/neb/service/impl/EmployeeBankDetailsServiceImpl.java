package com.neb.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neb.dto.EmployeeBankDetailsRequest;
import com.neb.dto.EmployeeBankDetailsResponse;
import com.neb.entity.Employee;
import com.neb.entity.EmployeeBankDetails;
import com.neb.exception.CustomeException;
import com.neb.exception.EmployeeNotFoundException;
import com.neb.repo.EmployeeBankDetailsRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.service.EmployeeBankDetailsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeBankDetailsServiceImpl implements EmployeeBankDetailsService {

    @Autowired
    private EmployeeRepository empRepo;
    
    @Autowired
    private EmployeeBankDetailsRepository bankRepo;
    
    @Autowired
    private ModelMapper mapper;
    
    @Override
    public EmployeeBankDetailsResponse addBankDetails(Long employeeId,EmployeeBankDetailsRequest request) {
        Employee employee = empRepo.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        bankRepo.findByEmployeeId(employeeId)
                .ifPresent(bd -> {
                    throw new CustomeException("Bank details already exist for this employee");
                });

        EmployeeBankDetails bankDetails = new EmployeeBankDetails();
        bankDetails.setEmployee(employee);
        bankDetails.setBankAccountNumber(request.getBankAccountNumber());
        bankDetails.setIfscCode(request.getIfscCode());
        bankDetails.setBankName(request.getBankName());
        bankDetails.setPfNumber(request.getPfNumber());
        bankDetails.setPanNumber(request.getPanNumber());
        bankDetails.setUanNumber(request.getUanNumber());
        bankDetails.setEpsNumber(request.getEpsNumber());
        bankDetails.setEsiNumber(request.getEsiNumber());
        EmployeeBankDetails saved = bankRepo.save(bankDetails);
       return mapper.map(saved, EmployeeBankDetailsResponse.class);
    }

	@Override
	public EmployeeBankDetailsResponse UpdateBankDetails(Long id, EmployeeBankDetailsRequest request) {
		 EmployeeBankDetails bankDetails = bankRepo.findByEmployeeId(id)
				                                   .orElseThrow(() ->new CustomeException("Bank details not found"));
		 
		if (request.getBankAccountNumber() != null)bankDetails.setBankAccountNumber(request.getBankAccountNumber());
	    if (request.getIfscCode() != null)  bankDetails.setIfscCode(request.getIfscCode());
	    if (request.getBankName() != null)  bankDetails.setBankName(request.getBankName());
	    if (request.getPfNumber() != null)  bankDetails.setPfNumber(request.getPfNumber());
	    if (request.getPanNumber() != null) bankDetails.setPanNumber(request.getPanNumber());
	    if (request.getUanNumber() != null) bankDetails.setUanNumber(request.getUanNumber());
	    if (request.getEpsNumber() != null) bankDetails.setEpsNumber(request.getEpsNumber());
	    if (request.getEsiNumber() != null) bankDetails.setEsiNumber(request.getEsiNumber());
	    EmployeeBankDetails save = bankRepo.save(bankDetails);
	    return mapper.map(save, EmployeeBankDetailsResponse.class);
	}

	@Override
	public EmployeeBankDetailsResponse getBankDetailsByEmployeeId(Long id) {
	    Employee employee = empRepo.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        // Return null if no bank details exist
	    return bankRepo.findByEmployeeId(id).map(bd -> mapper.map(bd, EmployeeBankDetailsResponse.class)).orElse(null);
	}

   
}
