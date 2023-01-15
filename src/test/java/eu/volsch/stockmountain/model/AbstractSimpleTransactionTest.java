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
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.volsch.stockmountain.model.AbstractSimpleTransaction.AbstractSimpleTransactionBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

abstract class AbstractSimpleTransactionTest
    <T extends AbstractSimpleTransaction, B extends AbstractSimpleTransactionBuilder<T, B>> {

  @Test
  void build() {
    final T transaction = createBuilderWithData().build();
    assertData(transaction);
  }

  @Test
  void testToString() {
    assertThat(createBuilderWithData().toString(), containsString("quantity"));
  }

  protected abstract B createBuilder();

  protected abstract B toBuilder(T transaction);

  protected final B createBuilderWithData() {
    return updateBuilderWithData(createBuilder());
  }

  protected void assertData(T transaction) {
    assertData(transaction, "US12345678");
  }

  protected void assertData(T transaction, String isin) {
    assertEquals(7893L, transaction.getId());
    assertEquals(827, transaction.getVersion());
    assertEquals(LocalDate.of(2022, 12, 16), transaction.getDate());
    assertEquals(LocalTime.of(14, 51, 36), transaction.getTime());
    assertTrue(transaction.isFictional());
    assertEquals(isin, transaction.getIsin());
    assertEquals("ABAB", transaction.getTickerSymbol());
    assertEquals("Test stock", transaction.getName());
    assertEquals("XETRA", transaction.getSecuritiesExchange());
    assertEquals(new BigDecimal("47.89"), transaction.getQuantity());
    assertEquals(new Price(new BigDecimal("28.34"), "GBP"), transaction.getLocalPrice());
    assertEquals(new Price(new BigDecimal("98.43"), "GBP"), transaction.getLocalValue());
    assertEquals(new BigDecimal("1.8397"), transaction.getExchangeRate());
    assertEquals(new Price(new BigDecimal("78.23"), "EUR"), transaction.getValue());
    assertEquals(new Price(new BigDecimal("1.23"), "EUR"), transaction.getCommission());
    assertEquals(new Price(new BigDecimal("79.52"), "EUR"), transaction.getTotal());
    assertEquals("O7329646823", transaction.getOrderId());
    assertEquals("T947625684", transaction.getTransactionId());
  }

  protected B updateBuilderWithData(B builder) {
    return builder
        .date(LocalDate.of(2022, 12, 16))
        .isin("US12345678")
        .name("Test stock")
        .quantity(new BigDecimal("47.89"))
        .total(new Price(new BigDecimal("79.52"), "EUR"))
        .id(7893L)
        .version(827)
        .time(LocalTime.of(14, 51, 36))
        .fictional(true)
        .tickerSymbol("ABAB")
        .securitiesExchange("XETRA")
        .localPrice(new Price(new BigDecimal("28.34"), "GBP"))
        .localValue(new Price(new BigDecimal("98.43"), "GBP"))
        .exchangeRate(new BigDecimal("1.8397"))
        .value(new Price(new BigDecimal("78.23"), "EUR"))
        .commission(new Price(new BigDecimal("1.23"), "EUR"))
        .orderId("O7329646823")
        .transactionId("T947625684");
  }
}