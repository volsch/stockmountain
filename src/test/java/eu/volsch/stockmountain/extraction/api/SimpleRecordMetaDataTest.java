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

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleRecordMetaDataTest {

  private Field<String> field1;
  private Field<Double> field2;
  private Field<String> field3;

  private RecordMetaData metaData;

  @BeforeEach
  void setUp() {
    field1 = new SimpleField<>("testField1", 0, String.class, null);
    field2 = new SimpleField<>("testField2", 4, Double.class, null);
    field3 = new SimpleField<>("testField3", 2, String.class, null);
    metaData = new SimpleRecordMetaData(Set.of(field1, field2, field3));
  }

  @Test
  void testNew_noFields_fail() {
    final Set<Field<?>> fields = Set.of();
    assertThrows(IllegalArgumentException.class, () -> new SimpleRecordMetaData(fields));
  }

  @Test
  void fieldStream() {
    assertThat(metaData.fieldStream().collect(toList()),
        contains(field1, field3, field2));
  }

  @Test
  void containsField_true() {
    assertTrue(metaData.containsField(field2));
  }

  @Test
  void containsField_false() {
    assertFalse(metaData.containsField(new SimpleField<>("testField4", 7, String.class, null)));
  }

  @Test
  void getMaxFieldOrdinal() {
    assertEquals(4, metaData.getMaxFieldOrdinal());
  }

  @Test
  void getFieldCount() {
    assertEquals(3, metaData.getFieldCount());
  }

  @Test
  void getField_exists_ok() {
    assertSame(field3, metaData.getField(1));
  }

  @Test
  void getField_notExists_fail() {
    assertThrows(IllegalArgumentException.class, () -> metaData.getField(3));
  }
}