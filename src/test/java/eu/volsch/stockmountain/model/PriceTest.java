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

package eu.volsch.stockmountain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PriceTest {

  @Test
  void getValue() {
    assertEquals(new BigDecimal("47.12"),
        new Price(new BigDecimal("47.12"), "EUR").getValue());
  }

  @Test
  void getCurrency() {
    assertEquals("ABC",
        new Price(new BigDecimal("47.11"), "ABC").getCurrency());
  }

  @Test
  void compareTo_currencyDiffers() {
    assertThat(new Price(new BigDecimal("47.11"), "ABC").compareTo(
        new Price(new BigDecimal("47.11"), "BCD")), lessThan(0));
  }

  @Test
  void compareTo_valueDiffers() {
    assertThat(new Price(new BigDecimal("47.12"), "ABC").compareTo(
        new Price(new BigDecimal("47.11"), "ABC")), greaterThan(0));
  }

  @Test
  void compareTo_equals() {
    assertThat(new Price(new BigDecimal("47.11"), "ABC").compareTo(
        new Price(new BigDecimal("47.11"), "ABC")), equalTo(0));
  }

  @Test
  void equalsAndHashCode() {
    EqualsVerifier.forClass(Price.class)
        .usingGetClass()
        .withNonnullFields("value", "currency")
        .verify();
  }

  @Test
  void testToString() {
    assertEquals("-20.57 DEF",
        new Price(new BigDecimal("-20.57"), "DEF").toString());
  }
}