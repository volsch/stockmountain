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

import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Converts a string to a trimmed currency string and converts blank strings to <code>null</code>.
 * If the currency does not contain exactly three upper-case US-ASCII letters,
 * {@link ConversionException} is thrown.
 */
public class StringToCurrencyConverter extends AbstractStringConverter<String> {

  public static final StringToCurrencyConverter INSTANCE = new StringToCurrencyConverter();

  private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");

  private StringToCurrencyConverter() {
  }

  @Override
  public @NonNull Class<String> getTargetType() {
    return String.class;
  }

  @Override
  protected String doConvert(@NonNull String source) throws ConversionException {
    if (!CURRENCY_PATTERN.matcher(source).matches()) {
      throw new ConversionException("Invalid currency: " + source);
    }
    return source;
  }
}
