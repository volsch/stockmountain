/*
 * Copyright 2023 Volker Schmidt
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.volsch.stockmountain.degiro;

import edu.umd.cs.findbugs.annotations.NonNull;
import eu.volsch.stockmountain.extraction.api.Field;
import eu.volsch.stockmountain.extraction.api.SimpleField;
import eu.volsch.stockmountain.extraction.conversion.StringConverter;
import eu.volsch.stockmountain.extraction.conversion.StringToBigDecimalConvertor;
import eu.volsch.stockmountain.extraction.conversion.StringToCurrencyConverter;
import eu.volsch.stockmountain.extraction.conversion.StringToLocalDateConverter;
import eu.volsch.stockmountain.extraction.conversion.StringToLocalTimeConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Fields of DEGIRO Transactions CSV.
 */
public class DegiroTransactionFields {

  public static final Field<LocalDate> DATE = new SimpleField<>("DATE", 0, LocalDate.class, false,
      new StringToLocalDateConverter(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
  public static final Field<LocalTime> TIME = new SimpleField<>("TIME", 1, LocalTime.class, false,
      new StringToLocalTimeConverter(DateTimeFormatter.ofPattern("HH:mm")));
  public static final Field<String> NAME = new SimpleField<>("NAME", 2, String.class, false,
      StringConverter.INSTANCE);
  public static final Field<String> ISIN = new SimpleField<>("ISIN", 3, String.class, false,
      StringConverter.INSTANCE);
  public static final Field<String> EXCHANGE = new SimpleField<>("EXCHANGE", 4, String.class, false,
      StringConverter.INSTANCE);
  public static final Field<String> EXECUTION_CENTER = new SimpleField<>("EXECUTION_CENTER", 5,
      String.class, true, StringConverter.INSTANCE);
  public static final Field<BigDecimal> QUANTITY = new SimpleField<>("QUANTITY", 6,
      BigDecimal.class, false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<BigDecimal> PRICE_PER_UNIT = new SimpleField<>("PRICE_PER_UNIT", 7,
      BigDecimal.class, false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<String> PRICE_PER_UNIT_CURRENCY = new SimpleField<>(
      "PRICE_PER_UNIT_CURRENCY", 8, String.class, false, StringToCurrencyConverter.INSTANCE);
  public static final Field<BigDecimal> LOCAL_VALUE = new SimpleField<>("LOCAL_VALUE", 9,
      BigDecimal.class, false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<String> LOCAL_VALUE_CURRENCY = new SimpleField<>("LOCAL_VALUE_CURRENCY",
      10, String.class, false, StringToCurrencyConverter.INSTANCE);
  public static final Field<BigDecimal> VALUE = new SimpleField<>("VALUE", 11, BigDecimal.class,
      false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<String> VALUE_CURRENCY = new SimpleField<>("VALUE_CURRENCY", 12,
      String.class, false, StringToCurrencyConverter.INSTANCE);
  public static final Field<BigDecimal> EXCHANGE_RATE = new SimpleField<>("EXCHANGE_RATE", 13,
      BigDecimal.class, false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<BigDecimal> COSTS = new SimpleField<>("COSTS", 14, BigDecimal.class,
      true, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<String> COSTS_CURRENCY = new SimpleField<>("COSTS_CURRENCY", 15,
      String.class, true, StringToCurrencyConverter.INSTANCE);
  public static final Field<BigDecimal> TOTAL = new SimpleField<>("TOTAL", 16, BigDecimal.class,
      false, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE);
  public static final Field<String> TOTAL_CURRENCY = new SimpleField<>("TOTAL_CURRENCY", 17,
      String.class, false, StringToCurrencyConverter.INSTANCE);
  public static final Field<String> ORDER_ID = new SimpleField<>("ORDER_ID", 18, String.class, true,
      StringConverter.INSTANCE);

  private static final Set<Field<?>> FIELDS = Set.of(DATE, TIME, NAME, ISIN, EXCHANGE,
      EXECUTION_CENTER, QUANTITY, PRICE_PER_UNIT, PRICE_PER_UNIT_CURRENCY, LOCAL_VALUE,
      LOCAL_VALUE_CURRENCY, VALUE, VALUE_CURRENCY, EXCHANGE_RATE, COSTS, COSTS_CURRENCY, TOTAL,
      TOTAL_CURRENCY, ORDER_ID);

  /**
   * Returns a set of these fields.
   *
   * @return set of these fields.
   */
  @SuppressWarnings("java:S1452")
  public static @NonNull Set<Field<?>> fields() {
    return FIELDS;
  }

  private DegiroTransactionFields() {
  }
}
