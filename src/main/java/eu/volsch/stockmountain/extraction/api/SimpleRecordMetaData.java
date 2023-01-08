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

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of a {@linkplain RecordMetaData}.
 */
@Immutable
@ThreadSafe
public class SimpleRecordMetaData implements RecordMetaData {

  private final @NonNull Set<@NonNull Field<?>> fieldSet;
  private final @NonNull List<@NonNull Field<?>> fieldList;
  private final int maxFieldOrdinal;

  /**
   * Creates simple record metadata with the specified fields. The specified set must include at
   * least one field.
   *
   * @param fields the fields that are included in the record.
   * @throws IllegalArgumentException thrown if the specified set does contain no fields.
   */
  public SimpleRecordMetaData(@NonNull Set<@NonNull Field<?>> fields)
      throws IllegalArgumentException {
    this.fieldSet = Set.copyOf(fields);
    this.fieldList = this.fieldSet.stream()
        .sorted(comparingInt(Field::ordinal))
        .collect(toUnmodifiableList());
    this.maxFieldOrdinal = this.fieldList.stream()
        .mapToInt(Field::ordinal)
        .max()
        .orElseThrow(() -> new IllegalArgumentException("At least one field must be specified"));
  }

  @Override
  public @NonNull Stream<@NonNull Field<?>> fieldStream() {
    return fieldList.stream();
  }

  @Override
  public boolean containsField(@NonNull Field<?> field) {
    return fieldSet.contains(field);
  }

  @Override
  public @NonNegative int getMaxFieldOrdinal() {
    return maxFieldOrdinal;
  }

  @Override
  public @NonNegative int getFieldCount() {
    return fieldList.size();
  }

  @Override
  public @NonNull Field<?> getField(@NonNegative int index) throws IllegalArgumentException {
    try {
      return fieldList.get(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
