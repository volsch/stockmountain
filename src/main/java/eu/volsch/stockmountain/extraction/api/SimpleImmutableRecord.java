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

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple immutable implementation of a {@linkplain Record} that is backed by an array.
 */
@Immutable
@ThreadSafe
public final class SimpleImmutableRecord implements Record {

  private final @NonNull RecordMetaData metaData;
  private final @NonNull Object[] values;

  /**
   * Create a simple immutable record. <b>It is required that each passed value is immutable as
   * well.</b> Changes to the items of the passed value array that are performed after the
   * construction of this record, will not affect this record anymore. The ordinal of the field is
   * used as array index of the value. The type of the values must match the type of the fields.
   *
   * @param metaData the metadata of this record.
   * @param values   the concrete values of this record.
   */
  public SimpleImmutableRecord(@NonNull RecordMetaData metaData, Object... values) {
    if (values.length <= metaData.getMaxFieldOrdinal()) {
      throw new IllegalArgumentException("Record must contain at least "
          + (metaData.getMaxFieldOrdinal() + 1) + " values");
    }
    this.metaData = metaData;
    this.values = values.clone();
  }

  @Override
  public @NonNull RecordMetaData getMetaData() {
    return metaData;
  }

  @Override
  public <T> @Nullable T getValue(@NonNull Field<T> field) throws IllegalArgumentException {
    if (!metaData.containsField(field)) {
      throw new IllegalArgumentException("Field is not included in record: " + field.name());
    }
    return field.cast(values[field.ordinal()]);
  }
}
