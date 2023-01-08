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

package eu.volsch.stockmountain.extraction.api;

import eu.volsch.stockmountain.extraction.conversion.ConversionException;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The definition of a field of a record.
 *
 * @param <T> the concrete type of the value of this field.
 */
@Immutable
@ThreadSafe
public interface Field<T> {

  /**
   * Returns the unique symbolic name of this field. The symbolic names must be unique for all
   * fields of a record.
   *
   * @return the unique symbolic name of this field.
   */
  @NonNull String name();

  /**
   * Returns the unique ordinal number of this field. The ordinal number must be unique for all
   * fields of a record. The lowest ordinal number is <code>0</code> and ordinal numbers should be
   * as small as possible.
   *
   * @return the unique ordinal number of this field.
   */
  @NonNegative int ordinal();

  /**
   * Returns the type of the value of this field.
   *
   * @return the type of the value of this field.
   */
  @NonNull Class<T> type();

  /**
   * Returns if this field is nullable. The default implementation return <code>true</code>.
   *
   * @return if this field is nullable.
   */
  default boolean nullable() {
    return true;
  }

  /**
   * Casts the specified value to the type of this field. No conversion will be made.
   *
   * @param value the value that should be cast to the type of this field.
   * @return the cast value (maybe <code>null</code> if value is <code>null</code>).
   * @throws ClassCastException thrown if specified value is non-null and is not an instance of the
   *                            type of this field.
   */
  default @Nullable T cast(@Nullable Object value)
      throws ClassCastException {
    return type().cast(value);
  }

  /**
   * Converts the specified value to the {@linkplain #type() type} of this field. If the conversion
   * is not supported, a {@linkplain ClassCastException} may be thrown.<br> The default
   * implementation of this method just casts the specified value to the {@linkplain #type() type}
   * of this field.
   *
   * @param value the value that should be converted to the type of this field.
   * @return the converted value.
   * @throws ConversionException thrown if the specified value cannot be converted.
   * @throws ClassCastException  thrown if the conversion is not supported due to the type of
   *                             specified value.
   */
  default @Nullable T convert(@Nullable Object value)
      throws ConversionException, ClassCastException {
    return type().cast(value);
  }

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();
}
