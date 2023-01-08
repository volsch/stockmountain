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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.volsch.stockmountain.extraction.conversion.StringConverter;
import eu.volsch.stockmountain.extraction.conversion.StringToBigDecimalConvertor;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleImmutableRecordTest {

  private Field<String> field1;
  private Field<BigDecimal> field2;
  private RecordMetaData metaData;

  @BeforeEach
  void setUp() {
    field1 = new SimpleField<>("testField1", 0, String.class, StringConverter.INSTANCE);
    field2 = new SimpleField<>("testField2", 3, BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE);
    final Field<String> field3 = new SimpleField<>("testField3", 2, String.class, null);
    metaData = new SimpleRecordMetaData(Set.of(field1, field2, field3));
  }

  @Test
  void testNew_tooLessValues_fail() {
    assertThrows(IllegalArgumentException.class,
        () -> new SimpleImmutableRecord(metaData, new Object[3]));
  }

  @Test
  void testNew_immutableValues() {
    final Object[] values = {"testValue1", null, "testValue2", 11.0};
    final SimpleImmutableRecord record = new SimpleImmutableRecord(metaData, values);
    values[0] = "testValueUpdated";
    assertEquals("testValue1", record.getValue(field1));
  }

  @Test
  void getMetaData() {
    assertSame(metaData, new SimpleImmutableRecord(metaData, new Object[4]).getMetaData());
  }

  @Test
  void getValue() {
    assertEquals(new BigDecimal("47.5"),
        new SimpleImmutableRecord(metaData, null, null, null, new BigDecimal("47.5"))
            .getValue(field2));
  }

  @Test
  void getValue_unknownField_fail() {
    final SimpleImmutableRecord record = new SimpleImmutableRecord(metaData, new Object[4]);
    final SimpleField<String> otherField = new SimpleField<>("otherField", 7, String.class, null);
    assertThrows(IllegalArgumentException.class, () -> record.getValue(otherField));
  }
}