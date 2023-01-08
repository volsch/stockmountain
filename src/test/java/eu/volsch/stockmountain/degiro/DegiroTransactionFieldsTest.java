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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.volsch.stockmountain.extraction.api.Field;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DegiroTransactionFieldsTest {

  @Test
  void fields_size() {
    assertEquals(19, DegiroTransactionFields.fields().size());
  }

  @Test
  void fields_uniqueNames() {
    final Set<String> names = new HashSet<>();
    for (Field<?> field : DegiroTransactionFields.fields()) {
      assertTrue(names.add(field.name()), "Field name " + field.name() + " is not unique");
    }
  }

  @Test
  void fields_uniqueOrdinals() {
    final Set<Integer> ordinals = new HashSet<>();
    for (Field<?> field : DegiroTransactionFields.fields()) {
      assertTrue(ordinals.add(field.ordinal()), "Ordinal " + field.ordinal() + " is not unique");
    }
  }
}