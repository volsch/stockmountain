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

package eu.volsch.stockmountain.extraction.csv;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import eu.volsch.stockmountain.extraction.api.ExtractionDataAccessException;
import eu.volsch.stockmountain.extraction.api.ExtractionException;
import eu.volsch.stockmountain.extraction.api.Field;
import eu.volsch.stockmountain.extraction.api.Record;
import eu.volsch.stockmountain.extraction.api.SimpleField;
import eu.volsch.stockmountain.extraction.conversion.StringConverter;
import eu.volsch.stockmountain.extraction.conversion.StringToBigDecimalConvertor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvExtractorTest {

  private static final String CR = "\r";
  private static final String NL = "\n";
  private static final String CR_NL = CR + NL;

  private static final String HEADER_1 =
      "Header 1,,Header 3,Header 2";
  private static final String HEADER_2 =
      "Sub Header 1,Other 1,Sub Header 3,Sub Header 2,Other 2";
  private static final String DATA_1 =
      "  Value 1 ,Skipped,\" 47,896 \",  Value 2 ";
  private static final String DATA_2 =
      "\"Line 1,\r\n\"\"Line 2\"\",\r\"\"Line 3,\r\r\nLine 4\"\"\",,-492,\"Value 2 \"";
  private static final byte[] INVALID_UTF8_INPUT = {'x', (byte) 0xDB, 0x7F, 'z', ',', ',', '1', '0',
      ',', 'a', 'b'};

  private Field<String> field1;
  private Field<BigDecimal> field2;
  private Field<String> field3;
  private Set<Field<?>> fields;

  @BeforeEach
  void setUp() {
    field1 = new SimpleField<>("testField1", 0, String.class, StringConverter.INSTANCE);
    field2 = new SimpleField<>("testField2", 2, BigDecimal.class,
        StringToBigDecimalConvertor.DECIMAL_COMMA_INSTANCE);
    field3 = new SimpleField<>("testField3", 3, String.class, null);
    fields = Set.of(field3, field1, field2);
  }

  @Test
  void testNew_fieldSeparatorNewLine_fail() {
    assertThrows(IllegalArgumentException.class,
        () -> new CsvExtractor('\n', fields, 0, Integer.MAX_VALUE));
  }

  @Test
  void testNew_fieldSeparatorCarriageReturn_fail() {
    assertThrows(IllegalArgumentException.class,
        () -> new CsvExtractor('\r', fields, 0, Integer.MAX_VALUE));
  }

  @Test
  void testNew_fieldSeparatorEncloseChar_fail() {
    assertThrows(IllegalArgumentException.class,
        () -> new CsvExtractor('"', fields, 0, Integer.MAX_VALUE));
  }

  @Test
  void readerSupported() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 2, Integer.MAX_VALUE);
    assertTrue(extractor.readerSupported());
  }

  @Test
  void extract_skipTwoHeaders() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 2, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_1 + CR_NL
                + HEADER_2 + NL
                + DATA_1 + CR_NL))
        .collect(toList());

    assertEquals(1, result.size());
    assertData1(result.get(0));
  }

  @Test
  void extract_noData() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 2, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_1 + NL
                + HEADER_2 + CR_NL))
        .collect(toList());

    assertTrue(result.isEmpty());
  }

  @Test
  void extract_noHeaders() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(DATA_1 + NL))
        .collect(toList());

    assertEquals(1, result.size());
    assertData1(result.get(0));
  }

  @Test
  void extract_tooLessHeaders() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 2, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_1 + CR_NL))
        .collect(toList());

    assertTrue(result.isEmpty());
  }

  @Test
  void extract_emptyInput() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(""))
        .collect(toList());

    assertTrue(result.isEmpty());
  }

  @Test
  void extract_semicolonSeparator() {
    final CsvExtractor extractor = new CsvExtractor(';', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader("abc;def;12,78;ghi"))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertEquals("abc", record.getValue(field1));
    assertEquals(new BigDecimal("12.78"), record.getValue(field2));
    assertEquals("ghi", record.getValue(field3));
  }

  @Test
  void extract_singleField() {
    final CsvExtractor extractor = new CsvExtractor(';', Set.of(field1), 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader("abc\r\ndef\rhij"))
        .collect(toList());

    assertEquals(3, result.size());

    assertEquals("abc", result.get(0).getValue(field1));
    assertEquals("def", result.get(1).getValue(field1));
    assertEquals("hij", result.get(2).getValue(field1));
  }

  @Test
  void extract_multiLineValue() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 1, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_1 + CR
                + DATA_2 + NL))
        .collect(toList());

    assertEquals(1, result.size());
    assertData2(result.get(0));
  }

  @Test
  void extract_noEndOfLine() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 1, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_2 + NL
                + DATA_2 + CR_NL
                + DATA_1))
        .collect(toList());

    assertEquals(2, result.size());
    assertData2(result.get(0));
    assertData1(result.get(1));
  }

  @Test
  void extract_multipleEmptyLines() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(DATA_1 + CR_NL + CR + CR + NL + CR_NL))
        .collect(toList());

    assertEquals(1, result.size());
    assertData1(result.get(0));
  }

  @Test
  void extract_lastEmptyLine() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(DATA_1 + CR_NL + "    " + CR_NL))
        .collect(toList());

    assertEquals(1, result.size());
    assertData1(result.get(0));
  }

  @Test
  void extract_additionalFields() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 1, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(HEADER_2 + ",Other X,OtherY" + NL
                + DATA_2 + ",Other Value 1,Other Value 2,Other Value 3" + CR
                + DATA_1 + CR))
        .collect(toList());

    assertEquals(2, result.size());
    assertData2(result.get(0));
    assertData1(result.get(1));
  }

  @Test
  void extract_emptyValues() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(",,," + CR_NL))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertNull(record.getValue(field1));
    assertNull(record.getValue(field2));
    assertEquals("", record.getValue(field3));
  }

  @Test
  void extract_encloseCharacterInsideString() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new StringReader(" \",,, \"" + CR_NL))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertEquals("\"", record.getValue(field1));
    assertNull(record.getValue(field2));
    assertEquals(" \"", record.getValue(field3));
  }

  @Test
  void extract_unexpectedNull_fail() {
    assertExtractWithCsvExtractionException(Set.of(
        new SimpleField<>("testField1", 0, String.class, false, StringConverter.INSTANCE),
        new SimpleField<>("testField2", 1, String.class, false, StringConverter.INSTANCE)
    ), "abc,", Integer.MAX_VALUE, 1, 1, 4, "");
  }

  @Test
  void extract_invalidDecimalValue_fail() {
    assertExtractWithCsvExtractionException("def,,10E01,abc", Integer.MAX_VALUE, 1, 1, 11, "10E01");
  }

  @Test
  void extract_exceededMaxRecordChars_fail() {
    assertExtractWithCsvExtractionException("a,,1,b\n1234567890,,1000,\"123456\n1234567890\"",
        30, 2, 3, 6, null);
  }

  @Test
  void extract_invalidEncloseNoEndOfField_fail() {
    assertExtractWithCsvExtractionException("\" \" ,,10,abc", Integer.MAX_VALUE, 1, 1, 4, null);
  }

  @Test
  void extract_invalidEncloseNoEol_fail() {
    assertExtractWithCsvExtractionException("abc,,10,\" \" \r\n", Integer.MAX_VALUE, 1, 1, 12,
        null);
  }

  @Test
  void extract_invalidEncloseNoEof_fail() {
    assertExtractWithCsvExtractionException("abc,,10,\" ", Integer.MAX_VALUE, 1, 1, 10, null);
  }

  @Test
  void extract_tooLessFields_fail() {
    assertExtractWithCsvExtractionException("abc,,10\n", Integer.MAX_VALUE, 1, 1, 8, null);
  }

  @Test
  void extract_emptyLineInBetween_fail() {
    assertExtractWithCsvExtractionException(DATA_1 + CR_NL + CR_NL + DATA_2 + CR_NL,
        Integer.MAX_VALUE, 5, 11, 27, null);
  }

  @Test
  void extract_emptyLineInBetweenBeforeEndOfFile_fail() {
    assertExtractWithCsvExtractionException(DATA_1 + CR_NL + CR_NL + DATA_2,
        Integer.MAX_VALUE, 5, 11, 26, null);
  }

  @Test
  void extract_Latin1() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    assertNonAscii(extractor, StandardCharsets.ISO_8859_1);
  }

  @Test
  void extract_Utf16() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE,
        StandardCharsets.UTF_16);
    assertNonAscii(extractor, StandardCharsets.UTF_16);
  }

  private void assertNonAscii(@NonNull CsvExtractor extractor, @NonNull Charset charset) {
    final List<Record> result = extractor.extract(
            new ByteArrayInputStream("xÜz,,10,aÑb".getBytes(charset)))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertEquals("xÜz", record.getValue(field1));
    assertEquals(new BigDecimal("10"), record.getValue(field2));
    assertEquals("aÑb", record.getValue(field3));
  }

  @Test
  void extract_invalidUsAsciiCharacter_replaced() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new InputStreamReader(
                new ByteArrayInputStream("xÜz,,10,aÑb".getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.US_ASCII))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertEquals("x�z", record.getValue(field1));
    assertEquals(new BigDecimal("10"), record.getValue(field2));
    assertEquals("a�b", record.getValue(field3));
  }

  @Test
  void extract_invalidUnicodeCharacter_replaced() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final List<Record> result = extractor.extract(
            new InputStreamReader(
                new ByteArrayInputStream(INVALID_UTF8_INPUT),
                StandardCharsets.UTF_8))
        .collect(toList());

    assertEquals(1, result.size());

    final Record record = result.get(0);
    assertEquals("x�\u007Fz", record.getValue(field1));
    assertEquals(new BigDecimal("10"), record.getValue(field2));
    assertEquals("ab", record.getValue(field3));
  }

  @Test
  void extract_invalidUnicodeCharacter_fail() {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE,
        StandardCharsets.UTF_8);
    final Stream<Record> recordStream = extractor.extract(
        new ByteArrayInputStream(INVALID_UTF8_INPUT));
    final ExtractionException e =
        assertThrows(ExtractionDataAccessException.class, () -> recordStreamConsume(recordStream));

    assertThat(e.getCause(), isA(CharacterCodingException.class));
  }

  @Test
  void extract_ioException_fail(@Mock Reader reader) throws IOException {
    when(reader.read()).thenThrow(new IOException("TEST"));

    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, Integer.MAX_VALUE);
    final Stream<Record> recordStream = extractor.extract(reader);
    final ExtractionException e =
        assertThrows(ExtractionDataAccessException.class, () -> recordStreamConsume(recordStream));

    assertThat(e.getCause(), isA(IOException.class));
  }

  private void assertData1(@NonNull Record record) {
    assertEquals("Value 1", record.getValue(field1));
    assertEquals(new BigDecimal("47.896"), record.getValue(field2));
    assertEquals("  Value 2 ", record.getValue(field3));
  }

  private void assertData2(@NonNull Record record) {
    assertEquals("Line 1,\n\"Line 2\",\n\"Line 3,\n\nLine 4\"", record.getValue(field1));
    assertEquals(new BigDecimal("-492"), record.getValue(field2));
    assertEquals("Value 2 ", record.getValue(field3));
  }

  private void assertExtractWithCsvExtractionException(@NonNull String data, int maxRecordChars,
      int recordNo, int lineNo, int linePos, String invalidValue) {
    assertExtractWithCsvExtractionException(fields, data, maxRecordChars,
        recordNo, lineNo, linePos, invalidValue);
  }

  private void assertExtractWithCsvExtractionException(@NonNull Set<Field<?>> fields,
      @NonNull String data, int maxRecordChars, int recordNo, int lineNo, int linePos,
      String invalidValue) {
    final CsvExtractor extractor = new CsvExtractor(',', fields, 0, maxRecordChars);
    final Stream<Record> recordStream = extractor.extract(new StringReader(data));
    final CsvExtractionException e =
        assertThrows(CsvExtractionException.class, () -> recordStreamConsume(recordStream));

    assertEquals(recordNo, e.getRecordNo());
    assertEquals(lineNo, e.getLineNo());
    assertEquals(linePos, e.getLinePos());
    assertEquals(invalidValue, e.getInvalidValue());

    final String s = e.toString();
    assertThat(s, allOf(containsString(String.valueOf(recordNo)),
        containsString(String.valueOf(lineNo)),
        containsString(String.valueOf(linePos))));
    if (invalidValue != null) {
      assertThat(s, containsString(invalidValue));
    }
  }

  private void recordStreamConsume(@NonNull Stream<Record> recordStream) {
    recordStream.forEach(r -> {
    });
  }
}