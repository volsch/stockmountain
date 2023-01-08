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

package eu.volsch.stockmountain.extraction.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class StringToBigDecimalConvertorTest {

  @Test
  void getSourceType() {
    assertEquals(String.class, StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE.getSourceType());
  }

  @Test
  void getTargetType() {
    assertEquals(BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.getTargetType());
  }

  @Test
  void convert_null() throws ConversionException {
    assertNull(StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE.convert(null));
  }

  @Test
  void convert_blank() throws ConversionException {
    assertNull(StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert(" "));
  }

  @Test
  void convert_commaThousand() throws ConversionException {
    assertEquals(new BigDecimal("123456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE.convert("123,456,789.23456"));
  }

  @Test
  void convert_pointThousand() throws ConversionException {
    assertEquals(new BigDecimal("123456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.789,23456"));
  }

  @Test
  void convert_spaceThousand() throws ConversionException {
    assertEquals(new BigDecimal("123456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123 456 789,23456"));
  }

  @Test
  void convert_trim() throws ConversionException {
    assertEquals(new BigDecimal("123456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("    123456789,23456 "));
  }

  @Test
  void convert_thousandWithoutDecimalSeparator() throws ConversionException {
    assertEquals(new BigDecimal("123456789"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.789"));
  }

  @Test
  void convert_noThousand() throws ConversionException {
    assertEquals(new BigDecimal("123456789"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123456789"));
  }

  @Test
  void convert_negativeNoThousand() throws ConversionException {
    assertEquals(new BigDecimal("-123456789"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("-123456789"));
  }

  @Test
  void convert_noThousandWithoutDecimalSeparator() throws ConversionException {
    assertEquals(new BigDecimal("123456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123456789,23456"));
  }

  @Test
  void convert_oneDigitThousand() throws ConversionException {
    assertEquals(new BigDecimal("1456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("1.456.789,23456"));
  }

  @Test
  void convert_negativeOneDigitThousand() throws ConversionException {
    assertEquals(new BigDecimal("-1456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("-1.456.789,23456"));
  }

  @Test
  void convert_positiveOneDigitThousand() throws ConversionException {
    assertEquals(new BigDecimal("1456789.23456"),
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("+1.456.789,23456"));
  }

  @Test
  void convert_missingFirstDigitThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert(".456.789,23456"));
  }

  @Test
  void convert_tooManyFirstDigitsThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("1234.456.789,23456"));
  }

  @Test
  void convert_tooManySecondDigitsThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.4567.789,23456"));
  }

  @Test
  void convert_tooLessSecondDigitsThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.45.789,23456"));
  }

  @Test
  void convert_tooLessLastDigitsThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.78,23456"));
  }

  @Test
  void convert_finalThousand_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.789."));
  }

  @Test
  void convert_tooLessLastDigitsThousandNoDecimalDigits_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.78"));
  }

  @Test
  void convert_tooManyDecimalSeparators_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE.convert("123.456.789,234,56"));
  }

  @Test
  void convert_noBigDecimal_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE.convert("123,456.,789,,"));
  }

  @Test
  void convert_character_fail() {
    assertThrows(ConversionException.class,
        () -> StringToBigDecimalConvertor.DECIMAL_POINT_INSTANCE.convert("1.23345353454567E16"));
  }
}