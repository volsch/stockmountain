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

import eu.volsch.stockmountain.extraction.api.ExtractionException;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Thrown if the content of the CSV is invalid. Either the format is not correct or the values have
 * not the expected type.
 */
public class CsvExtractionException extends ExtractionException {

  private final @Positive int recordNo;
  private final @Positive int lineNo;
  private final @Positive int fieldNo;
  private final @Positive int linePos;
  private final @Nullable String invalidValue;

  /**
   * Constructs the exception.
   *
   * @param recordNo     the one-based record number that caused the issue.
   * @param lineNo       the one-based line number of the file that caused the issue.
   * @param fieldNo      the one-based record field number that caused the issue.
   * @param linePos      the one-based position within the line that caused the issue.
   * @param invalidValue the invalid field value that caused the issue (can be <code>null</code>).
   * @param message      the message that describes the exact issue.
   */
  public CsvExtractionException(@Positive int recordNo, @Positive int lineNo, @Positive int fieldNo,
      @Positive int linePos, @Nullable String invalidValue, @NonNull String message) {
    super(message);
    this.recordNo = recordNo;
    this.lineNo = lineNo;
    this.fieldNo = fieldNo;
    this.linePos = linePos;
    this.invalidValue = invalidValue;
  }

  public @Positive int getRecordNo() {
    return recordNo;
  }

  public @Positive int getLineNo() {
    return lineNo;
  }

  public @Positive int getLinePos() {
    return linePos;
  }

  public @Nullable String getInvalidValue() {
    return invalidValue;
  }

  @Override
  public @NonNull String toString() {
    final StringBuilder sb = new StringBuilder(getClass().getName())
        .append(": at record number ").append(recordNo)
        .append(" at line number ").append(lineNo)
        .append(" at field number ").append(fieldNo)
        .append(" at line position ").append(linePos);
    if (invalidValue != null) {
      sb.append(" with invalid value \"").append(invalidValue).append('"');
    }
    sb.append(" : ").append(getLocalizedMessage());
    return sb.toString();
  }
}
