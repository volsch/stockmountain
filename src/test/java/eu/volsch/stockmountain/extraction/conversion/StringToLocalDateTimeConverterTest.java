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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class StringToLocalDateTimeConverterTest {

  @Test
  void getTargetType() {
    assertEquals(LocalDateTime.class, new StringToLocalDateTimeConverter(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME).getTargetType());
  }

  @Test
  void parse_null() throws ConversionException {
    final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    assertNull(converter.convert(null));
  }

  @Test
  void parse_empty() throws ConversionException {
    final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    assertNull(converter.convert(" "));
  }

  @Test
  void parse() throws ConversionException {
    final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter(
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

    final LocalDateTime temporal = converter.convert("14-03-2022 14:43:34");
    assertNotNull(temporal);
    assertEquals(2022, temporal.getYear());
    assertEquals(3, temporal.getMonthValue());
    assertEquals(14, temporal.getDayOfMonth());
  }

  @Test
  void parse_invalid_fail() {
    final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter(
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss 14:43:34"));

    assertThrows(ConversionException.class, () -> converter.convert("14-17-2022 14:43:34"));
  }
}