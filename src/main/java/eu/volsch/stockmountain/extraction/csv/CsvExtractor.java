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

import eu.volsch.stockmountain.extraction.api.ExtractionDataAccessException;
import eu.volsch.stockmountain.extraction.api.Extractor;
import eu.volsch.stockmountain.extraction.api.Field;
import eu.volsch.stockmountain.extraction.api.Record;
import eu.volsch.stockmountain.extraction.api.RecordMetaData;
import eu.volsch.stockmountain.extraction.api.SimpleImmutableRecord;
import eu.volsch.stockmountain.extraction.api.SimpleRecordMetaData;
import eu.volsch.stockmountain.extraction.conversion.ConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Extracts CSV character streams to record streams. The format of the records are fixed and all
 * fields and their columns (the {@linkplain Field#ordinal() ordinal number} of the
 * {@linkplain Field field}) must be known before extraction. If data is extracted from an
 * {@linkplain InputStream input stream}, ISO 8859-1 character set is used to read characters from
 * that stream. Malformed byte input or unmappable characters result in an exception.
 */
@Immutable
@ThreadSafe
public class CsvExtractor implements Extractor {

  private static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

  private static final char CR_CHAR = '\r';
  private static final char NL_CHAR = '\n';
  private static final char ENCLOSE_CHAR = '"';

  private final Charset charset;
  private final char fieldSeparator;
  private final @NonNegative int skipCount;
  private final @Positive int maxRecordsChars;
  private final @NonNull RecordMetaData recordMetaData;
  private final @NonNegative int fieldCount;
  private final Field<?>[] fields;

  /**
   * Constructs a new extractor. The extractor uses ISO 8859-1 character set when extracting data
   * from an {@linkplain InputStream input stream}.
   *
   * @param fieldSeparator  the field separator to be used (e.g. <code>,</code> or <code>;</code>).
   * @param fields          the fields to be used.
   * @param skipCount       the number of records to be skipped at the beginning (e.g. header).
   * @param maxRecordsChars the maximum number of allowed characters per record without throwing an
   *                        exception
   */
  public CsvExtractor(char fieldSeparator, @NonNull Set<Field<?>> fields,
      int skipCount, int maxRecordsChars) {
    this(fieldSeparator, fields, skipCount, maxRecordsChars, DEFAULT_CHARSET);
  }

  /**
   * Constructs a new extractor.
   *
   * @param fieldSeparator  the field separator to be used (e.g. <code>,</code> or <code>;</code>).
   * @param fields          the fields to be used.
   * @param skipCount       the number of records to be skipped at the beginning (e.g. header).
   * @param maxRecordsChars the maximum number of allowed characters per record without throwing an
   *                        exception
   * @param charset         the character set that is used to decode an
   *                        {@linkplain InputStream input stream}.
   */
  public CsvExtractor(char fieldSeparator, @NonNull Set<Field<?>> fields,
      int skipCount, int maxRecordsChars, @NonNull Charset charset) {
    if (fieldSeparator == NL_CHAR || fieldSeparator == CR_CHAR || fieldSeparator == ENCLOSE_CHAR) {
      throw new IllegalArgumentException("Field separator is invalid: " + fieldSeparator);
    }

    this.charset = charset;
    this.fieldSeparator = fieldSeparator;
    this.skipCount = skipCount;
    this.maxRecordsChars = maxRecordsChars;
    this.recordMetaData = new SimpleRecordMetaData(fields);

    this.fieldCount = recordMetaData.getMaxFieldOrdinal() + 1;
    this.fields = new Field[fieldCount];
    this.recordMetaData.fieldStream()
        .forEach(field -> this.fields[field.ordinal()] = field);
  }

  @Override
  public boolean readerSupported() {
    return true;
  }

  @Override
  public @NonNull Stream<Record> extract(@NonNull InputStream inputStream) {
    return extract(new InputStreamReader(inputStream, charset.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)));
  }

  /**
   * Extracts the CSV records from the specified reader to the returned stream. Stream operations
   * may throw a {@link ExtractionDataAccessException} if reading the characters from the underlying
   * stream fails or a {@link CsvExtractionException} if the CSV contains any invalid data.
   *
   * @param reader the reader from which the CSV should be read.
   * @return the stream with the resulting records.
   */
  public @NonNull Stream<Record> extract(@NonNull Reader reader) {
    return StreamSupport.stream(new CsvSpliterator(reader), false);
  }

  @NotThreadSafe
  private class CsvSpliterator extends AbstractSpliterator<Record> {

    private final StringBuilder value = new StringBuilder();
    private final @NonNull Reader reader;
    private final Object[] values;
    private boolean eof;
    private boolean finished;
    private boolean lastCrChar;
    private @NonNegative int skippedCount;
    private @NonNegative int lineNo;
    private @NonNegative int recordNo;

    public CsvSpliterator(@NonNull Reader reader) {
      super(Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.NONNULL);
      this.reader = reader;
      this.values = new Object[fieldCount];
    }

    @Override
    public boolean tryAdvance(@NonNull Consumer<@NonNull ? super Record> action) {
      try {
        if (skippedCount < skipCount && !skipRows()) {
          return false;
        }
        if (readRow(true)) {
          action.accept(new SimpleImmutableRecord(recordMetaData, values));
          return true;
        }
      } catch (IOException e) {
        throw new ExtractionDataAccessException("Error when reading input stream", e);
      }
      return false;
    }

    private boolean skipRows() throws IOException {
      while (skippedCount < skipCount) {
        if (readRow(false)) {
          skippedCount++;
        } else {
          return false;
        }
      }
      return true;
    }

    private boolean readRow(boolean data) throws IOException {
      boolean enclosed = false;
      boolean lastEncloseChar = false;
      int recordPos = 0;
      int linePos = 0;
      int fieldIndex = 0;
      int fieldPos = 0;
      int c;

      value.setLength(0);
      while (true) {
        c = read();
        if (c < 0) {
          return handleEof(data, enclosed, lastEncloseChar, linePos, fieldIndex);
        }
        incLineNo(recordPos, linePos);

        linePos++;
        fieldPos++;
        recordPos++;
        verifyMaxRecordChars(recordPos, linePos, fieldIndex);

        if (isEol(c)) {
          switch (handleEol(data, enclosed, lastEncloseChar, linePos, fieldIndex, c)) {
            case CONTINUE:
              lastEncloseChar = false;
              linePos = 0;
              break;
            case RESET:
              linePos = 0;
              fieldPos = 0;
              recordPos = 0;
              break;
            default:
              return true;
          }
        } else if (isEncloseDelimiter(enclosed, fieldPos, c)) {
          lastCrChar = false;
          if (fieldPos == 1) {
            enclosed = true;
          }
          lastEncloseChar = handleEnclose(lastEncloseChar, fieldPos);
        } else if (isFieldSeparator(enclosed, lastEncloseChar, c)) {
          appendFieldValue(data, linePos, fieldIndex);
          lastCrChar = false;
          enclosed = false;
          lastEncloseChar = false;
          fieldIndex++;
          fieldPos = 0;
        } else {
          verifyEnclosedEncloseChar(enclosed, lastEncloseChar, linePos, fieldIndex);
          value.append((char) c);
          lastCrChar = false;
          lastEncloseChar = false;
        }
      }
    }

    private void incLineNo(@NonNegative int recordPos, @NonNegative int linePos) {
      if (linePos == 0) {
        if (recordPos == 0) {
          recordNo++;
        }
        lineNo++;
      }
    }

    private boolean isEol(@NonNegative int c) {
      return c == CR_CHAR || c == NL_CHAR;
    }

    private boolean isEncloseDelimiter(boolean enclosed, @Positive int fieldPos,
        @NonNegative int c) {
      return c == ENCLOSE_CHAR && (enclosed || fieldPos == 1);
    }

    private boolean isFieldSeparator(boolean enclosed, boolean lastEncloseChar, int c) {
      return c == fieldSeparator && (!enclosed || lastEncloseChar);
    }

    private boolean handleEnclose(boolean lastEncloseChar, @Positive int fieldPos) {
      if (fieldPos == 1) {
        lastEncloseChar = false;
      } else if (lastEncloseChar) {
        value.append(ENCLOSE_CHAR);
        lastEncloseChar = false;
      } else {
        lastEncloseChar = true;
      }
      return lastEncloseChar;
    }

    private @NonNull EolAction handleEol(boolean data, boolean enclosed, boolean lastEncloseChar,
        @Positive int linePos, @NonNegative int fieldIndex, @NonNegative int c) {
      if (enclosed && !lastEncloseChar) {
        if (!lastCrChar || c == CR_CHAR) {
          value.append(NL_CHAR);
        }
        lastCrChar = c == CR_CHAR;
        return EolAction.CONTINUE;
      }

      if (lastCrChar && c == NL_CHAR) {
        value.setLength(0);
        lastCrChar = false;
        return EolAction.RESET;
      }

      lastCrChar = c == CR_CHAR;
      if (isEmptyLine(fieldIndex)) {
        finished = true;
        return EolAction.RESET;
      }

      verifyMinFieldCount(linePos, fieldIndex);
      appendFieldValue(data, linePos, fieldIndex);
      return EolAction.RETURN;
    }

    private boolean isEmptyLine(@NonNegative int fieldIndex) {
      return fieldIndex == 0 && value.toString().isBlank();
    }

    private boolean handleEof(boolean data, boolean enclosed, boolean lastEncloseChar,
        @NonNegative int linePos, @NonNegative int fieldIndex) {
      if (isEmptyLine(fieldIndex)) {
        return false;
      }
      verifyEncloseEnd(enclosed, lastEncloseChar, linePos, fieldIndex);
      verifyMinFieldCount(linePos, fieldIndex);
      appendFieldValue(data, linePos, fieldIndex);
      return true;
    }

    private void verifyMaxRecordChars(@Positive int recordPos, @Positive int linePos,
        @NonNegative int fieldIndex) {
      if (recordPos > maxRecordsChars) {
        throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, null,
            "Record exceeds " + maxRecordsChars + " characters");
      }
    }

    private void verifyEnclosedEncloseChar(boolean enclosed, boolean lastEncloseChar,
        @Positive int linePos, @NonNegative int fieldIndex) {
      if (enclosed && lastEncloseChar) {
        throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, null,
            "Field has not been enclosed properly");
      }
    }

    private void verifyEncloseEnd(boolean enclosed, boolean lastEncloseChar, @Positive int linePos,
        @NonNegative int fieldIndex) {
      if (enclosed && !lastEncloseChar) {
        throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, null,
            "Field has not been enclosed properly");
      }
    }

    private void verifyMinFieldCount(@Positive int linePos, @NonNegative int fieldIndex) {
      if (finished) {
        throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, null,
            "No more data expected in line " + lineNo);
      }
      if (fieldIndex + 1 < fieldCount) {
        throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, null,
            "Record contains " + (fieldIndex + 1) + " instead of "
                + fieldCount + " fields");
      }
    }

    private void appendFieldValue(boolean data,
        @Positive int linePos, @NonNegative int fieldIndex) {
      if (data && fieldIndex < fieldCount) {
        final Field<?> field = fields[fieldIndex];
        if (field != null) {
          final String stringValue = value.toString();
          final Object resultingValue;
          try {
            resultingValue = field.convert(stringValue);
          } catch (ConversionException e) {
            throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, stringValue,
                "Field " + (fieldIndex + 1) + " in record " + recordNo + " contains invalid value: "
                    + stringValue);
          }
          if (resultingValue == null && !field.nullable()) {
            throw new CsvExtractionException(recordNo, lineNo, fieldIndex + 1, linePos, stringValue,
                "Non-nullable field " + (fieldIndex + 1) + " in record " + recordNo
                    + " contains null value");
          }
          values[fieldIndex] = resultingValue;
        }
      }
      value.setLength(0);
    }

    private int read() throws IOException {
      if (eof) {
        return -1;
      }
      final int c = reader.read();
      if (c < 0) {
        eof = true;
      }
      return c;
    }
  }

  private enum EolAction {
    RETURN, CONTINUE, RESET
  }
}
