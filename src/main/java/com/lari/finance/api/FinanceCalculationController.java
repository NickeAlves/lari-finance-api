package com.lari.finance.api;

import com.lari.finance.api.domain.model.FinancialAllocationPolicy;
import com.lari.finance.api.domain.model.MoneyBreakdown;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
public class FinanceCalculationController {

	@GetMapping("/rates")
	public Map<String, BigDecimal> rates() {
		return Map.of(
				"iva", FinancialAllocationPolicy.VAT_RATE,
				"fixedExpenses", FinancialAllocationPolicy.FIXED_EXPENSES_RATE,
				"products", FinancialAllocationPolicy.PRODUCTS_RATE,
				"salary", FinancialAllocationPolicy.SALARY_RATE,
				"annualTaxReserve", FinancialAllocationPolicy.ANNUAL_TAX_RESERVE_RATE);
	}

	@PostMapping("/calculate")
	public CalculationResponse calculate(@RequestBody CalculationRequest request) {
		BigDecimal amount = FinancialAllocationPolicy.normalize(request.amount() == null ? BigDecimal.ZERO : request.amount());
		MoneyBreakdown breakdown = FinancialAllocationPolicy.calculate(amount);

		return new CalculationResponse(
				amount,
				breakdown.vatAmount(),
				breakdown.fixedExpensesAmount(),
				breakdown.productsAmount(),
				breakdown.salaryAmount(),
				breakdown.annualTaxReserveAmount(),
				amount);
	}

	public record CalculationRequest(BigDecimal amount) {
	}

	public record CalculationResponse(
			BigDecimal amount,
			BigDecimal iva,
			BigDecimal fixedExpenses,
			BigDecimal products,
			BigDecimal salary,
			BigDecimal annualTaxReserve,
			BigDecimal totalDay) {
	}
}
