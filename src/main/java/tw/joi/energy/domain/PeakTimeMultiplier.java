package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {}
