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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import eu.volsch.stockmountain.extraction.api.Extractor;
import eu.volsch.stockmountain.extraction.api.Record;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class DegiroTransactionExtractorProviderTest {

  private final DegiroTransactionExtractorProvider provider =
      new DegiroTransactionExtractorProvider();

  @Test
  void getBrokerId() {
    assertEquals("DEGIRO", provider.getBrokerId());
  }

  @Test
  void getExtractor() throws IOException {
    final Extractor extractor = provider.getExtractor();

    final List<Record> records;
    try (final InputStream is = DegiroTransactionExtractorProvider.class.getResourceAsStream(
        "simple_transactions.csv")) {
      assertNotNull(is, "Sample data does not exist");
      records = extractor.extract(is).collect(toList());
    }

    assertEquals(2, records.size());
    assertExtractedRecord1(records.get(0));
    assertExtractedRecord2(records.get(1));
  }

  private static void assertExtractedRecord1(Record r) {
    assertEquals(LocalDate.of(2022, 7, 20), r.getValue(DegiroTransactionFields.DATE));
    assertEquals(LocalTime.of(18, 12, 0), r.getValue(DegiroTransactionFields.TIME));
    assertEquals("AMAZON.COM INC. - COM", r.getValue(DegiroTransactionFields.NAME));
    assertEquals("US0231351067", r.getValue(DegiroTransactionFields.ISIN));
    assertEquals("NDQ", r.getValue(DegiroTransactionFields.EXCHANGE));
    assertEquals("CDED", r.getValue(DegiroTransactionFields.EXECUTION_CENTER));
    assertEquals(BigDecimal.valueOf(1), r.getValue(DegiroTransactionFields.QUANTITY));
    assertEquals(new BigDecimal("122.6800"), r.getValue(DegiroTransactionFields.PRICE_PER_UNIT));
    assertEquals("USD", r.getValue(DegiroTransactionFields.PRICE_PER_UNIT_CURRENCY));
    assertEquals(new BigDecimal("-245.36"), r.getValue(DegiroTransactionFields.LOCAL_VALUE));
    assertEquals("USD", r.getValue(DegiroTransactionFields.LOCAL_VALUE_CURRENCY));
    assertEquals(new BigDecimal("-240.20"), r.getValue(DegiroTransactionFields.VALUE));
    assertEquals("EUR", r.getValue(DegiroTransactionFields.VALUE_CURRENCY));
    assertEquals(new BigDecimal("1.0215"), r.getValue(DegiroTransactionFields.EXCHANGE_RATE));
    assertEquals(new BigDecimal("-0.50"), r.getValue(DegiroTransactionFields.COSTS));
    assertEquals("EUR", r.getValue(DegiroTransactionFields.COSTS_CURRENCY));
    assertEquals(new BigDecimal("-240.90"), r.getValue(DegiroTransactionFields.TOTAL));
    assertEquals("EUR", r.getValue(DegiroTransactionFields.TOTAL_CURRENCY));
    assertEquals("0d71cb9d-1879-43cc-838b-bfd845a81856",
        r.getValue(DegiroTransactionFields.ORDER_ID));
  }

  private static void assertExtractedRecord2(Record r) {
    assertEquals(LocalDate.of(2022, 6, 6), r.getValue(DegiroTransactionFields.DATE));
    assertEquals(LocalTime.of(0, 0, 0), r.getValue(DegiroTransactionFields.TIME));
    assertEquals("AMAZON.COM INC. - COM", r.getValue(DegiroTransactionFields.NAME));
    assertEquals("US0231351067", r.getValue(DegiroTransactionFields.ISIN));
    assertEquals("NDQ", r.getValue(DegiroTransactionFields.EXCHANGE));
    assertNull(r.getValue(DegiroTransactionFields.EXECUTION_CENTER));
    assertEquals(BigDecimal.valueOf(6), r.getValue(DegiroTransactionFields.QUANTITY));
    assertEquals(new BigDecimal("122.3500"), r.getValue(DegiroTransactionFields.PRICE_PER_UNIT));
    assertEquals("USD", r.getValue(DegiroTransactionFields.PRICE_PER_UNIT_CURRENCY));
    assertEquals(new BigDecimal("-734.10"), r.getValue(DegiroTransactionFields.LOCAL_VALUE));
    assertEquals("USD", r.getValue(DegiroTransactionFields.LOCAL_VALUE_CURRENCY));
    assertEquals(new BigDecimal("-703.56"), r.getValue(DegiroTransactionFields.VALUE));
    assertEquals("EUR", r.getValue(DegiroTransactionFields.VALUE_CURRENCY));
    assertEquals(new BigDecimal("1.0434"), r.getValue(DegiroTransactionFields.EXCHANGE_RATE));
    assertNull(r.getValue(DegiroTransactionFields.COSTS));
    assertNull(r.getValue(DegiroTransactionFields.COSTS_CURRENCY));
    assertEquals(new BigDecimal("-703.56"), r.getValue(DegiroTransactionFields.TOTAL));
    assertEquals("EUR", r.getValue(DegiroTransactionFields.TOTAL_CURRENCY));
    assertNull(r.getValue(DegiroTransactionFields.ORDER_ID));
  }
}