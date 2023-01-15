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

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.volsch.stockmountain.model.SimplePurchaseTransaction.SimplePurchaseTransactionBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SimplePurchaseTransactionTest
    <T extends SimplePurchaseTransaction, B extends SimplePurchaseTransactionBuilder<T, B>>
    extends AbstractSimpleTransactionTest<T, B> {

  @Test
  void testToBuilder() {
    final T transaction = toBuilder(createBuilderWithData().build())
        .isin("ES9876543")
        .remainingQuantity(new BigDecimal("41.21"))
        .build();
    assertData(transaction, "ES9876543", new BigDecimal("41.21"));
  }

  @SuppressWarnings("unchecked")
  @Override
  protected B createBuilder() {
    return (B) SimplePurchaseTransaction.builder();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected B toBuilder(T transaction) {
    return (B) transaction.toBuilder();
  }

  @Override
  protected void assertData(T transaction, String isin) {
    assertData(transaction, isin, new BigDecimal("45.82"));
  }

  protected void assertData(T transaction, String isin, BigDecimal remainingQuantity) {
    super.assertData(transaction, isin);
    assertEquals(TransactionType.PURCHASE, transaction.getType());
    assertEquals(remainingQuantity, transaction.getRemainingQuantity());
    assertEquals(LocalDate.of(2022, 12, 19), transaction.getLastSplitDate());
  }

  @Override
  protected B updateBuilderWithData(B builder) {
    return super.updateBuilderWithData(builder)
        .remainingQuantity(new BigDecimal("45.82"))
        .lastSplitDate(LocalDate.of(2022, 12, 19));
  }
}