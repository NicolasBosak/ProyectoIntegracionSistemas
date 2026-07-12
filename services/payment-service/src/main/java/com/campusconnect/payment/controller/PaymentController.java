package com.campusconnect.payment.controller;

import com.campusconnect.payment.dto.ConfirmPaymentRequest;
import com.campusconnect.payment.dto.PaymentResponse;
import com.campusconnect.payment.dto.RegisterDebtRequest;
import com.campusconnect.payment.dto.StudentRefResponse;
import com.campusconnect.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Pagos", description = "Deudas y confirmacion de pagos")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping("/students")
    @Operation(summary = "Listar estudiantes matriculados (proyeccion local)")
    public List<StudentRefResponse> students() {
        return service.listStudents();
    }

    @PostMapping("/debts")
    @Operation(summary = "Registrar obligacion de pago / simular deuda")
    public ResponseEntity<PaymentResponse> registerDebt(@Valid @RequestBody RegisterDebtRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerDebt(req));
    }

    @GetMapping("/pending")
    @Operation(summary = "Listar pagos pendientes")
    public List<PaymentResponse> pending() {
        return service.listPending();
    }

    @GetMapping("/confirmed")
    @Operation(summary = "Listar pagos confirmados")
    public List<PaymentResponse> confirmed() {
        return service.listConfirmed();
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar pago (publica PaymentConfirmed)")
    public ResponseEntity<PaymentResponse> confirm(@Valid @RequestBody ConfirmPaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.confirmPayment(req));
    }
}
