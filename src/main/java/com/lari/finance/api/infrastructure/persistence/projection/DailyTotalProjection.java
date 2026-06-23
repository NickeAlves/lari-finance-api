package com.lari.finance.api.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyTotalProjection {
    LocalDate getDate();
    BigDecimal getTotal();
}
