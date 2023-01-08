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

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.volsch.stockmountain.extraction.conversion.ConversionException;
import eu.volsch.stockmountain.extraction.conversion.Converter;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.interning.qual.EqualsMethod;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple implementation of a {@linkplain Field}.
 *
 * @param <T> the concrete type of the value of this field.
 */
@Immutable
@ThreadSafe
public class SimpleField<T> implements Field<T> {

  private final @NonNull String name;
  private final @NonNegative int ordinal;
  private final @NonNull Class<T> type;
  private final boolean nullable;
  public final @Nullable Converter<?, T> converter;
  @SuppressFBWarnings("JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS")
  private int cachedHashCode;

  /**
   * Creates a simple field. The created field is nullable.
   *
   * @param name      the unique symbolic name of this field.
   * @param ordinal   the zero-based ordinal number of this field.
   * @param type      the type of the value of this field.
   * @param converter the converter that converts a value to the type of this field.
   */
  public SimpleField(@NonNull String name, @NonNegative int ordinal,
      @NonNull Class<T> type, @Nullable Converter<?, T> converter) {
    this(name, ordinal, type, true, converter);
  }

  /**
   * Creates a simple field.
   *
   * @param name      the unique symbolic name of this field.
   * @param ordinal   the zero-based ordinal number of this field.
   * @param type      the type of the value of this field.
   * @param nullable  specifies if this field is nullable.
   * @param converter the converter that converts a value to the type of this field.
   */
  public SimpleField(@NonNull String name, int ordinal,
      @NonNull Class<T> type, boolean nullable, @Nullable Converter<?, T> converter) {
    this.name = requireNonNull(name);
    this.ordinal = ordinal;
    this.type = requireNonNull(type);
    this.nullable = nullable;
    this.converter = converter;
  }

  @Override
  public final @NonNull String name() {
    return name;
  }

  @Override
  public final @NonNegative int ordinal() {
    return ordinal;
  }

  @Override
  public boolean nullable() {
    return nullable;
  }

  @Override
  public final @NonNull Class<T> type() {
    return type;
  }

  @Override
  public @Nullable T convert(@Nullable Object value)
      throws ConversionException, ClassCastException {
    if (converter == null) {
      return Field.super.convert(value);
    }
    return converter.castAndConvert(value);
  }

  @Override
  @EqualsMethod
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SimpleField<?> that = (SimpleField<?>) o;
    return name.equals(that.name) && ordinal == that.ordinal && type.equals(that.type)
        && nullable == that.nullable;
  }

  @Override
  public final int hashCode() {
    if (cachedHashCode == 0) {
      cachedHashCode = calcHashCode();
    }
    return cachedHashCode;
  }

  private int calcHashCode() {
    return Objects.hash(name, ordinal, type, nullable);
  }

  public final @NonNull String toString() {
    return name;
  }
}
