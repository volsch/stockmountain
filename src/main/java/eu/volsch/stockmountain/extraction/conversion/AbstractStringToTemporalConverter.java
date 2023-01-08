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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Implementations converts a string to a {@linkplain Temporal temporal} based on the specified
 * formatter.
 *
 * @param <T> the concrete type of the temporal.
 */
public abstract class AbstractStringToTemporalConverter<T extends Temporal>
    extends AbstractStringConverter<T> {

  private final @NonNull DateTimeFormatter formatter;

  protected AbstractStringToTemporalConverter(@NonNull DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  protected final @NonNull T doConvert(@NonNull String source) throws ConversionException {
    try {
      return parse(source, formatter);
    } catch (DateTimeParseException e) {
      throw new ConversionException("Value cannot be converted to a temporal with format \""
          + formatter + "\": " + source);
    }
  }

  /**
   * Converts the specified source vale to a temporal based on the formatter used by this
   * converter.
   *
   * @param source    the source value.
   * @param formatter the formatter to be used for parsing.
   * @return the converted source value.
   * @throws DateTimeParseException thrown if the source value cannot be converted to the temporal
   *                                value.
   * @throws ConversionException    thrown if conversion failed due to any other reason than the
   *                                format of this converter.
   */
  protected abstract @NonNull T parse(@NonNull String source, @NonNull DateTimeFormatter formatter)
      throws DateTimeParseException, ConversionException;
}
