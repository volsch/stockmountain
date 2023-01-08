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

package eu.volsch.stockmountain.extraction.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.volsch.stockmountain.extraction.conversion.ConversionException;
import eu.volsch.stockmountain.extraction.conversion.StringToBigDecimalConvertor;
import java.math.BigDecimal;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class SimpleFieldTest {

  @Test
  void name() {
    assertEquals("testName",
        new SimpleField<>("testName", 2, String.class, null).name());
  }

  @Test
  void ordinal() {
    assertEquals(2,
        new SimpleField<>("testName", 2, String.class, null).ordinal());
  }

  @Test
  void type() {
    assertEquals(String.class,
        new SimpleField<>("testName", 2, String.class, null).type());
  }

  @Test
  void nullable_default() {
    assertTrue(new SimpleField<>("testName", 2, String.class, null).nullable());
  }

  @Test
  void nullable_true() {
    assertTrue(new SimpleField<>("testName", 2, String.class, true, null).nullable());
  }

  @Test
  void nullable_false() {
    assertFalse(new SimpleField<>("testName", 2, String.class, false, null).nullable());
  }

  @Test
  void cast() {
    assertEquals(20.1,
        new SimpleField<>("testName", 2, Number.class, null).cast(20.1));
  }

  @Test
  void cast_nonMatchingType_fail() {
    final SimpleField<Double> field = new SimpleField<>("testName", 2, Double.class, null);
    assertThrows(ClassCastException.class, () -> field.cast(10));
  }

  @Test
  void convert_withoutConverter() throws ConversionException {
    final Field<BigDecimal> field = new SimpleField<>("testName", 2, BigDecimal.class, null);
    assertEquals(new BigDecimal("20.1"), field.convert(new BigDecimal("20.1")));
  }

  @Test
  void convert_withoutConverterNonMatchingType_fail() {
    final Field<BigDecimal> field = new SimpleField<>("testName", 2, BigDecimal.class, null);
    assertThrows(ClassCastException.class, () -> field.convert("20.1"));
  }

  @Test
  void convert_withConverter() throws ConversionException {
    final Field<BigDecimal> field = new SimpleField<>("testName", 2, BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE);
    assertEquals(new BigDecimal("20.1"), field.convert(" 20,1 "));
  }

  @Test
  void convert_withConverterNonMatchingValue_fail() {
    final Field<BigDecimal> field = new SimpleField<>("testName", 2, BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE);
    assertThrows(ConversionException.class, () -> field.convert("20,1E10"));
  }

  @Test
  void convert_withConverterNonMatchingType_fail() {
    final Field<BigDecimal> field = new SimpleField<>("testName", 2, BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE);
    assertThrows(ClassCastException.class, () -> field.convert(20.1));
  }

  @Test
  void equalsAndHashCode() {
    EqualsVerifier.forClass(SimpleField.class)
        .usingGetClass()
        .withCachedHashCode("cachedHashCode", "calcHashCode",
            new SimpleField<>("test", 2, String.class, null))
        .withIgnoredFields("converter")
        .withNonnullFields("name", "type")
        .verify();
  }

  @Test
  void testToString() {
    assertEquals("testName",
        new SimpleField<>("testName", 2, String.class, null).toString());
  }
}