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

import java.math.BigDecimal;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converts a string into a decimal value.
 */
@Immutable
@ThreadSafe
public class StringToBigDecimalConvertor extends AbstractStringConverter<BigDecimal> {

  public static final StringToBigDecimalConvertor DECIMAL_POINT_INSTANCE =
      new StringToBigDecimalConvertor('.', ',');

  public static final StringToBigDecimalConvertor DECIMAL_COMMA_INSTANCE =
      new StringToBigDecimalConvertor(',', '.');

  private final char decimalSeparator;
  private final char thousandSeparator;

  private StringToBigDecimalConvertor(char decimalSeparator, char thousandSeparator) {
    this.decimalSeparator = decimalSeparator;
    this.thousandSeparator = thousandSeparator;
  }

  @Override
  public @NonNull Class<BigDecimal> getTargetType() {
    return BigDecimal.class;
  }

  @Override
  public @Nullable BigDecimal doConvert(@NonNull String source) throws ConversionException {
    final char[] value = source.toCharArray();
    final int len = removeThousandSeparator(source, value, value.length);
    replaceCharacters(source, value, len);
    return new BigDecimal(value, 0, len);
  }

  private void replaceCharacters(@NonNull String source, char[] value, @NonNegative int len)
      throws ConversionException {
    final boolean replaceDecimalSeparator = decimalSeparator != '.';
    boolean replacedDecimalSeparator = false;
    for (int i = 0; i < len; i++) {
      final char c = value[i];
      if (c == decimalSeparator) {
        if (replaceDecimalSeparator) {
          if (replacedDecimalSeparator) {
            throw new ConversionException("Value contains invalid decimal separator: " + source);
          }
          value[i] = '.';
          replacedDecimalSeparator = true;
        }
      } else if (isNonDigitChar(c) && !isSigned(i, c)) {
        throw new ConversionException("Value contains invalid characters: " + source);
      }
    }
  }

  private static boolean isNonDigitChar(char c) {
    return c < '0' || c > '9';
  }

  private @NonNegative int removeThousandSeparator(
      @NonNull String source, char[] value, @NonNegative int len) throws ConversionException {
    int lastIndex = -1;
    boolean signed = false;
    boolean removed;
    for (int i = 0; i < len; i = removed ? i : i + 1) {
      final char c = value[i];
      if (c == decimalSeparator) {
        validateLastThousandSeparator(source, signed, lastIndex, i, true);
        return len;
      }

      removed = false;
      if (isSigned(i, c)) {
        signed = true;
      } else if (c == thousandSeparator || c == ' ') {
        validateLastThousandSeparator(source, signed, lastIndex, i, false);
        if (--len <= i) {
          throw new ConversionException("Value contains invalid thousand separator: " + source);
        }

        System.arraycopy(value, i + 1, value, i, len - i);
        lastIndex = i;
        signed = false;
        removed = true;
      }
    }
    validateLastThousandSeparator(source, signed, lastIndex, len, true);
    return len;
  }

  private static boolean isSigned(int index, char c) {
    return index == 0 && (c == '+' || c == '-');
  }

  private static void validateLastThousandSeparator(@NonNull String source, boolean signed,
      int lastIndex, @NonNegative int i, boolean ignoreMissing)
      throws ConversionException {
    if ((lastIndex < 0 && !ignoreMissing && (i == (signed ? 1 : 0) || i > (signed ? 4 : 3)))
        || (lastIndex > 0 && i - lastIndex != 3)) {
      throw new ConversionException("Value contains invalid thousand separator: " + source);
    }
  }
}
