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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.volsch.stockmountain.extraction.conversion.ConversionException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FieldTest {

  @SuppressWarnings("unchecked")
  @Test
  void nullable() {
    final Field<Number> field = mock(Field.class, CALLS_REAL_METHODS);
    assertTrue(field.nullable());
  }

  @SuppressWarnings("unchecked")
  @Test
  void cast() {
    final Field<Number> field = mock(Field.class, CALLS_REAL_METHODS);
    when(field.type()).thenReturn(Number.class);
    assertEquals(20.1, field.cast(20.1));
  }

  @SuppressWarnings("unchecked")
  @Test
  void cast_nonMatchingType_fail() {
    final Field<Double> field = mock(Field.class, CALLS_REAL_METHODS);
    when(field.type()).thenReturn(Double.class);
    assertThrows(ClassCastException.class, () -> field.cast(10));
  }

  @SuppressWarnings("unchecked")
  @Test
  void convert() throws ConversionException {
    final Field<BigDecimal> field = mock(Field.class, CALLS_REAL_METHODS);
    when(field.type()).thenReturn(BigDecimal.class);
    assertEquals(new BigDecimal("20.1"), field.convert(new BigDecimal("20.1")));
  }

  @SuppressWarnings("unchecked")
  @Test
  void convert_nonMatchingType_fail() {
    final Field<BigDecimal> field = mock(Field.class, CALLS_REAL_METHODS);
    when(field.type()).thenReturn(BigDecimal.class);
    assertThrows(ClassCastException.class, () -> field.convert("20.1"));
  }
}