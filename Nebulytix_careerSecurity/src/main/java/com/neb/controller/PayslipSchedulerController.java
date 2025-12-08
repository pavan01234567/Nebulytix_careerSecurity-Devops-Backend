package com.neb.controller;

import com.neb.exception.PayslipGenerationException; // ✅ Import your custom exception
import com.neb.scheduler.PayslipScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * -----------------------------------------------------------------
 * Controller: PayslipSchedulerController
 * -----------------------------------------------------------------
 * Purpose:
 *   Allows manual triggering of payslip generation for all employees
 *   via REST API (useful for testing or admin control through Postman).
 *
 * Endpoint:
 *   ✅ POST /api/payslip/generate-all → Generate payslips for all employees
 * -----------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/payslip")
public class PayslipSchedulerController {

    @Autowired
    private PayslipScheduler payslipScheduler;

    /**
     * -----------------------------------------------------------------
     * Method: generateAllPayslips()
     * -----------------------------------------------------------------
     * Purpose:
     *   - Allows manual triggering of monthly payslip generation.
     *   - Useful for testing via Postman instead of waiting for the
     *     scheduled task to run automatically.
     *
     * Example (Postman):
     *   POST → http://localhost:8080/api/payslip/generate-all
     * -----------------------------------------------------------------
     */
    @PostMapping("/generate-all")
    public ResponseEntity<String> generateAllPayslips() {
        try {
            payslipScheduler.generateMonthlyPayslips();
            return ResponseEntity.ok("✅ Payslips generated successfully for all employees.");
        } catch (PayslipGenerationException ex) {
            // Custom exception from PayslipScheduler
            return ResponseEntity.internalServerError()
                    .body("❌ Payslip generation failed: " + ex.getMessage());
        } catch (Exception e) {
            // Catch any unexpected errors
            return ResponseEntity.internalServerError()
                    .body("⚠ Unexpected error during payslip generation: " + e.getMessage());
        }
    }
}
